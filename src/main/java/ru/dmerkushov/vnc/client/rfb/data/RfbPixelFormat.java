/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data;

import ru.dmerkushov.vnc.client.VncCommon;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.logging.Level;

import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.*;

/**
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
		if (ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat.defaultPixelFormat == null) {
//			defaultPixelFormat = new RfbPixelFormat (32, 24, true, true, 0xFF, 0xFF, 0xFF, 16, 8, 0);
			ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat.defaultPixelFormat = new RfbPixelFormat (32, 24, true, true, 0xFF, 0xFF, 0xFF, 24, 16, 8);
		}
		return ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat.defaultPixelFormat;
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

		this.bitsPerPixel = readU8 (bais);
		this.depth = readU8 (bais);
		this.bigEndian = readBoolean (bais);
		this.trueColor = readBoolean (bais);
		this.redMax = readU16 (bais, true);
		this.greenMax = readU16 (bais, true);
		this.blueMax = readU16 (bais, true);
		this.redShift = readU8 (bais);
		this.greenShift = readU8 (bais);
		this.blueShift = readU8 (bais);
		readBytes (bais, 3);        // Padding
	}

	public void write (OutputStream out) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream (16);

		writeU8 (baos, this.bitsPerPixel);
		writeU8 (baos, this.depth);
		writeBoolean (baos, this.bigEndian);
		writeBoolean (baos, this.trueColor);
		writeU16 (baos, this.redMax, true);
		writeU16 (baos, this.greenMax, true);
		writeU16 (baos, this.blueMax, true);
		writeU8 (baos, this.redShift);
		writeU8 (baos, this.greenShift);
		writeU8 (baos, this.blueShift);
		writeU8 (baos, 0);    // Padding
		writeU8 (baos, 0);    //
		writeU8 (baos, 0);    //

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
		if (!this.bigEndian) {
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
		value = (this.normalizeOrder (value) >> this.redShift);
		return (value & this.redMax);
	}

	public int getGreen (int value) {
		value = (this.normalizeOrder (value) >> this.greenShift);
		return (value & this.greenMax);
	}

	public int getBlue (int value) {
		value = (this.normalizeOrder (value) >> this.blueShift);
		return (value & this.blueMax);
	}

	public int getBitsPerPixel () {
		return this.bitsPerPixel;
	}

	public void setBitsPerPixel (int bitsPerPixel) {
		this.bitsPerPixel = bitsPerPixel;
	}

	public int getDepth () {
		return this.depth;
	}

	public void setDepth (int depth) {
		this.depth = depth;
	}

	public boolean isBigEndian () {
		return this.bigEndian;
	}

	public void setBigEndian (boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public boolean isTrueColor () {
		return this.trueColor;
	}

	public void setTrueColor (boolean trueColor) {
		this.trueColor = trueColor;
	}

	public int getRedMax () {
		return this.redMax;
	}

	public void setRedMax (int redMax) {
		this.redMax = redMax;
	}

	public int getGreenMax () {
		return this.greenMax;
	}

	public void setGreenMax (int greenMax) {
		this.greenMax = greenMax;
	}

	public int getBlueMax () {
		return this.blueMax;
	}

	public void setBlueMax (int blueMax) {
		this.blueMax = blueMax;
	}

	public int getRedShift () {
		return this.redShift;
	}

	public void setRedShift (int redShift) {
		this.redShift = redShift;
	}

	public int getGreenShift () {
		return this.greenShift;
	}

	public void setGreenShift (int greenShift) {
		this.greenShift = greenShift;
	}

	public int getBlueShift () {
		return this.blueShift;
	}

	public void setBlueShift (int blueShift) {
		this.blueShift = blueShift;
	}

	public int[] readArgbPixels (InputStream in, int length) throws IOException {
		Objects.requireNonNull (in, "in");

		int[] innerPixels = new int[length];

		int bytesPerPixel = this.getBitsPerPixel () / 8;

		int bytesCount = length * bytesPerPixel;
		DataInputStream dis = new DataInputStream (in);
		byte[] bytes = new byte[bytesCount];
		dis.readFully (bytes);

		if ((this.bitsPerPixel != 32) || (!this.trueColor)) {
			throw new IllegalStateException ("Unsupported pixel format");
		}

		ByteBuffer bb = ByteBuffer.wrap (bytes);
		bb.order (this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

		int pixel;
		int red;
		int green;
		int blue;
		int innerPixel;

		for (int innerPixelIndex = 0; innerPixelIndex < innerPixels.length; innerPixelIndex++) {
			pixel = bb.getInt ();

			red = (((pixel >> this.redShift) & this.redMax) * 0xFF / this.redMax) & 0xFF;
			green = (((pixel >> this.greenShift) & this.greenMax) * 0xFF / this.greenMax) & 0xFF;
			blue = (((pixel >> this.blueShift) & this.blueMax) * 0xFF / this.blueMax) & 0xFF;

			innerPixel = (0xFF << 24) | (red << 16) | (green << 8) | blue;

			innerPixels[innerPixelIndex] = innerPixel;
		}

		return innerPixels;
	}

	public BufferedImage readArgbImage (int width, int height, InputStream in) throws IOException {
		BufferedImage innerImage;
		if (width > 0 && height > 0) {
			innerImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
			int[] innerPixels = this.readArgbPixels (in, width * height);
			innerImage.setRGB (0, 0, width, height, innerPixels, 0, width);
		} else {
			innerImage = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB);
			innerImage.setRGB (0, 0, 0);
		}
		return innerImage;
	}

	public int[] readArgbCompressedPixels (InputStream in, int length) throws IOException {
		Objects.requireNonNull (in, "in");

		if (!this.mayApplyTightPixel ()) {
			return this.readArgbPixels (in, length);
		}

		if ((this.bitsPerPixel != 32) || (!this.trueColor)) {
			throw new IllegalStateException ("Unsupported actual pixel format for readArgbCompressedPixels()");
		}

		int additionalShift = this.holdAllIn3FirstBytes () ? 8 : 0;

		int bytesPerPixel = 3;
		int bytesLength = bytesPerPixel * length;

		DataInputStream dis = new DataInputStream (in);
		byte[] bytes = new byte[bytesLength];
		dis.readFully (bytes);

		ByteArrayInputStream compressedBais = new ByteArrayInputStream (bytes);

		int[] argbPixels = new int[length];
		int red;
		int green;
		int blue;
		int argbPixel;

		for (int argbIndex = 0; argbIndex < length; argbIndex++) {
			int pixel = compressedBais.read ();
			pixel <<= 8;
			pixel |= compressedBais.read ();
			pixel <<= 8;
			pixel |= compressedBais.read ();
			pixel <<= additionalShift;

			red = (((pixel >> this.redShift) & this.redMax) * 0xFF / this.redMax) & 0xFF;
			green = (((pixel >> this.greenShift) & this.greenMax) * 0xFF / this.greenMax) & 0xFF;
			blue = (((pixel >> this.blueShift) & this.blueMax) * 0xFF / this.blueMax) & 0xFF;

			argbPixel = (0xFF << 24) | (red << 16) | (green << 8) | blue;

			argbPixels[argbIndex] = argbPixel;
		}

		return argbPixels;
	}

	public BufferedImage readArgbCompressedImage (int width, int height, InputStream in) throws IOException {
		BufferedImage innerImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
		int[] innerPixels = this.readArgbCompressedPixels (in, width * height);
		innerImage.setRGB (0, 0, width, height, innerPixels, 0, width);
		return innerImage;
	}

	public boolean mayApplyCompressedPixel () {
		if (!this.trueColor) {
			return false;
		}
		if (this.bitsPerPixel != 32) {
			return false;
		}
		if (this.depth > 24) {
			return false;
		}

		return this.holdAllIn3LatterBytes () || this.holdAllIn3FirstBytes ();
	}

	public int[] readArgbTightPixels (InputStream in, int length) throws IOException {
		Objects.requireNonNull (in, "in");

		if (!this.mayApplyTightPixel ()) {
			return this.readArgbPixels (in, length);
		}

		int bytesPerPixel = 3;
		int bytesLength = bytesPerPixel * length;

		DataInputStream dis = new DataInputStream (in);
		byte[] bytes = new byte[bytesLength];
		dis.readFully (bytes);

		ByteArrayInputStream tightBais = new ByteArrayInputStream (bytes);

		int[] argbPixels = new int[length];

		for (int argbIndex = 0; argbIndex < length; argbIndex++) {
			int red = tightBais.read ();
			int green = tightBais.read ();
			int blue = tightBais.read ();

			argbPixels[argbIndex] = ((red * this.getRedMax () / 255) << this.getRedShift ()) |
					((green * this.getGreenMax () / 255) << this.getGreenShift ()) |
					((blue * this.getBlueMax () / 255) << this.getBlueShift ());
		}

		return argbPixels;
	}

	public BufferedImage readArgbTightImage (int width, int height, InputStream in) throws IOException {
		BufferedImage innerImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
		int[] innerPixels = this.readArgbTightPixels (in, width * height);
		innerImage.setRGB (0, 0, width, height, innerPixels, 0, width);
		return innerImage;
	}

	public boolean mayApplyTightPixel () {
		if (!this.trueColor) {
			return false;
		}
		if (this.bitsPerPixel != 32) {
			return false;
		}
		if (this.depth > 24) {
			return false;
		}

		return true;
	}

	public int getMaxColor () {
		return ((this.redMax << this.redShift) | (this.greenMax << this.greenShift) | (this.blueMax << this.blueShift));
	}

	public boolean holdAllIn3LatterBytes () {
		return (this.getMaxColor () | 0x00FFFFFF) == 0x00FFFFFF;
	}

	public boolean holdAllIn3FirstBytes () {
		return (this.getMaxColor () | 0xFFFFFF00) == 0xFFFFFF00;
	}

	public int createPixel (int red, int green, int blue) {
		return ((red & this.redMax) << this.redShift) |
				((green & this.greenMax) << this.greenShift) |
				((blue & this.blueMax) << this.blueShift);
	}

}
