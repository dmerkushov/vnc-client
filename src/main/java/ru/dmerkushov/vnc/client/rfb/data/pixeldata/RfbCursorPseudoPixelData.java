/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBytes;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.ui.VncView;

/**
 *
 * @author dmerkushov
 */
public class RfbCursorPseudoPixelData extends RfbPixelData {

	private int width;
	private int height;
	private int hotspotX;
	private int hotspotY;
	private RfbPixelFormat pixelFormat;
	BufferedImage cursorImage;

	public RfbCursorPseudoPixelData (RfbRectangle rectangle, RfbClientSession session) {
		super (rectangle);

		Objects.requireNonNull (session, "session");

		pixelFormat = rectangle.pixelFormat;
	}

	@Override
	public void read (InputStream in) throws IOException {
		width = rectangle.getWidth ();
		height = rectangle.getHeight ();
		hotspotX = rectangle.getX ();
		hotspotY = rectangle.getY ();

		cursorImage = pixelFormat.readArgbImage (width, height, in);
		int scanlineLength = (width + 7) / 8;
		int bitmaskByteCount = scanlineLength * height;
		byte[] bitmask = readBytes (in, bitmaskByteCount);

		ByteArrayOutputStream baos = new ByteArrayOutputStream (scanlineLength * 8 * height);

//		for (int scanlineIndex = 0; scanlineIndex < bitmask.length / scanlineLength; scanlineIndex ++) {
//			for (int x = 0; x < width; x++) {
//				byte bitmaskByte = bitmask[scanlineIndex * scanlineLength + x];
//				System.out.println ()
//			}
//		}
		for (int i = 0; i < bitmask.length; i++) {
			byte bitmaskByte = bitmask[i];

			for (int k = 0; k < 8; k++) {
				baos.write ((bitmaskByte & 0x80) == 0 ? 0 : 1);
				bitmaskByte <<= 1;
			}
		}

		byte[] bytemask = baos.toByteArray ();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				boolean keepPoint = (bytemask[y * scanlineLength * 8 + x] != 0);
				int argb;
				if (keepPoint) {
					argb = cursorImage.getRGB (x, y) | 0xFF000000;
				} else {
					argb = 0;
				}
				cursorImage.setRGB (x, y, argb);
			}
		}
	}

	@Override
	public void write (OutputStream out) throws IOException {
		//TODO Implement write()
	}

	@Override
	public void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException {
		Objects.requireNonNull (framebuffer, "framebuffer");

		RfbClientSession session = framebuffer.getSession ();
		VncView vncView = session.getView ();

		Cursor cursor = Toolkit.getDefaultToolkit ().createCustomCursor (cursorImage, new Point (hotspotX, hotspotY), "VNC cursor");
		vncView.setCursor (cursor);
	}

}
