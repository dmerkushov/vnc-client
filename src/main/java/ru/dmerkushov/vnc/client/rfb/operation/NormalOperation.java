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
import ru.dmerkushov.lib.threadhelper.AbstractTHRunnable;
import ru.dmerkushov.lib.threadhelper.ThreadHelper;
import ru.dmerkushov.lib.threadhelper.ThreadHelperException;
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

	AbstractTHRunnable getMessagesThread;
	final Queue<S2CMessage> incomingMessagesQueue;
	AbstractTHRunnable processMessagesThread;
	AbstractTHRunnable framebufferUpdateRequestThread;
	final Queue<C2SMessage> outgoingMessagesQueue;
	AbstractTHRunnable sendMessagesThread;

	public NormalOperation (RfbClientSession session) {
		super (session);

		incomingMessagesQueue = new ConcurrentLinkedQueue<> ();
		outgoingMessagesQueue = new ConcurrentLinkedQueue<> ();

		String threadGroupName = session.getThreadGroupName ();

		getMessagesThread = new GetMessagesRunnable ();
		processMessagesThread = new ProcessMessagesRunnable ();
		framebufferUpdateRequestThread = new FramebufferUpdateRequestRunnable ();
		sendMessagesThread = new SendMessagesRunnable ();

		ThreadHelper.getInstance ().addRunnable (threadGroupName, getMessagesThread);
		ThreadHelper.getInstance ().addRunnable (threadGroupName, processMessagesThread);
		ThreadHelper.getInstance ().addRunnable (threadGroupName, framebufferUpdateRequestThread);
		ThreadHelper.getInstance ().addRunnable (threadGroupName, sendMessagesThread);
	}

	@Override
	public void operate () {
		try {
			getMessagesThread.start ();
		} catch (ThreadHelperException ex) {
			VncCommon.getLogger ().log (Level.SEVERE, null, ex);
			return;
		}
		try {
			processMessagesThread.start ();
		} catch (ThreadHelperException ex) {
			VncCommon.getLogger ().log (Level.SEVERE, null, ex);
			return;
		}
		try {
			framebufferUpdateRequestThread.start ();
		} catch (ThreadHelperException ex) {
			VncCommon.getLogger ().log (Level.SEVERE, null, ex);
			return;
		}
		try {
			sendMessagesThread.start ();
		} catch (ThreadHelperException ex) {
			VncCommon.getLogger ().log (Level.SEVERE, null, ex);
			return;
		}
	}

	public void sendMessage (C2SMessage message) {
		Objects.requireNonNull (message, "message");

		outgoingMessagesQueue.add (message);
	}

	class GetMessagesRunnable extends AbstractTHRunnable {

		private boolean goOn = true;

		@Override
		public void doSomething () {
			while (goOn) {
				S2CMessage message = null;
				try {
					message = S2CMessageFactory.getInstance ().readMessage (session);
				} catch (MessageFactoryException | IOException ex) {
					VncCommon.getLogger ().log (Level.SEVERE, null, ex);
				}
				if (message == null) {	// Means the socket is closed by the server
					try {
						VncCommon.getLogger ().log (Level.WARNING, "Finishing threads for VNC session because incoming message is null (probably VNC server has closed TCP connection): session {0}, socket connected? - {1}", new Object[]{session.toString (), session.getSocket ().isConnected ()});
						ThreadHelper.getInstance ().finish (session.getThreadGroupName (), 1000l);
					} catch (ThreadHelperException ex) {
						VncCommon.getLogger ().log (Level.SEVERE, null, ex);
					}
				} else {
					incomingMessagesQueue.add (message);
				}
			}
		}

		@Override
		public void finish () throws ThreadHelperException {
			goOn = false;
		}
	}

	class SendMessagesRunnable extends AbstractTHRunnable {

		private boolean goOn = true;

		@Override
		public void doSomething () {
			OutputStream out = session.getOut ();
			C2SMessage message = null;
			while (goOn) {
				message = outgoingMessagesQueue.poll ();

				if (message != null) {
					try {
						message.write (out);
					} catch (MessageException | IOException ex) {
						VncCommon.getLogger ().log (Level.SEVERE, null, ex);
					}
				}

				if (outgoingMessagesQueue.isEmpty ()) {
					try {
						Thread.sleep (10l);
					} catch (InterruptedException ex) {
					}
				}
			}
		}

		@Override
		public void finish () throws ThreadHelperException {
			goOn = false;
		}
	}

	class ProcessMessagesRunnable extends AbstractTHRunnable {

		private boolean goOn = true;

		@Override
		public void doSomething () {
			while (goOn) {
				S2CMessage message = incomingMessagesQueue.poll ();
				if (message != null && message instanceof FramebufferUpdateMessage && session.isFramebufferAttached ()) {
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
					}
				}
			}
		}

		@Override
		public void finish () throws ThreadHelperException {
			goOn = false;
		}
	}

	class FramebufferUpdateRequestRunnable extends AbstractTHRunnable {

		private boolean goOn = true;

		@Override
		public void doSomething () {
			int counter = 1;
			while (goOn && session.getSocket ().isConnected ()) {
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

		@Override
		public void finish () throws ThreadHelperException {
			goOn = false;
		}
	}
}
