/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public class RfbCopyRectPixelData extends RfbPixelData {

	private int srcX;
	private int srcY;

	public RfbCopyRectPixelData (RfbRectangle rectangle) {
		super (rectangle);
	}

	@Override
	public void read (InputStream in) throws IOException {
		srcX = readU16 (in, true);
		srcY = readU16 (in, true);
	}

	@Override
	public void write (OutputStream out) throws IOException {
		writeU16 (out, srcX, true);
		writeU16 (out, srcY, true);
	}

	@Override
	public void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException {
		Objects.requireNonNull (framebuffer, "framebuffer");

		int dstX = rectangle.getX ();
		int dstY = rectangle.getY ();
		int width = rectangle.getWidth ();
		int height = rectangle.getHeight ();

		synchronized (framebuffer) {
			Raster src = framebuffer.getData (new Rectangle (srcX, srcY, width, height));
			Raster dst = src.createTranslatedChild (dstX, dstY);
			framebuffer.setData (dst);
		}
	}

}
