/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javafx.scene.Node;
import javax.swing.JComponent;
import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.ui.events.SwingVncViewKeyboardEvents;
import ru.dmerkushov.vnc.client.ui.events.SwingVncViewMouseEvents;

/**
 *
 * @author dmerkushov
 */
public final class DefaultSwingVncView extends JComponent implements VncView {

	RfbClientSession session;

	public DefaultSwingVncView () {
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
			g.drawImage (framebuffer, 0, 0, DefaultSwingVncView.this);
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

	@Override
	public void setSession (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
		session.attachView (this);

		SwingVncViewMouseEvents mouseEvents = new SwingVncViewMouseEvents (session);
		SwingVncViewKeyboardEvents keyboardEvents = new SwingVncViewKeyboardEvents (session);

		this.setFocusable (true);

		this.addMouseMotionListener (mouseEvents);
		this.addMouseListener (mouseEvents);
		this.addMouseWheelListener (mouseEvents);
		this.addKeyListener (keyboardEvents);
	}

	@Override
	public JComponent getSwingComponent () {
		return this;
	}

	@Override
	public Node getJavafxNode () {
		return null;
	}

	@Override
	public Component getAwtComponent () {
		return this;
	}

	@Override
	public RfbClientSession getSession () {
		return this.session;
	}

	@Override
	public void paintNow (int x, int y, int width, int height) {
		paintImmediately (x, y, width, height);
	}

	@Override
	public void setCursorImage (BufferedImage cursorImage, int hotspotX, int hotspotY) {
		Cursor cursor = Toolkit.getDefaultToolkit ().createCustomCursor (cursorImage, new Point (hotspotX, hotspotY), "VNC cursor");
		this.setCursor (cursor);
	}

}
