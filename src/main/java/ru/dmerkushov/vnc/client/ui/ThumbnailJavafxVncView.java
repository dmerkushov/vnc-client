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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javax.swing.JComponent;
import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public class ThumbnailJavafxVncView extends Canvas implements VncView {

	private RfbClientSession session;

	@Override
	public void setSession (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
		session.attachView (this);

		this.setFocusTraversable (true);
		this.setMouseTransparent (false);
	}

	@Override
	public RfbClientSession getSession () {
		return session;
	}

	@Override
	public Component getAwtComponent () {
		return null;
	}

	@Override
	public JComponent getSwingComponent () {
		return null;
	}

	@Override
	public Node getJavafxNode () {
		return this;
	}

	@Override
	public Dimension getPreferredSize () {
		RfbFramebuffer framebuffer = session.getFramebuffer ();

		if (framebuffer == null) {
			return new Dimension (1000, 1000);
		}

		synchronized (framebuffer) {
			return new Dimension (framebuffer.getWidth (), framebuffer.getHeight ());
		}
	}

	@Override
	public void paintNow (int x, int y, int width, int height) {
		RfbFramebuffer framebuffer = session.getFramebuffer ();

		if (framebuffer == null) {
			VncCommon.getLogger ().warning ("No framebuffer attached to session");
			return;
		}

		Image fxImg;

		synchronized (framebuffer) {
			fxImg = SwingFXUtils.toFXImage (framebuffer, null);
		}

		final CountDownLatch doneLatch = new CountDownLatch (1);

		Platform.runLater (new Runnable () {
			@Override
			public void run () {

				try {

					GraphicsContext gc = ThumbnailJavafxVncView.this.getGraphicsContext2D ();

					double width = ThumbnailJavafxVncView.this.getWidth ();
					double height = ThumbnailJavafxVncView.this.getHeight ();

					gc.drawImage (fxImg, 0, 0, width, height);
				} finally {
					doneLatch.countDown ();
				}
			}
		});

		try {
			doneLatch.await ();
		} catch (InterruptedException ex) {
			Logger.getLogger (DefaultJavaFxVncView.class.getName ()).log (Level.SEVERE, null, ex);
		}

		System.out.println ("WxH: " + getWidth () + "x" + getHeight ());
	}

	@Override
	public void repaint () {
	}

	@Override
	public void setCursorImage (BufferedImage cursor, int hotspotX, int hotspotY) {
	}

}
