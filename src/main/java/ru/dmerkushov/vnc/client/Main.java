/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.ui.VncView;

/**
 *
 * @author dmerkushov
 */
public class Main {

	public static void main (String args[]) {

		RfbClientSession session;
		try {
			session = new RfbClientSession ("10.1.1.232", 5901);
		} catch (IOException ex) {
			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
			return;
		}
		VncView view = new VncView (session);

		JFrame frame = new JFrame ("CNIIAG VNC");
		frame.add (view);

		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.setVisible (true);
	}
}
