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
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class PointerEventMessage extends C2SMessage {

	public static final int BUTTONMASK_LEFT = 1;
	public static final int BUTTONMASK_MIDDLE = 2;
	public static final int BUTTONMASK_RIGHT = 4;
	public static final int BUTTONMASK_WHEELUP = 8;
	public static final int BUTTONMASK_WHEELDOWN = 16;

	private int buttonMask;
	private int x;
	private int y;

	/**
	 *
	 * @param session
	 * @param buttonMask
	 * @param x
	 * @param y
	 */
	public PointerEventMessage (RfbClientSession session, int buttonMask, int x, int y) {
		super (session, NormalMessage.MESSAGETYPE_C2S_POINTEREVENT);

		this.buttonMask = buttonMask;
		this.x = x;
		this.y = y;
	}

	@Override
	public void read (InputStream in) throws IOException, MessageException {
		buttonMask = readU8 (in);
		x = readU16 (in, true);
		y = readU16 (in, true);
	}

	@Override
	public void write (OutputStream out) throws IOException, MessageException {
		super.write (out);
		writeU8 (out, buttonMask);
		writeU16 (out, x, true);
		writeU16 (out, y, true);
	}
}
