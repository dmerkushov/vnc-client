/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

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
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionState;
import ru.dmerkushov.vnc.client.ui.VncView;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static ru.dmerkushov.vnc.client.VncCommon.vncPrefs;

/**
 * @author dmerkushov
 */
public class NormalOperation extends Operation {

	private AbstractTHRunnable getMessagesThread;
	private final Queue<S2CMessage> incomingMessagesQueue;
	private AbstractTHRunnable processMessagesThread;
	private AbstractTHRunnable framebufferUpdateRequestThread;
	private final Queue<C2SMessage> outgoingMessagesQueue;
	private AbstractTHRunnable sendMessagesThread;

	public NormalOperation (RfbClientSession session) {
		super (session);

		this.incomingMessagesQueue = new ConcurrentLinkedQueue<> ();
		this.outgoingMessagesQueue = new ConcurrentLinkedQueue<> ();

		String threadGroupName = session.getThreadGroupName ();

		this.getMessagesThread = new GetMessagesRunnable ();
		this.processMessagesThread = new ProcessMessagesRunnable ();
		this.framebufferUpdateRequestThread = new FramebufferUpdateRequestRunnable ();
		this.sendMessagesThread = new SendMessagesRunnable ();

		ThreadHelper.getInstance ().addRunnable (threadGroupName, this.getMessagesThread);
		ThreadHelper.getInstance ().addRunnable (threadGroupName, this.processMessagesThread);
		ThreadHelper.getInstance ().addRunnable (threadGroupName, this.framebufferUpdateRequestThread);
		ThreadHelper.getInstance ().addRunnable (threadGroupName, this.sendMessagesThread);
	}

	@Override
	public void operate () {
		try {
			this.getMessagesThread.start ();
		} catch (ThreadHelperException ex) {
			VncCommon.getLogger ().log (Level.SEVERE, null, ex);
			return;
		}
		try {
			this.processMessagesThread.start ();
		} catch (ThreadHelperException ex) {
			VncCommon.getLogger ().log (Level.SEVERE, null, ex);
			return;
		}
		try {
			this.framebufferUpdateRequestThread.start ();
		} catch (ThreadHelperException ex) {
			VncCommon.getLogger ().log (Level.SEVERE, null, ex);
			return;
		}
		try {
			this.sendMessagesThread.start ();
		} catch (ThreadHelperException ex) {
			VncCommon.getLogger ().log (Level.SEVERE, null, ex);
			return;
		}
	}

	public void sendMessage (C2SMessage message) {
		Objects.requireNonNull (message, "message");

		this.outgoingMessagesQueue.add (message);
	}

	class GetMessagesRunnable extends AbstractTHRunnable {

		private boolean goOn = true;

		@Override
		public void doSomething () {
			while (this.goOn && ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSocket ().isConnected ()) {
				S2CMessage message = null;

				try {
					message = S2CMessageFactory.getInstance ().readMessage (ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session);
				} catch (IOException ex) {
					if (ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSessionState () == RfbSessionState.Finished) {
						return;
					}
					VncCommon.getLogger ().log (Level.SEVERE, null, ex);
				} catch (MessageFactoryException ex) {
					VncCommon.getLogger ().log (Level.SEVERE, null, ex);
				}
				if (message == null && ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSessionState () != RfbSessionState.Finished) {    // Means the socket is closed by the server
					VncCommon.getLogger ().log (Level.WARNING, "Setting session state to Error because incoming message is null (probably VNC server has closed TCP connection): session {0}, socket connected? - {1}", new Object[]{ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.toString (), ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSocket ().isConnected ()});

					// DEBUG
					try {
						ThreadHelper.getInstance ().finish (ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getThreadGroupName (), 0);
					} catch (ThreadHelperException ex) {
						VncCommon.getLogger ().throwing ("", "", ex);
						System.exit (1);
					}

					try {
						ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.setSessionState (RfbSessionState.Error);
					} catch (RfbSessionException ex) {
						VncCommon.getLogger ().log (Level.SEVERE, null, ex);
					}
					try {
						Thread.sleep (1000L);
					} catch (InterruptedException ex) {
						VncCommon.getLogger ().throwing ("", "", ex);
					}
				} else if (!ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.isSuspended ()) {
					ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.incomingMessagesQueue.add (message);
				}
			}
		}

		@Override
		public void finish () throws ThreadHelperException {
			this.goOn = false;
		}
	}

	class SendMessagesRunnable extends AbstractTHRunnable {

		private boolean goOn = true;

		@Override
		public void doSomething () {
			OutputStream out;
			C2SMessage message;
			while (this.goOn && ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSocket ().isConnected ()) {
				message = ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.outgoingMessagesQueue.poll ();

				if (message != null && !ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.isSuspended ()) {
					try {
						out = ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getOut ();
					} catch (RfbSessionException ex) {
						VncCommon.getLogger ().log (Level.WARNING, null, ex);
						if (ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSessionState () == RfbSessionState.Finished) {
							return;
						} else {
							try {
								ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.setSessionState (RfbSessionState.Error);
							} catch (RfbSessionException ex1) {
								VncCommon.getLogger ().log (Level.SEVERE, null, ex1);
							}
							try {
								Thread.sleep (1000L);
							} catch (InterruptedException ex1) {
								VncCommon.getLogger ().log (Level.SEVERE, null, ex1);
							}
							try {
								out = ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getOut ();
							} catch (RfbSessionException ex1) {
								VncCommon.getLogger ().log (Level.SEVERE, null, ex1);
								return;
							}
						}
					}
					try {
						message.write (out);
					} catch (MessageException ex) {
						VncCommon.getLogger ().log (Level.SEVERE, null, ex);
					} catch (IOException ex) {
						if (ex instanceof SocketException) {
							VncCommon.getLogger ().log (Level.WARNING, "Socket broken (probably 'broken pipe' exception caught). Trying to restart the session", ex);
							try {
								ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.restartSession (RfbSessionState.Error);
							} catch (RfbSessionException | IOException ex1) {
								VncCommon.getLogger ().log (Level.SEVERE, null, ex1);
							}
						}
						VncCommon.getLogger ().log (Level.SEVERE, null, ex);
						if (ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSessionState () != RfbSessionState.Finished) {
							try {
								ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.setSessionState (RfbSessionState.Error);
							} catch (RfbSessionException ex1) {
								VncCommon.getLogger ().log (Level.SEVERE, null, ex1);
							}
						}
					}
				}

				try {
					Thread.sleep (10L);
				} catch (InterruptedException ex) {
					VncCommon.getLogger ().log (Level.SEVERE, null, ex);
				}
			}
		}

		@Override
		public void finish () throws ThreadHelperException {
			this.goOn = false;
		}
	}

	class ProcessMessagesRunnable extends AbstractTHRunnable {

		private boolean goOn = true;

		@Override
		public void doSomething () {
			while (this.goOn && ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSocket ().isConnected ()) {
				S2CMessage message = ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.incomingMessagesQueue.poll ();
				if (message != null && message instanceof FramebufferUpdateMessage && ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.isFramebufferAttached () && !ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.isSuspended ()) {
					FramebufferUpdateMessage fbuMessage = (FramebufferUpdateMessage) message;

					RfbRectangle[] rectangles = fbuMessage.getRectangles ();
					for (int i = 0; i < rectangles.length; i++) {
						RfbRectangle rectangle = rectangles[i];
						if (rectangle != null) {
							RfbFramebuffer framebuffer = ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getFramebuffer ();

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
					Set<VncView> vncViews = ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getViews ();
					for (VncView vncView : vncViews) {
						Dimension size = vncView.getPreferredSize ();
						vncView.paintNow (0, 0, size.width, size.height);
					}
				}
				if (ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.incomingMessagesQueue.isEmpty ()) {
					try {
						Thread.sleep (10L);
					} catch (InterruptedException ex) {
						VncCommon.getLogger ().log (Level.SEVERE, null, ex);
					}
				}
			}
		}

		@Override
		public void finish () throws ThreadHelperException {
			this.goOn = false;
		}
	}

	class FramebufferUpdateRequestRunnable extends AbstractTHRunnable {

		private boolean goOn = true;

		@Override
		public void doSomething () {
			int counter = 0;

			long framebufferUpdateDelay = vncPrefs.getLong ("FRAMEBUFFER_UPDATE_DELAY", 100L);
			long fullUpdateCounter = vncPrefs.getLong ("FULL_UPDATE_COUNTER", 256L);

			System.err.println ("Framebuffer update delay: " + framebufferUpdateDelay);
			System.err.println ("Full update counter: " + fullUpdateCounter);

			while (this.goOn && ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.getSocket ().isConnected ()) {
				FramebufferUpdateRequestMessage furm = new FramebufferUpdateRequestMessage (ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session, (counter % fullUpdateCounter != 0));

				ru.dmerkushov.vnc.client.rfb.operation.NormalOperation.this.session.sendMessage (furm);

				if (counter == fullUpdateCounter) {
					counter = 1;
				} else {
					counter++;
				}

				try {
					Thread.sleep (framebufferUpdateDelay);
				} catch (InterruptedException ex) {
					break;
				}
			}
		}

		@Override
		public void finish () throws ThreadHelperException {
			this.goOn = false;
		}
	}
}
