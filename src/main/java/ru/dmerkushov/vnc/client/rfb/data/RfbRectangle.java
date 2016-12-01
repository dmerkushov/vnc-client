/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbPixelData;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbRawPixelData;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readS32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;

/**
 *
 * @author dmerkushov
 */
public class RfbRectangle {

	public static final int ENCODINGTYPE_RAW = 0;
	public static final int ENCODINGTYPE_COPYRECT = 1;
	public static final int ENCODINGTYPE_RRE = 2;
	public static final int ENCOFINGTYPE_HEXTILE = 5;
	public static final int ENCOFINGTYPE_TRLE = 15;
	public static final int ENCOFINGTYPE_ZRLE = 16;
	public static final int ENCOFINGTYPE_PSEUDO_CURSOR = -239;
	public static final int ENCOFINGTYPE_PSEUDO_DESKTOPSIZE = -223;

	public final RfbPixelFormat pixelFormat;

	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private int encodingType;
	RfbPixelData pixelData;

	public RfbRectangle (RfbPixelFormat pixelFormat) {
		this.pixelFormat = pixelFormat;
	}

	public void read (InputStream in) throws IOException {
		xPosition = readU16 (in);
		yPosition = readU16 (in);
		width = readU16 (in);
		height = readU16 (in);

		encodingType = readS32 (in);

		switch (encodingType) {
			case ENCODINGTYPE_RAW:
				pixelData = new RfbRawPixelData (this, width, height);
				break;

		}
	}

	public void write (OutputStream out) throws IOException {

	}

}
