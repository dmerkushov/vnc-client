/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import ru.dmerkushov.vnc.client.VncCommon;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBytes;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU8;

/**
 *
 * @author dmerkushov
 */
public class RfbPixelFormat {

	private int bitsPerPixel;
	private int depth;
	private boolean bigEndian;
	private boolean trueColor;
	private int redMax;
	private int greenMax;
	private int blueMax;
	private int redShift;
	private int greenShift;
	private int blueShift;

	public RfbPixelFormat () {
	}

	public RfbPixelFormat (int bitsPerPixel, int depth, boolean bigEndian, boolean trueColor, int redMax, int greenMax, int blueMax, int redShift, int greenShift, int blueShift) {
		this.bitsPerPixel = bitsPerPixel;
		this.depth = depth;
		this.bigEndian = bigEndian;
		this.trueColor = trueColor;
		this.redMax = redMax;
		this.greenMax = greenMax;
		this.blueMax = blueMax;
		this.redShift = redShift;
		this.greenShift = greenShift;
		this.blueShift = blueShift;
	}

	private static RfbPixelFormat defaultPixelFormat;

	public static RfbPixelFormat getDefaultPixelFormat () {
		if (defaultPixelFormat == null) {
//			defaultPixelFormat = new RfbPixelFormat (32, 24, true, true, 0xFF, 0xFF, 0xFF, 16, 8, 0);
			defaultPixelFormat = new RfbPixelFormat (32, 24, true, true, 0xFF, 0xFF, 0xFF, 24, 16, 8);
		}
		return defaultPixelFormat;
	}

	public void read (InputStream in) throws IOException {
		byte[] bytes = readBytes (in, 16);

		if (VncCommon.getLogger ().isLoggable (Level.INFO)) {
			StringBuilder logMsgBuilder = new StringBuilder ();

			logMsgBuilder.append ("Read PixelFormat:");
			for (int i = 0; i < 16; i++) {
				logMsgBuilder.append (String.format (" %x", bytes[i]));
			}
			VncCommon.getLogger ().info (logMsgBuilder.toString ());
		}

		ByteArrayInputStream bais = new ByteArrayInputStream (bytes);

		bitsPerPixel = readU8 (bais);
		depth = readU8 (bais);
		bigEndian = readBoolean (bais);
		trueColor = readBoolean (bais);
		redMax = readU16 (bais, true);
		greenMax = readU16 (bais, true);
		blueMax = readU16 (bais, true);
		redShift = readU8 (bais);
		greenShift = readU8 (bais);
		blueShift = readU8 (bais);
		readBytes (bais, 3);		// Padding
	}

	public void write (OutputStream out) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream (16);

		writeU8 (baos, bitsPerPixel);
		writeU8 (baos, depth);
		writeBoolean (baos, bigEndian);
		writeBoolean (baos, trueColor);
		writeU16 (baos, redMax, true);
		writeU16 (baos, greenMax, true);
		writeU16 (baos, blueMax, true);
		writeU8 (baos, redShift);
		writeU8 (baos, greenShift);
		writeU8 (baos, blueShift);
		writeU8 (baos, 0);	// Padding
		writeU8 (baos, 0);	//
		writeU8 (baos, 0);	//

		byte[] bytes = baos.toByteArray ();

		out.write (bytes);

		if (VncCommon.getLogger ().isLoggable (Level.INFO)) {
			StringBuilder logMsgBuilder = new StringBuilder ();

			logMsgBuilder.append ("Wrote PixelFormat:");
			for (int i = 0; i < 16; i++) {
				logMsgBuilder.append (String.format (" %x", bytes[i]));
			}
			VncCommon.getLogger ().info (logMsgBuilder.toString ());
		}
	}

	private int normalizeOrder (int value) {
		if (!bigEndian) {
			ByteBuffer bb = ByteBuffer.allocate (Integer.BYTES);
			bb.order (ByteOrder.LITTLE_ENDIAN);
			bb.putInt (value);
			bb.flip ();
			bb.order (ByteOrder.BIG_ENDIAN);
			value = bb.getInt ();
		}
		return value;
	}

	public int getRed (int value) {
		value = (normalizeOrder (value) >> redShift);
		return (value & redMax);
	}

	public int getGreen (int value) {
		value = (normalizeOrder (value) >> greenShift);
		return (value & greenMax);
	}

	public int getBlue (int value) {
		value = (normalizeOrder (value) >> blueShift);
		return (value & blueMax);
	}

	public int getBitsPerPixel () {
		return bitsPerPixel;
	}

	public void setBitsPerPixel (int bitsPerPixel) {
		this.bitsPerPixel = bitsPerPixel;
	}

	public int getDepth () {
		return depth;
	}

	public void setDepth (int depth) {
		this.depth = depth;
	}

	public boolean isBigEndian () {
		return bigEndian;
	}

	public void setBigEndian (boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public boolean isTrueColor () {
		return trueColor;
	}

	public void setTrueColor (boolean trueColor) {
		this.trueColor = trueColor;
	}

	public int getRedMax () {
		return redMax;
	}

	public void setRedMax (int redMax) {
		this.redMax = redMax;
	}

	public int getGreenMax () {
		return greenMax;
	}

	public void setGreenMax (int greenMax) {
		this.greenMax = greenMax;
	}

	public int getBlueMax () {
		return blueMax;
	}

	public void setBlueMax (int blueMax) {
		this.blueMax = blueMax;
	}

	public int getRedShift () {
		return redShift;
	}

	public void setRedShift (int redShift) {
		this.redShift = redShift;
	}

	public int getGreenShift () {
		return greenShift;
	}

	public void setGreenShift (int greenShift) {
		this.greenShift = greenShift;
	}

	public int getBlueShift () {
		return blueShift;
	}

	public void setBlueShift (int blueShift) {
		this.blueShift = blueShift;
	}

}
