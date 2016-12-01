/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.ui;

import java.awt.Graphics;
import java.util.Objects;
import javax.swing.JComponent;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 *
 * @author dmerkushov
 */
public class VncView extends JComponent {

	RfbSession session;

	public VncView (RfbSession session) {
		Objects.requireNonNull (session, "session");
	}

	@Override
	public void paint (Graphics g) {
		g.drawImage (session.getFramebuffer (), 0, 0, this);
	}

}
