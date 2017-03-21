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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionState;
import ru.dmerkushov.vnc.client.ui.DefaultJavaFxVncView;
import ru.dmerkushov.vnc.client.ui.VncView;

/**
 *
 * @author dmerkushov
 */
public class Main extends Application {

	public static void main (String[] args) {
		launch (args);
	}

	String host = "10.1.4.74";
	int port = 5901;
	String password = "12345678";

// JAVAFX-STYLE
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
	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle ("Hello World!");

		final RfbClientSession session;
		try {
			session = new RfbClientSession (host, port);
		} catch (IOException ex) {
			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
			return;
		}
		session.setPasswordSupplier (() -> password);

		VncView vncView = new DefaultJavaFxVncView ();
		vncView.setSession (session);

		try {
			session.startSession ();
		} catch (RfbSessionException | IOException ex) {
			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
		}

		HBox root = new HBox ();
		root.getChildren ().add (vncView.getJavafxNode ());
		primaryStage.setScene (new Scene (root, 300, 250));

		primaryStage.setOnHiding (new EventHandler<WindowEvent> () {
			@Override
			public void handle (WindowEvent event) {
				Platform.runLater (new Runnable () {
					@Override
					public void run () {
						try {
							session.setSessionState (RfbSessionState.Finished);
						} catch (RfbSessionException ex) {
							Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
						}
					}
				});
			}
		});

		Thread suspendThread = new Thread (() -> {
			System.out.println ("SuspendThread started");
			try {
				Thread.sleep (10000L);
			} catch (InterruptedException ex) {
				Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
			}
			long suspendFor = 360000L;
			System.out.println ("Suspending session for " + suspendFor + " millis");
			session.suspend ();
			try {
				Thread.sleep (suspendFor);
			} catch (InterruptedException ex) {
				Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
			}
			System.out.println ("Resuming session");
			session.resume ();
		});
		suspendThread.start ();
		primaryStage.show ();
	}
}

// SWING-STYLE
//	public static void main (String[] args) throws Exception {
//
//		JFrame frame = new JFrame ();
//
//		RfbClientSession session = new RfbClientSession (host, port);
//		session.setPasswordSupplier (() -> password);
//
//		VncView vncView = new DefaultSwingVncView ();
//		vncView.setSession (session);
//
////		frame.add (new ru.dmerkushov.vnc.client.ui.ThumbnailView (vncView));
//		frame.add (vncView.getSwingComponent ());
//
//		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
//
//		frame.setSize (new Dimension (500, 500));
//
//		session.startSession ();
//
//		frame.setVisible (true);
//
//		vncView.repaint ();
//	}
//}
