/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.JComponent;
import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.ui.events.VncViewKeyboardEvents;
import ru.dmerkushov.vnc.client.ui.events.VncViewMouseEvents;

/**
 *
 * @author dmerkushov
 */
public class VncView extends JComponent {

	RfbClientSession session;

	public VncView (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
		session.attachView (this);

		VncViewMouseEvents mouseEvents = new VncViewMouseEvents (session);
		VncViewKeyboardEvents keyboardEvents = new VncViewKeyboardEvents (session);

		this.setFocusable (true);

		this.addMouseMotionListener (mouseEvents);
		this.addMouseListener (mouseEvents);
		this.addMouseWheelListener (mouseEvents);
		this.addKeyListener (keyboardEvents);

		BufferedImage cursorImg = new BufferedImage (16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit ().createCustomCursor (cursorImg, new Point (0, 0), "blank cursor");
		this.setCursor (blankCursor);
	}

	@Override
	public void paint (Graphics g) {
		RfbFramebuffer framebuffer = session.getFramebuffer ();

		if (framebuffer == null) {
			VncCommon.getLogger ().warning ("No framebuffer attached to session");
			return;
		}

		synchronized (framebuffer) {
			g.drawImage (framebuffer, 0, 0, VncView.this);
		}
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

	public BufferedImage getScreenshot () {
		RfbFramebuffer framebuffer = session.getFramebuffer ();

		if (framebuffer == null) {
			return null;
		}

		BufferedImage screenshot;
		synchronized (framebuffer) {
			int width = framebuffer.getWidth ();
			int height = framebuffer.getHeight ();

			screenshot = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics screenshotG = screenshot.createGraphics ();
			screenshotG.drawImage (framebuffer, width, height, null);
		}
		return screenshot;
	}

}
