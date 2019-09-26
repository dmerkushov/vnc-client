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

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.dmerkushov.vnc.client.VncCommon.logger;

/**
 * @author dmerkushov
 */
public class ThumbnailJavafxVncView extends VncCanvas implements VncView {

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
		return this.session;
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
		RfbFramebuffer framebuffer = this.session.getFramebuffer ();

		if (framebuffer == null) {
			return new Dimension (1000, 1000);
		}

		synchronized (framebuffer) {
			return new Dimension (framebuffer.getWidth (), framebuffer.getHeight ());
		}
	}

	@Override
	public void paintNow (int x, int y, int width, int height) {
		RfbFramebuffer framebuffer = this.session.getFramebuffer ();

		if (framebuffer == null) {
			logger.warning ("No framebuffer attached to session");
			return;
		}

		Image fxImg;

		synchronized (framebuffer) {
			fxImg = this.toFxImage (framebuffer);
		}

		final CountDownLatch doneLatch = new CountDownLatch (1);

		Platform.runLater (() -> {
			try {

				GraphicsContext gc = ThumbnailJavafxVncView.this.getGraphicsContext2D ();

				double thumbWidth = ThumbnailJavafxVncView.this.getWidth ();
				double thumbHeight = ThumbnailJavafxVncView.this.getHeight ();

				gc.drawImage (fxImg, 0, 0, thumbWidth, thumbHeight);
			} finally {
				doneLatch.countDown ();
			}
		});

		try {
			doneLatch.await ();
		} catch (InterruptedException ex) {
			Logger.getLogger (DefaultJavaFxVncView.class.getName ()).log (Level.SEVERE, null, ex);
		}

		logger.finest ("thumb paintNow WxH: " + this.getWidth () + "x" + this.getHeight ());
	}

	@Override
	public void repaint () {
	}

	@Override
	public void setCursorImage (BufferedImage cursor, int hotspotX, int hotspotY) {
	}

}
