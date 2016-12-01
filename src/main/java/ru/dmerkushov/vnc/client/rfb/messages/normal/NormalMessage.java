/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 *
 * @author dmerkushov
 */
public abstract class NormalMessage extends RfbMessage {

	private int messageType;

	public NormalMessage (RfbSession session) {
		super (session);
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		writeU8 (out, messageType);
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		messageType = readU8 (in);
	}

}
