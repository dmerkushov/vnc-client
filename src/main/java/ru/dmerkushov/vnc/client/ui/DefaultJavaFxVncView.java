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
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Dimension2D;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.KeyEventMessageSequence;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.PointerEventMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.PointerEventMessageSequence;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.ui.events.Keysyms;
import sun.awt.image.IntegerComponentRaster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.dmerkushov.vnc.client.VncCommon.vncPrefs;

/**
 * @author dmerkushov
 */
public class DefaultJavaFxVncView extends VncCanvas implements VncView {

	private RfbClientSession session;

	@Override
	public void setSession (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
		session.attachView (this);

		this.setFocusTraversable (true);
		this.setMouseTransparent (false);

		this.addEventFilter (MouseEvent.ANY, new EventHandler<MouseEvent> () {

			long lastMoveTimestamp = 0;
			long mouseMoveSendDelayMs = vncPrefs.getLong ("MOUSEMOVE_SEND_DELAY", 100L);

			@Override
			public void handle (MouseEvent e) {
				EventType eventType = e.getEventType ();
				int x = (int) e.getX ();
				int y = (int) e.getY ();

				if (eventType == MouseEvent.MOUSE_PRESSED) {
					int buttonMask = PointerEventMessage.BUTTONMASK_LEFT;
					if (e.getButton () == MouseButton.SECONDARY) {
						buttonMask = PointerEventMessage.BUTTONMASK_RIGHT;
					} else if (e.getButton () == MouseButton.MIDDLE) {
						buttonMask = PointerEventMessage.BUTTONMASK_MIDDLE;
					}

					PointerEventMessage pointerEventMsg = new PointerEventMessage (session, buttonMask, x, y);
					session.sendMessage (pointerEventMsg);
				} else if (eventType == MouseEvent.MOUSE_RELEASED) {
					PointerEventMessage pointerEventMsg = new PointerEventMessage (session, 0, x, y);
					session.sendMessage (pointerEventMsg);
				} else if (eventType == MouseEvent.MOUSE_ENTERED || eventType == MouseEvent.MOUSE_MOVED) {

					// Don't spam the server with mouse position updates
					long motionTimestamp = System.currentTimeMillis ();
					if (motionTimestamp - this.lastMoveTimestamp > this.mouseMoveSendDelayMs) {
						PointerEventMessageSequence pointerEventMsg = new PointerEventMessageSequence (session, PointerEventMessageSequence.EVENTTYPE_CAMEHERE, x, y);
						session.sendMessage (pointerEventMsg);

						this.lastMoveTimestamp = motionTimestamp;
					}
				}
			}
		});
		this.addEventFilter (ScrollEvent.ANY, (ScrollEvent e) -> {
			int x = (int) e.getX ();
			int y = (int) e.getY ();

			int eventType;
			int times;

			int wheelRotation = (int) (e.getDeltaY () / e.getMultiplierY ());
			if (wheelRotation < 0) {        // wheel rotated down
				eventType = PointerEventMessageSequence.EVENTTYPE_WHEEL_DOWN;
				times = -wheelRotation;
			} else {                        // wheel rotated up
				eventType = PointerEventMessageSequence.EVENTTYPE_WHEEL_UP;
				times = wheelRotation;
			}

			PointerEventMessageSequence pointerEventMsg = new PointerEventMessageSequence (session, eventType, x, y);

			for (int i = 0; i < times; i++) {
				session.sendMessage (pointerEventMsg);
			}
		});
		this.addEventFilter (KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
			int keySym = Keysyms.translateFxKeyEvent (e);

			System.out.println ("KeyEvent " + e + " : keysym " + keySym + " " + Integer.toHexString (keySym));

			KeyEventMessageSequence seq = new KeyEventMessageSequence (session, KeyEventMessageSequence.EVENTTYPE_PRESSED, keySym);
			session.sendMessage (seq);

			e.consume ();
		});
		this.addEventFilter (KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
			int keySym = Keysyms.translateFxKeyEvent (e);

			System.out.println ("KeyEvent " + e + " : keysym " + keySym + " " + Integer.toHexString (keySym));

			KeyEventMessageSequence seq = new KeyEventMessageSequence (session, KeyEventMessageSequence.EVENTTYPE_RELEASED, keySym);
			session.sendMessage (seq);

			e.consume ();
		});
		this.addEventFilter (KeyEvent.ANY, (KeyEvent e) -> {
			e.consume ();
		});
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
			VncCommon.getLogger ().warning ("No framebuffer attached to session");
			return;
		}

		Image fxImg;

		synchronized (framebuffer) {
			fxImg = this.toFxImage (framebuffer);
		}

		final CountDownLatch doneLatch = new CountDownLatch (1);

		Platform.runLater (() -> {
			// Skip this framebuffer update if it's old
//				if (framebuffer.fbuIndex.get () != FramebufferUpdateMessage.currentFbuIndex.get ()) {
//
//					// DEBUG
//					System.out.println ("Skipping");
//
//					doneLatch.countDown ();
//					return;
//				}

			try {
				DefaultJavaFxVncView.this.setWidth (fxImg.getWidth ());
				DefaultJavaFxVncView.this.setHeight (fxImg.getHeight ());

				GraphicsContext gc = DefaultJavaFxVncView.this.getGraphicsContext2D ();

				gc.drawImage (fxImg, 0, 0, width, height);
			} finally {
				doneLatch.countDown ();
			}
		});

		try {
			doneLatch.await ();
		} catch (InterruptedException ex) {
			Logger.getLogger (DefaultJavaFxVncView.class.getName ()).log (Level.SEVERE, null, ex);
		}

		// DEBUG
		System.out.println ("def paintNow WxH: " + this.getWidth () + "x" + this.getHeight ());
	}

	@Override
	public void setCursorImage (BufferedImage cursor, int hotspotX, int hotspotY) {
		Platform.runLater (() -> {
			Dimension2D bestSize = ImageCursor.getBestSize (cursor.getWidth (), cursor.getHeight ());
			int bestWidth = (int) bestSize.getWidth ();
			int bestHeight = (int) bestSize.getHeight ();

			WritableImage wimg = new WritableImage (bestWidth, bestHeight);
			PixelWriter pw = wimg.getPixelWriter ();
			IntegerComponentRaster icr = (IntegerComponentRaster) cursor.getRaster ();
			int[] data = icr.getDataStorage ();
			int offset = icr.getDataOffset (0);
			int scan = icr.getScanlineStride ();
			PixelFormat<IntBuffer> pf = PixelFormat.getIntArgbInstance ();
			pw.setPixels (0, 0, cursor.getWidth (), cursor.getHeight (), pf, data, offset, scan);

			// DEBUG
			System.out.println ("New cursor buf: w=" + cursor.getWidth () + " h=" + cursor.getHeight ());
			System.out.println ("New cursor fx: w=" + wimg.widthProperty ().get () + " h=" + wimg.heightProperty ().get ());

			ImageCursor fxImgCursor = new ImageCursor (wimg, hotspotX, hotspotY);

			this.setCursor (fxImgCursor);
		});
	}

	@Override
	public void repaint () {
	}

}
