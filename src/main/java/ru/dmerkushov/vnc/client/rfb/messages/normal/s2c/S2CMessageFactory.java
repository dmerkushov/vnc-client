/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.s2c;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.MessageFactoryException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 *
 * @author dmerkushov
 */
public class S2CMessageFactory {

	private static S2CMessageFactory instance;

	private S2CMessageFactory () {
	}

	public static S2CMessageFactory getInstance () {
		if (instance == null) {
			instance = new S2CMessageFactory ();
		}
		return instance;
	}

	public S2CMessage readMessage (RfbSession session, InputStream in) throws MessageFactoryException, IOException {
		Objects.requireNonNull (session, "session");
		Objects.requireNonNull (in, "in");

		int messageType = readU8 (in);

		S2CMessage message;

		switch (messageType) {
			case 0:
				message = new FramebufferUpdateMessage (session);
				break;
			case 1:
				message = new SetColorMapEntriesMessage (session);
				break;
			case 2:
				message = new BellMessage (session);
				break;
			case 3:
				message = new ServerCutTextMessage (session);
				break;
			default:
				throw new MessageFactoryException ("Unknown message type: " + messageType);
		}

		try {
			message.read (in);
		} catch (MessageException ex) {
			throw new MessageFactoryException (ex);
		}

		return message;
	}

}
