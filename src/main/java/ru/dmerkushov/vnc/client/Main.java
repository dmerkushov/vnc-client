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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionState;
import ru.dmerkushov.vnc.client.ui.ThumbnailJavafxVncView;
import ru.dmerkushov.vnc.client.ui.VncView;

/**
 *
 * @author dmerkushov
 */
public class Main extends Application {

	public static void main (String[] args) {
		launch (args);
	}

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle ("Hello World!");

		RfbClientSession session;
		try {
			session = new RfbClientSession ("10.1.4.133", 5901);
		} catch (IOException ex) {
			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
			return;
		}

		VncView vncView = new ThumbnailJavafxVncView ();
		vncView.setSession (session);

		try {
			session.startSession ();
		} catch (RfbSessionException | IOException ex) {
			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
		}

		Pane root = new Pane ();
		root.getChildren ().add (vncView.getJavafxNode ());
		root.setPadding (new Insets (0, 0, 0, 0));
		primaryStage.setScene (new Scene (root, 300, 250));

		vncView.getJavafxNode ().prefHeight (root.getWidth ());
		vncView.getJavafxNode ().prefHeight (root.getHeight ());

		System.out.println ("Root: " + root.getWidth () + "x" + root.getHeight ());

//		vncView.getJavafxNode ().getProperties ().put (root, root)
//
//		((Canvas) vncView.getJavafxNode ()).setWidth (400);
//		((Canvas) vncView.getJavafxNode ()).setHeight (400);
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

		primaryStage.show ();
	}

// JAVAFX-STYLE
//	@Override
//	public void start (Stage primaryStage) {
//		primaryStage.setTitle ("Hello World!");
//
//		RfbClientSession session;
//		try {
//			session = new RfbClientSession ("10.1.4.133", 5901);
//		} catch (IOException ex) {
//			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//			return;
//		}
//
//		VncView vncView = new DefaultJavaFxVncView ();
//		vncView.setSession (session);
//
//		try {
//			session.startSession ();
//		} catch (RfbSessionException | IOException ex) {
//			Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
//		}
//
//		StackPane root = new StackPane ();
//		root.getChildren ().add (vncView.getJavafxNode ());
//		primaryStage.setScene (new Scene (root, 300, 250));
//
//		primaryStage.setOnHiding (new EventHandler<WindowEvent> () {
//			@Override
//			public void handle (WindowEvent event) {
//				Platform.runLater (new Runnable () {
//					@Override
//					public void run () {
//						try {
//							session.setSessionState (RfbSessionState.Finished);
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
}

// SWING-STYLE
//	public static void main (String[] args) throws Exception {
//
//		JFrame frame = new JFrame ();
//
//		RfbClientSession session = new RfbClientSession ("192.168.2.6", 5901);
//
//		VncView vncView = new DefaultJavaFxVncView ();
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
