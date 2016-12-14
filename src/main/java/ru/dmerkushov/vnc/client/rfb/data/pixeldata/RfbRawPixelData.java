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
		innerImage = pixelFormat.readArgbImage (width, height, in);
	}

	@Override
	public void write (OutputStream out) throws IOException {
		out.write (bytes);
	}

	@Override
	public void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException {
		Objects.requireNonNull (framebuffer, "framebuffer");

		Objects.requireNonNull (innerImage, "innerImage");

		synchronized (framebuffer) {
			Graphics fbg = framebuffer.createGraphics ();
			fbg.drawImage (innerImage, rectangle.getX (), rectangle.getY (), null);
		}
	}

}
