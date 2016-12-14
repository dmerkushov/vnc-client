/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public abstract class RfbPixelData {

	public static final int ENCODINGTYPE_RAW = 0;
	public static final int ENCODINGTYPE_COPYRECT = 1;
	public static final int ENCODINGTYPE_RRE = 2;
	public static final int ENCODINGTYPE_HEXTILE = 5;
	public static final int ENCODINGTYPE_ZLIB = 6;
	public static final int ENCODINGTYPE_TRLE = 15;
	public static final int ENCODINGTYPE_ZRLE = 16;
	public static final int ENCODINGTYPE_PSEUDO_DESKTOPSIZE = -223;
	public static final int ENCODINGTYPE_PSEUDO_CURSOR = -239;

	public final RfbRectangle rectangle;

	public RfbPixelData (RfbRectangle rectangle) {
		Objects.requireNonNull (rectangle, "rectangle");

		this.rectangle = rectangle;
	}

	public abstract void read (InputStream in) throws MessageException, IOException;

	public abstract void write (OutputStream out) throws IOException;

	public abstract void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException;
}
