/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import ru.dmerkushov.lib.threadhelper.AbstractTHRunnable;
import ru.dmerkushov.lib.threadhelper.ThreadHelper;
import ru.dmerkushov.lib.threadhelper.ThreadHelperException;
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

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static ru.dmerkushov.vnc.client.VncCommon.logger;
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
			logger.log (Level.SEVERE, null, ex);
			return;
		}
		try {
			this.processMessagesThread.start ();
		} catch (ThreadHelperException ex) {
			logger.log (Level.SEVERE, null, ex);
			return;
		}
		try {
			this.framebufferUpdateRequestThread.start ();
		} catch (ThreadHelperException ex) {
			logger.log (Level.SEVERE, null, ex);
			return;
		}
		try {
			this.sendMessagesThread.start ();
		} catch (ThreadHelperException ex) {
			logger.log (Level.SEVERE, null, ex);
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
			while (this.goOn && NormalOperation.this.session.getSocket ().isConnected ()) {
				S2CMessage message = null;

				try {
					message = S2CMessageFactory.getInstance ().readMessage (NormalOperation.this.session);
				} catch (IOException ex) {
					if (NormalOperation.this.session.getSessionState () == RfbSessionState.Finished) {
						return;
					}
					logger.log (Level.SEVERE, null, ex);
				} catch (MessageFactoryException ex) {
					logger.log (Level.SEVERE, null, ex);
				}
				if (message == null && NormalOperation.this.session.getSessionState () != RfbSessionState.Finished) {    // Means the socket is closed by the server
					logger.log (Level.WARNING, "Setting session state to Error because incoming message is null (probably VNC server has closed TCP connection): session {0}, socket connected? - {1}", new Object[]{NormalOperation.this.session.toString (), NormalOperation.this.session.getSocket ().isConnected ()});

//					try {
//						ThreadHelper.getInstance ().finish (session.getThreadGroupName (), 0);
//					} catch (ThreadHelperException ex) {
//						logger.throwing ("", "", ex);
//						System.exit (1);
//					}

					try {
						NormalOperation.this.session.setSessionState (RfbSessionState.Error);
					} catch (RfbSessionException ex) {
						logger.log (Level.SEVERE, null, ex);
					}
					try {
						Thread.sleep (1000L);
					} catch (InterruptedException ex) {
						logger.throwing ("", "", ex);
					}
				} else if (!NormalOperation.this.session.isSuspended ()) {
					NormalOperation.this.incomingMessagesQueue.add (message);
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
			while (this.goOn && NormalOperation.this.session.getSocket ().isConnected ()) {
				message = NormalOperation.this.outgoingMessagesQueue.poll ();

				if (message != null && !NormalOperation.this.session.isSuspended ()) {
					try {
						out = NormalOperation.this.session.getOut ();
					} catch (RfbSessionException ex) {
						logger.log (Level.WARNING, null, ex);
						if (NormalOperation.this.session.getSessionState () == RfbSessionState.Finished) {
							return;
						} else {
							try {
								NormalOperation.this.session.setSessionState (RfbSessionState.Error);
							} catch (RfbSessionException ex1) {
								logger.log (Level.SEVERE, null, ex1);
							}
							try {
								Thread.sleep (1000L);
							} catch (InterruptedException ex1) {
								logger.log (Level.SEVERE, null, ex1);
							}
							try {
								out = NormalOperation.this.session.getOut ();
							} catch (RfbSessionException ex1) {
								logger.log (Level.SEVERE, null, ex1);
								return;
							}
						}
					}
					try {
						message.write (out);
					} catch (MessageException ex) {
						logger.log (Level.SEVERE, null, ex);
					} catch (IOException ex) {
						if (ex instanceof SocketException) {
							logger.log (Level.WARNING, "Socket broken (probably 'broken pipe' exception caught). Trying to restart the session", ex);
							try {
								NormalOperation.this.session.restartSession (RfbSessionState.Error);
							} catch (RfbSessionException | IOException ex1) {
								logger.log (Level.SEVERE, null, ex1);
							}
						}
						logger.log (Level.SEVERE, null, ex);
						if (NormalOperation.this.session.getSessionState () != RfbSessionState.Finished) {
							try {
								NormalOperation.this.session.setSessionState (RfbSessionState.Error);
							} catch (RfbSessionException ex1) {
								logger.log (Level.SEVERE, null, ex1);
							}
						}
					}
				}

				try {
					Thread.sleep (10L);
				} catch (InterruptedException ex) {
					logger.log (Level.SEVERE, null, ex);
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
			while (this.goOn && NormalOperation.this.session.getSocket ().isConnected ()) {
				S2CMessage message = NormalOperation.this.incomingMessagesQueue.poll ();
				if (message != null && message instanceof FramebufferUpdateMessage && NormalOperation.this.session.isFramebufferAttached () && !NormalOperation.this.session.isSuspended ()) {
					FramebufferUpdateMessage fbuMessage = (FramebufferUpdateMessage) message;

					RfbRectangle[] rectangles = fbuMessage.getRectangles ();
					for (int i = 0; i < rectangles.length; i++) {
						RfbRectangle rectangle = rectangles[i];
						if (rectangle != null) {
							RfbFramebuffer framebuffer = NormalOperation.this.session.getFramebuffer ();

							if (framebuffer == null) {
								logger.warning ("Framebuffer not attached to session");
							} else {
								synchronized (framebuffer) {
									try {
										rectangle.getPixelData ().updateFramebuffer (framebuffer);
									} catch (RfbPixelDataException ex) {
										logger.log (Level.SEVERE, null, ex);
									}
								}
							}
						} else {
							logger.log (Level.WARNING, "Rectangle #{0} of {1} is null", new Object[]{i, rectangles.length});
						}
					}
					Set<VncView> vncViews = NormalOperation.this.session.getViews ();
					for (VncView vncView : vncViews) {
						Dimension size = vncView.getPreferredSize ();
						vncView.paintNow (0, 0, size.width, size.height);
					}
				}
				if (NormalOperation.this.incomingMessagesQueue.isEmpty ()) {
					try {
						Thread.sleep (10L);
					} catch (InterruptedException ex) {
						logger.log (Level.SEVERE, null, ex);
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

			logger.info ("Framebuffer update delay: " + framebufferUpdateDelay);
			logger.info ("Full update counter: " + fullUpdateCounter);

			while (this.goOn && NormalOperation.this.session.getSocket ().isConnected ()) {
				FramebufferUpdateRequestMessage furm = new FramebufferUpdateRequestMessage (NormalOperation.this.session, (counter % fullUpdateCounter != 0));

				NormalOperation.this.session.sendMessage (furm);

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
