/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
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

	public RfbRawPixelData (RfbRectangle rectangle) {
		super (rectangle);

		if (!ALLOWED_BITS_PER_PIXEL_SET.contains (rectangle.pixelFormat.getBitsPerPixel ())) {
			throw new IllegalArgumentException ("Bits per pixel in the supplied rectangle's PixelFormat is not one of {8, 16, 32}: " + rectangle.pixelFormat.getBitsPerPixel ());
		}

		this.width = rectangle.getWidth ();
		this.height = rectangle.getHeight ();
		this.pixelFormat = rectangle.pixelFormat;
		bytesPerPixel = pixelFormat.getBitsPerPixel () / 8;
	}

	@Override
	public void read (InputStream in) throws IOException {
		innerImage = readPixelsArray (width, height, pixelFormat, in);

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

		fbg.drawImage (innerImage, rectangle.getX (), rectangle.getY (), null);
	}

	public static BufferedImage readPixelsArray (int width, int height, RfbPixelFormat pixelFormat, InputStream in) throws IOException {
		BufferedImage innerImage;

		int bytesPerPixel = pixelFormat.getBitsPerPixel () / 8;

		int bytesCount = width * height * bytesPerPixel;

		DataInputStream dis = new DataInputStream (in);
		byte[] bytes = new byte[bytesCount];
		dis.readFully (bytes);

		if (bytesPerPixel == 4 && pixelFormat.isTrueColor ()) {
			innerImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);

			ByteBuffer bb = ByteBuffer.wrap (bytes);
			bb.order (pixelFormat.isBigEndian () ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

			int redMax = pixelFormat.getRedMax ();
			int redShift = pixelFormat.getRedShift ();
			int greenMax = pixelFormat.getGreenMax ();
			int greenShift = pixelFormat.getGreenShift ();
			int blueMax = pixelFormat.getBlueMax ();
			int blueShift = pixelFormat.getBlueShift ();

			int pixel;
			int red;
			int green;
			int blue;
			int innerPixel;
			int[] innerPixels = new int[width * height];

			for (int innerPixelIndex = 0; innerPixelIndex < innerPixels.length; innerPixelIndex++) {
				pixel = bb.getInt ();

				red = (((pixel >> redShift) & redMax) * 0xFF / redMax) & 0xFF;
				green = (((pixel >> greenShift) & greenMax) * 0xFF / greenMax) & 0xFF;
				blue = (((pixel >> blueShift) & blueMax) * 0xFF / blueMax) & 0xFF;

				innerPixel = (0xFF << 24) | (red << 16) | (green << 8) | blue;

				innerPixels[innerPixelIndex] = innerPixel;
			}
			innerImage.setRGB (0, 0, width, height, innerPixels, 0, width);
		} else {
			throw new IllegalStateException ("Unsupported pixel format");
		}

		return innerImage;
	}

}
