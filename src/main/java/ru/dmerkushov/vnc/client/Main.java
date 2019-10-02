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
 *<
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
package ru.dmerkushov.vnc.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionState;
import ru.dmerkushov.vnc.client.rfb.session.password.UiPasswordSupplier;
import ru.dmerkushov.vnc.client.ui.DefaultJavaFxVncView;
import ru.dmerkushov.vnc.client.ui.VncView;

import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dmerkushov
 */
public class Main extends Application {

	static String host;
	static int port;
	static String password;

	// JAVAFX-STYLE SINGLE VIEW
	public static void main (String[] args) {
		Main.host = JOptionPane.showInputDialog ("Host", "localhost");
		Main.port = Integer.parseInt (JOptionPane.showInputDialog ("Port", "5901"));
//		Main.password = JOptionPane.showInputDialog ("Password");

		Application.launch (args);
	}

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle ("Hello World!");

		final RfbClientSession session;
		try {
			session = new RfbClientSession (ru.dmerkushov.vnc.client.Main.host, ru.dmerkushov.vnc.client.Main.port);
		} catch (java.io.IOException ex) {
			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
			return;
		}
		session.setPasswordSupplier (new UiPasswordSupplier ());

		VncView vncView = new DefaultJavaFxVncView ();
		vncView.setSession (session);

		try {
			session.startSession ();
		} catch (RfbSessionException | java.io.IOException ex) {
			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
		}

		HBox root = new HBox ();
		root.getChildren ().add (vncView.getJavafxNode ());
		primaryStage.setScene (new Scene (root, 1200, 800));

		primaryStage.setOnHiding (event -> Platform.runLater (() -> {
			try {
				session.setSessionState (RfbSessionState.Finished);
			} catch (RfbSessionException ex) {
				Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
			}
		}));

		primaryStage.show ();
	}

// JAVAFX-STYLE MULTIPLE THUMBNAILS
//	@Override
//	public void start (Stage primaryStage) {
//		primaryStage.setTitle ("Hello World!");
//
//		RfbClientSession session1;
//		try {
//			session1 = new RfbClientSession (host, port);
//		} catch (IOException ex) {
//			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//			return;
//		}
//		session1.setPasswordSupplier (() -> password);
//
//		VncView vncView1 = new ThumbnailJavafxVncView ();
//		vncView1.setSession (session1);
//
//		try {
//			session1.startSession ();
//		} catch (RfbSessionException | IOException ex) {
//			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//		}
//
//		RfbClientSession session2;
//		try {
//			session2 = new RfbClientSession (host, port);
//		} catch (IOException ex) {
//			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//			return;
//		}
//		session2.setPasswordSupplier (() -> password);
//
//		VncView vncView2 = new ThumbnailJavafxVncView ();
//		vncView2.setSession (session2);
//
//		try {
//			session2.startSession ();
//		} catch (RfbSessionException | IOException ex) {
//			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//		}
//
//		RfbClientSession session3;
//		try {
//			session3 = new RfbClientSession (host, port);
//		} catch (IOException ex) {
//			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//			return;
//		}
//		session3.setPasswordSupplier (() -> password);
//
//		VncView vncView3 = new ThumbnailJavafxVncView ();
//		vncView3.setSession (session3);
//
//		try {
//			session3.startSession ();
//		} catch (RfbSessionException | IOException ex) {
//			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//		}
//
//		HBox root = new HBox ();
//		root.getChildren ().addAll (vncView1.getJavafxNode (), vncView2.getJavafxNode (), vncView3.getJavafxNode ());
//		root.setPadding (new Insets (0, 0, 0, 0));
//		primaryStage.setScene (new Scene (root, 900, 250));
//
//		System.out.println ("Root: " + root.getWidth () + "x" + root.getHeight ());
//
//		((Canvas) vncView1.getJavafxNode ()).setWidth (350);
//		((Canvas) vncView1.getJavafxNode ()).setHeight (220);
//		((Canvas) vncView2.getJavafxNode ()).setWidth (350);
//		((Canvas) vncView2.getJavafxNode ()).setHeight (220);
//		((Canvas) vncView3.getJavafxNode ()).setWidth (350);
//		((Canvas) vncView3.getJavafxNode ()).setHeight (220);
//		primaryStage.setOnHiding (new EventHandler<WindowEvent> () {
//			@Override
//			public void handle (WindowEvent event) {
//				Platform.runLater (new Runnable () {
//					@Override
//					public void run () {
//						try {
//							session3.setSessionState (RfbSessionState.Finished);
//						} catch (RfbSessionException ex) {
//							Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//						}
//					}
//				});
//			}
//		});
//
//		primaryStage.show ();
//	}

	// SWING-STYLE
//	public static void main (String[] args) throws Exception {
//		Main.host = JOptionPane.showInputDialog ("Host", "localhost");
//		Main.port = Integer.parseInt (JOptionPane.showInputDialog ("Port", "5901"));
//		Main.password = JOptionPane.showInputDialog ("Password");
//
//		JFrame frame = new JFrame ();
//
//		RfbClientSession session = new RfbClientSession (Main.host, Main.port);
//		session.setPasswordSupplier (() -> Main.password);
//
//		VncView vncView = new DefaultSwingVncView ();
//		vncView.setSession (session);
//
//		frame.add (vncView.getSwingComponent ());
//
//		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
//
//		frame.setSize (new java.awt.Dimension (500, 500));
//
//		session.startSession ();
//
//		frame.setVisible (true);
//
//		vncView.repaint ();
//	}
}


