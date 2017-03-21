/*
 * Copyright (C) 2017 dmerkushov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ru.dmerkushov.vnc.client.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import sun.awt.image.IntegerComponentRaster;

/**
 *
 * @author dmerkushov
 */
public class VncCanvas extends Canvas {

	/**
	 * Taken from
	 * {@link javafx.embed.swing.SwingFXUtils#toFXImage(java.awt.image.BufferedImage, javafx.scene.image.WritableImage)}
	 *
	 * @param bimg
	 * @return
	 */
	public WritableImage toFxImage (BufferedImage bimg) {
		int bw = bimg.getWidth ();
		int bh = bimg.getHeight ();
		BufferedImage usedBi = bimg;
		switch (bimg.getType ()) {
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				break;
			default:
				usedBi = new BufferedImage (bw, bh, BufferedImage.TYPE_INT_ARGB_PRE);
				Graphics2D g2d = usedBi.createGraphics ();
				g2d.drawImage (bimg, 0, 0, null);
				g2d.dispose ();
				break;
		}
		// assert(bimg.getType == TYPE_INT_ARGB[_PRE]);
		WritableImage wimg = new WritableImage (bw, bh);
		PixelWriter pw = wimg.getPixelWriter ();
		IntegerComponentRaster icr = (IntegerComponentRaster) usedBi.getRaster ();
		int data[] = icr.getDataStorage ();
		int offset = icr.getDataOffset (0);
		int scan = icr.getScanlineStride ();
		PixelFormat<IntBuffer> pf = (usedBi.isAlphaPremultiplied ()
				? PixelFormat.getIntArgbPreInstance ()
				: PixelFormat.getIntArgbInstance ());
		pw.setPixels (0, 0, bw, bh, pf, data, offset, scan);
		return wimg;
	}

}
