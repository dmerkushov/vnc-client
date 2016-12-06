/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.FramebufferUpdateRequestMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public class VncView extends JComponent {

	RfbClientSession session;

	public VncView (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
	}

	@Override
	public void paint (Graphics g) {
		if (session.isFramebufferAttached ()) {
			SwingUtilities.invokeLater (new Runnable () {
				@Override
				public void run () {
					FramebufferUpdateRequestMessage furm = new FramebufferUpdateRequestMessage (session, session.getFramebuffer ());
					try {
						furm.write (session.getOut ());
					} catch (MessageException | IOException ex) {
						Logger.getLogger (VncView.class.getName ()).log (Level.SEVERE, null, ex);
					}
					g.drawImage (session.getFramebuffer (), 0, 0, VncView.this);
				}
			});

		} else {
			System.err.println ("No framebuffer attached to session");
		}
	}

	@Override
	public Dimension getPreferredSize () {
		if (session.isFramebufferAttached ()) {
			RfbFramebuffer frm = session.getFramebuffer ();

			return new Dimension (frm.getWidth (), frm.getHeight ());
		} else {
			return new Dimension (0, 0);
		}
	}

}
