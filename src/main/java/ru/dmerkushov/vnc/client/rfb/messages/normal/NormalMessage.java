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
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public abstract class NormalMessage extends RfbMessage {

	public static final int MESSAGETYPE_C2S_SETPIXELFORMAT = 0;
	public static final int MESSAGETYPE_C2S_SETENCODINGS = 2;
	public static final int MESSAGETYPE_C2S_FRAMEBUFFERUPDATEREQUEST = 3;
	public static final int MESSAGETYPE_C2S_KEYEVENT = 4;
	public static final int MESSAGETYPE_C2S_POINTEREVENT = 5;
	public static final int MESSAGETYPE_C2S_CLIENTCUTTEXT = 6;
	public static final int MESSAGETYPE_S2C_FRAMEBUFFERUPDATE = 0;
	public static final int MESSAGETYPE_S2C_SETCOLORMAPENTRIES = 1;
	public static final int MESSAGETYPE_S2C_BELL = 2;
	public static final int MESSAGETYPE_S2C_SERVERCUTTEXT = 3;

	private final int messageType;

	public NormalMessage (RfbClientSession session, int messageType) {
		super (session);

		this.messageType = messageType;
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		writeU8 (out, messageType);
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
	}

	public int getMessageType () {
		return messageType;
	}

}
