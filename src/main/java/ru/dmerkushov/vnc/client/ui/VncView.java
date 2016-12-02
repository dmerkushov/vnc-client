/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Objects;
import javax.swing.JComponent;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class VncView extends JComponent {

	RfbClientSession session;

	public VncView (RfbClientSession session) {
		Objects.requireNonNull (session, "session");
	}

	@Override
	public void paint (Graphics g) {
		if (session.isFramebufferAttached ()) {
			g.drawImage (session.getFramebuffer (), 0, 0, this);
		}
	}

	@Override
	public Dimension getPreferredSize () {
		Dimension size;
		if (session.isFramebufferAttached ()) {
			RfbFramebuffer frm = session.getFramebuffer ();

			return new Dimension (frm.getWidth (), frm.getHeight ());
		} else {
			return new Dimension (0, 0);
		}
	}

}
