/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbPixelDataException;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.MessageFactoryException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.FramebufferUpdateRequestMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.s2c.FramebufferUpdateMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.s2c.S2CMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.s2c.S2CMessageFactory;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class NormalOperation extends Operation {

	Thread getMessagesThread;
	final Queue<S2CMessage> messagesQueue;
	Thread processMessagesThread;
	Thread framebufferUpdateRequestThread;

	public NormalOperation (RfbClientSession session) {
		super (session);

		getMessagesThread = new Thread (new GetMessagesRunnable ());
		processMessagesThread = new Thread (new ProcessMessagesRunnable ());
		framebufferUpdateRequestThread = new Thread (new FramebufferUpdateRequestRunnable ());
		messagesQueue = new ConcurrentLinkedQueue<> ();
	}

	@Override
	public void operate () {
		getMessagesThread.start ();
		processMessagesThread.start ();
//		framebufferUpdateRequestThread.start ();
	}

	class GetMessagesRunnable implements Runnable {

		@Override
		public void run () {
			InputStream in = session.getIn ();
			while (true) {
				S2CMessage message = null;
				try {
					message = S2CMessageFactory.getInstance ().readMessage (session, in);
				} catch (MessageFactoryException | IOException ex) {
					Logger.getLogger (NormalOperation.class.getName ()).log (Level.SEVERE, null, ex);
				}
				if (message == null) {
					processMessagesThread.interrupt ();
					break;
				}
				messagesQueue.add (message);
			}
		}

	}

	class ProcessMessagesRunnable implements Runnable {

		@Override
		public void run () {
			while (true) {
				S2CMessage message = messagesQueue.poll ();
				if (message instanceof FramebufferUpdateMessage && session.isFramebufferAttached ()) {
					FramebufferUpdateMessage fbuMessage = (FramebufferUpdateMessage) message;
					RfbRectangle[] rectangles = fbuMessage.getRectangles ();
					for (RfbRectangle rectangle : rectangles) {
						try {
							rectangle.getPixelData ().updateFramebuffer (session.getFramebuffer ());
						} catch (RfbPixelDataException ex) {
							Logger.getLogger (NormalOperation.class.getName ()).log (Level.SEVERE, null, ex);
						}
					}
				}
				if (messagesQueue.isEmpty ()) {
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
			OutputStream out = session.getOut ();
			while (session.getSocket ().isConnected ()) {
				FramebufferUpdateRequestMessage furm = new FramebufferUpdateRequestMessage (session, session.getFramebuffer ());
				try {
					furm.write (out);
				} catch (MessageException | IOException ex) {
					Logger.getLogger (NormalOperation.class.getName ()).log (Level.SEVERE, null, ex);
				}
				try {
					Thread.sleep (100l);
				} catch (InterruptedException ex) {
					break;
				}
			}
		}

	}

}
