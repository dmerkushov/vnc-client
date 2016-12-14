/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbCopyRectPixelData;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbCursorPseudoPixelData;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbPixelData;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbRawPixelData;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbTrlePixelData;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbZlibPixelData;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readS32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeS32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class RfbRectangle {

	public final RfbPixelFormat pixelFormat;

	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private int encodingType;
	RfbPixelData pixelData;
	private RfbClientSession session;

	public RfbRectangle (RfbPixelFormat pixelFormat, RfbClientSession session) {
		Objects.requireNonNull (pixelFormat, "pixelFormat");
		Objects.requireNonNull (session, "session");

		this.pixelFormat = pixelFormat;
		this.session = session;
	}

	public void read (InputStream in) throws MessageException, IOException {
		xPosition = readU16 (in, true);
		yPosition = readU16 (in, true);
		width = readU16 (in, true);
		height = readU16 (in, true);

		encodingType = readS32 (in, true);

		switch (encodingType) {
			case RfbPixelData.ENCODINGTYPE_RAW:
				pixelData = new RfbRawPixelData (this);
				break;
			case RfbPixelData.ENCODINGTYPE_COPYRECT:
				pixelData = new RfbCopyRectPixelData (this);
				break;
//			case RfbPixelData.ENCODINGTYPE_RRE:
//				pixelData = new RfbRrePixelData (this, width, height);
//				break;
//			case RfbPixelData.ENCODINGTYPE_HEXTILE:
//				pixelData = new RfbHextilePixelData (this, width, height);
//				break;
			case RfbPixelData.ENCODINGTYPE_TRLE:
				pixelData = new RfbTrlePixelData (this);
				break;
			case RfbPixelData.ENCODINGTYPE_ZLIB:
				pixelData = new RfbZlibPixelData (this);
				break;
//			case RfbPixelData.ENCODINGTYPE_ZRLE:
//				pixelData = new RfbZrlePixelData (this, width, height);
//				break;
			case RfbPixelData.ENCODINGTYPE_PSEUDO_CURSOR:
				pixelData = new RfbCursorPseudoPixelData (this, session);
				break;
//			case RfbPixelData.ENCODINGTYPE_PSEUDO_DESKTOPSIZE:
//				pixelData = new RfbDesktopSizePseudoPixelData (this);
//				break;
			default:
				throw new MessageException ("Unknown encoding type: " + encodingType);
		}

		pixelData.read (in);
	}

	public void write (OutputStream out) throws IOException {
		Objects.requireNonNull (out, "out");
		Objects.requireNonNull (pixelData, "pixelData");

		writeU16 (out, xPosition, true);
		writeU16 (out, yPosition, true);
		writeU16 (out, width, true);
		writeU16 (out, height, true);
		writeS32 (out, encodingType, true);

		pixelData.write (out);
	}

	public RfbPixelData getPixelData () {
		return pixelData;
	}

	public int getX () {
		return xPosition;
	}

	public int getY () {
		return yPosition;
	}

	public int getWidth () {
		return width;
	}

	public int getHeight () {
		return height;
	}

	public RfbClientSession getSession () {
		return session;
	}

}
