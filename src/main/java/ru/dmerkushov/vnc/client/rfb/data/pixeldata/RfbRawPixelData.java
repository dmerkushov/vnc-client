/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBytes;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public class RfbRawPixelData extends RfbPixelData {

	public final int width;
	public final int height;
	RfbPixelFormat pixelFormat;
	int bytesPerPixel;
	BufferedImage innerImage;

	public static final int[] ALLOWED_BITS_PER_PIXEL = {8, 16, 32};
	public static final HashSet<Integer> ALLOWED_BITS_PER_PIXEL_SET = new HashSet<> (Arrays.asList (8, 16, 32));

	private byte[] bytes;

	public RfbRawPixelData (RfbRectangle rectangle, int width, int height) {
		super (rectangle);

		if (!ALLOWED_BITS_PER_PIXEL_SET.contains (rectangle.pixelFormat.getBitsPerPixel ())) {
			throw new IllegalArgumentException ("Bits per pixel in the supplied rectangle's PixelFormat is not one of {8, 16, 32}: " + rectangle.pixelFormat.getBitsPerPixel ());
		}

		this.width = width;
		this.height = height;
		this.pixelFormat = rectangle.pixelFormat;
		bytesPerPixel = pixelFormat.getBitsPerPixel () / 8;
	}

	@Override
	public void read (InputStream in) throws IOException {
		int bytesCount = width * height * bytesPerPixel;

		bytes = readBytes (in, bytesCount);

		innerImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);

		if (pixelFormat.isTrueColor ()) {

			ByteBuffer bb = ByteBuffer.allocate (bytesPerPixel);

			int[] argbs = new int[width * height];

			int argb;
			int red;
			int green;
			int blue;
			int readValue;
			int redMax = pixelFormat.getRedMax ();
			int redShift = pixelFormat.getRedShift ();
			int greenMax = pixelFormat.getGreenMax ();
			int greenShift = pixelFormat.getGreenShift ();
			int blueMax = pixelFormat.getBlueMax ();
			int blueShift = pixelFormat.getBlueShift ();

			for (int i = 0; i < bytes.length; i += bytesPerPixel) {
				bb.order (ByteOrder.BIG_ENDIAN);
				bb.put (bytes, i, bytesPerPixel);
				bb.flip ();
				if (!pixelFormat.isBigEndian ()) {
					bb.order (ByteOrder.LITTLE_ENDIAN);
				}
				readValue = bb.getInt ();
				bb.flip ();
				red = (((readValue >> redShift) & redMax) * 0xFF / redMax) & 0xFF;
				green = (((readValue >> greenShift) & greenMax) * 0xFF / greenMax) & 0xFF;
				blue = (((readValue >> blueShift) & blueMax) * 0xFF / blueMax) & 0xFF;
				argb = (red << 16) | (green << 8) | blue;
				argbs[i / bytesPerPixel] = argb;
			}
			innerImage.setRGB (0, 0, width, height, argbs, 0, width);
		}
	}

	@Override
	public void write (OutputStream out) throws IOException {
		out.write (bytes);
	}

	@Override
	public void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException {
		Objects.requireNonNull (framebuffer, "framebuffer");

		Objects.requireNonNull (innerImage, "innerImage");

		Graphics fbg = framebuffer.getGraphics ();

		fbg.drawImage (innerImage, 0, 0, null);
	}

}
