/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data;

import ru.dmerkushov.vnc.client.rfb.data.pixeldata.*;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.*;

/**
 * @author dmerkushov
 */
public class RfbRectangle {

	public final RfbPixelFormat pixelFormat;

	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private int encodingType;
	private RfbPixelData pixelData;
	private RfbClientSession session;

	public RfbRectangle (RfbPixelFormat pixelFormat, RfbClientSession session) {
		Objects.requireNonNull (pixelFormat, "pixelFormat");
		Objects.requireNonNull (session, "session");

		this.pixelFormat = pixelFormat;
		this.session = session;
	}

	public void read (InputStream in) throws MessageException, IOException {
		this.xPosition = readU16 (in, true);
		this.yPosition = readU16 (in, true);
		this.width = readU16 (in, true);
		this.height = readU16 (in, true);

		this.encodingType = readS32 (in, true);

		switch (this.encodingType) {
			case RfbPixelData.ENCODINGTYPE_RAW:
				this.pixelData = new RfbRawPixelData (this);
				break;
			case RfbPixelData.ENCODINGTYPE_COPYRECT:
				this.pixelData = new RfbCopyRectPixelData (this);
				break;
//			case RfbPixelData.ENCODINGTYPE_RRE:
//				pixelData = new RfbRrePixelData (this, width, height);
//				break;
//			case RfbPixelData.ENCODINGTYPE_HEXTILE:
//				pixelData = new RfbHextilePixelData (this, width, height);
//				break;
			case RfbPixelData.ENCODINGTYPE_TRLE:
				this.pixelData = new RfbTrlePixelData (this);
				break;
			case RfbPixelData.ENCODINGTYPE_ZLIB:
				this.pixelData = new RfbZlibPixelData (this);
				break;
			case RfbPixelData.ENCODINGTYPE_TIGHT:
				this.pixelData = new RfbTightPixelData (this);
				break;
//			case RfbPixelData.ENCODINGTYPE_ZRLE:
//				pixelData = new RfbZrlePixelData (this, width, height);
//				break;
			case RfbPixelData.ENCODINGTYPE_PSEUDO_CURSOR:
				this.pixelData = new RfbCursorPseudoPixelData (this, this.session);
				break;
//			case RfbPixelData.ENCODINGTYPE_PSEUDO_DESKTOPSIZE:
//				pixelData = new RfbDesktopSizePseudoPixelData (this);
//				break;
			default:
				throw new MessageException ("Unknown encoding type: " + this.encodingType);
		}

		this.pixelData.read (in);
	}

	public void write (OutputStream out) throws IOException {
		Objects.requireNonNull (out, "out");
		Objects.requireNonNull (this.pixelData, "pixelData");

		writeU16 (out, this.xPosition, true);
		writeU16 (out, this.yPosition, true);
		writeU16 (out, this.width, true);
		writeU16 (out, this.height, true);
		writeS32 (out, this.encodingType, true);

		this.pixelData.write (out);
	}

	public RfbPixelData getPixelData () {
		return this.pixelData;
	}

	public int getX () {
		return this.xPosition;
	}

	public int getY () {
		return this.yPosition;
	}

	public int getWidth () {
		return this.width;
	}

	public int getHeight () {
		return this.height;
	}

	public RfbClientSession getSession () {
		return this.session;
	}

}
