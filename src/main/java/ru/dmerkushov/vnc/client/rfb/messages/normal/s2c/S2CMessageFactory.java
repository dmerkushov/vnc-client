/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.s2c;

import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.MessageFactoryException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.NormalMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionState;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Objects;

import static ru.dmerkushov.vnc.client.VncCommon.logger;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;

/**
 * @author dmerkushov
 */
public class S2CMessageFactory {

	private static S2CMessageFactory instance;

	private S2CMessageFactory () {
	}

	public static S2CMessageFactory getInstance () {
		if (S2CMessageFactory.instance == null) {
			S2CMessageFactory.instance = new S2CMessageFactory ();
		}
		return S2CMessageFactory.instance;
	}

	private final int maxErrorCounter = 5;
	private int errorCounter = 0;

	public S2CMessage readMessage (RfbClientSession session) throws MessageFactoryException, IOException {
		Objects.requireNonNull (session, "session");

		Socket socket = session.getSocket ();
		if (socket.isClosed ()) {
			throw new IOException ("Socket is closed");
		}

		InputStream in = socket.getInputStream ();

		int messageType = readU8 (in);

		S2CMessage message;

		switch (messageType) {
			case NormalMessage.MESSAGETYPE_S2C_FRAMEBUFFERUPDATE:
				message = new FramebufferUpdateMessage (session);
				break;
			case NormalMessage.MESSAGETYPE_S2C_SETCOLORMAPENTRIES:
				message = new SetColorMapEntriesMessage (session);
				break;
			case NormalMessage.MESSAGETYPE_S2C_BELL:
				message = new BellMessage (session);
				break;
			case NormalMessage.MESSAGETYPE_S2C_SERVERCUTTEXT:
				message = new ServerCutTextMessage (session);
				break;
			default:
				throw new MessageFactoryException ("Unknown message type: " + messageType);
		}

		try {
			message.read (in);
		} catch (MessageException ex) {
			logger.warning ("MessageException: " + ex.getMessage ());
			ex.printStackTrace ();

//			errorCounter++;
//			if (errorCounter > maxErrorCounter) {
//			System.exit (1);

			try {
				session.setSessionState (RfbSessionState.Error);
			} catch (RfbSessionException ex1) {
				throw new MessageFactoryException (ex1);
			}
//			}
		}

		return message;
	}

}
