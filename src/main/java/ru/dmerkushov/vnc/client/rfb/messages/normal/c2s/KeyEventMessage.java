/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.c2s;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.NormalMessage;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readS32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeS32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class KeyEventMessage extends C2SMessage {

	int keySym;
	boolean downFlag;

	public KeyEventMessage (RfbClientSession session, int keySym, boolean downFlag) {
		super (session, NormalMessage.MESSAGETYPE_C2S_KEYEVENT);

		this.keySym = keySym;
		this.downFlag = downFlag;
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		downFlag = readBoolean (in);
		readU8 (in);
		readU8 (in);
		keySym = readS32 (in, true);
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		super.write (out);

		writeBoolean (out, downFlag);
		writeU8 (out, (byte) 0);
		writeU8 (out, (byte) 0);
		writeS32 (out, keySym, true);
	}

}
