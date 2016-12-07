/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client;

import java.awt.Dimension;
import javax.swing.JFrame;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.ui.VncView;

/**
 *
 * @author dmerkushov
 */
public class Main {

	public static void main (String[] args) throws Exception {
		JFrame frame = new JFrame ();

		RfbClientSession session = new RfbClientSession ("localhost", 5901);
		VncView vncView = new VncView (session);

//		frame.add (new ru.dmerkushov.vnc.client.ui.ThumbnailView (vncView));
		frame.add (vncView);

		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		frame.setSize (new Dimension (500, 500));

		session.startSession ();

		frame.setVisible (true);

		vncView.repaint ();
	}
}
