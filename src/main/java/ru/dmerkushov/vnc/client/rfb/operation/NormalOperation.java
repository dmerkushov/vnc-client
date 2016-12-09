/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbPixelDataException;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.MessageFactoryException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.C2SMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.FramebufferUpdateRequestMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.s2c.FramebufferUpdateMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.s2c.S2CMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.s2c.S2CMessageFactory;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.ui.VncView;

/**
 *
 * @author dmerkushov
 */
public class NormalOperation extends Operation {

	Thread getMessagesThread;
	final Queue<S2CMessage> incomingMessagesQueue;
	Thread processMessagesThread;
	Thread framebufferUpdateRequestThread;
	final Queue<C2SMessage> outgoingMessagesQueue;
	Thread sendMessagesThread;

	public NormalOperation (RfbClientSession session) {
		super (session);

		getMessagesThread = new Thread (new GetMessagesRunnable ());
		processMessagesThread = new Thread (new ProcessMessagesRunnable ());
		framebufferUpdateRequestThread = new Thread (new FramebufferUpdateRequestRunnable ());
		incomingMessagesQueue = new ConcurrentLinkedQueue<> ();
		outgoingMessagesQueue = new ConcurrentLinkedQueue<> ();
		sendMessagesThread = new Thread (new SendMessagesRunnable ());
	}

	@Override
	public void operate () {
		getMessagesThread.start ();
		processMessagesThread.start ();
		framebufferUpdateRequestThread.start ();
		sendMessagesThread.start ();
	}

	public void sendMessage (C2SMessage message) {
		Objects.requireNonNull (message, "message");

		outgoingMessagesQueue.add (message);
	}

	class GetMessagesRunnable implements Runnable {

		@Override
		public void run () {
			while (true) {
				S2CMessage message = null;
				try {
					message = S2CMessageFactory.getInstance ().readMessage (session);
				} catch (MessageFactoryException | IOException ex) {
					Logger.getLogger (NormalOperation.class.getName ()).log (Level.SEVERE, null, ex);
				}
				if (message == null) {
					processMessagesThread.interrupt ();
					break;
				}
				incomingMessagesQueue.add (message);
			}
		}
	}

	class SendMessagesRunnable implements Runnable {

		@Override
		public void run () {
			OutputStream out = session.getOut ();
			C2SMessage message = null;
			lbl:
			while (true) {
				message = outgoingMessagesQueue.poll ();
				while (message == null) {
					try {
						Thread.sleep (10l);
					} catch (InterruptedException ex) {
						break lbl;
					}
					message = outgoingMessagesQueue.poll ();
				}

				try {
					message.write (out);
				} catch (MessageException | IOException ex) {
					Logger.getLogger (NormalOperation.class.getName ()).log (Level.SEVERE, null, ex);
				}
			}
		}
	}

	class ProcessMessagesRunnable implements Runnable {

		@Override
		public void run () {
			while (true) {
				S2CMessage message = incomingMessagesQueue.poll ();
				if (message instanceof FramebufferUpdateMessage && session.isFramebufferAttached ()) {
					FramebufferUpdateMessage fbuMessage = (FramebufferUpdateMessage) message;
					RfbRectangle[] rectangles = fbuMessage.getRectangles ();
					for (int i = 0; i < rectangles.length; i++) {
						RfbRectangle rectangle = rectangles[i];
						if (rectangle != null) {
							RfbFramebuffer framebuffer = session.getFramebuffer ();

							if (framebuffer == null) {
								VncCommon.getLogger ().warning ("Framebuffer not attached to session");
							} else {
								synchronized (framebuffer) {
									try {
										rectangle.getPixelData ().updateFramebuffer (framebuffer);
									} catch (RfbPixelDataException ex) {
										VncCommon.getLogger ().log (Level.SEVERE, null, ex);
									}
								}
							}
						} else {
							VncCommon.getLogger ().log (Level.WARNING, "Rectangle #{0} of {1} is null", new Object[]{i, rectangles.length});
						}
					}
					VncView view = session.getView ();
					if (view != null) {
						Dimension size = view.getPreferredSize ();
						session.getView ().paintImmediately (0, 0, size.width, size.height);
					}
				}
				if (incomingMessagesQueue.isEmpty ()) {
					try {
						Thread.sleep (10l);
					} catch (InterruptedException ex) {
						break;
					}
				}
			}
		}
	}

	class FramebufferUpdateRequestRunnable implements Runnable {

		@Override
		public void run () {
			int counter = 1;
			while (session.getSocket ().isConnected ()) {
				FramebufferUpdateRequestMessage furm = new FramebufferUpdateRequestMessage (session, (counter / 256.0 != 0));

				session.sendMessage (furm);

				if (counter == 256) {
					counter = 1;
				} else {
					counter++;
				}

				try {
					Thread.sleep (50l);
				} catch (InterruptedException ex) {
					break;
				}
			}
		}
	}
}
