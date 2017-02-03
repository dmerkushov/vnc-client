/*
 * Copyright (C) 2016 Dmitriy Merkushov
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
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

		RfbClientSession session = new RfbClientSession ("10.1.4.133", 5901);
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
