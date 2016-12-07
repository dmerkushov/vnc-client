/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.c2s;

import java.io.IOException;
import java.io.OutputStream;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.NormalMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class KeyEventMessageSequence extends C2SMessage {

	public static final int EVENTTYPE_PRESSED = 0;
	public static final int EVENTTYPE_RELEASED = 1;
	public static final int EVENTTYPE_TYPED = 2;

	private int eventType;
	private int keySym;

	/**
	 *
	 * @param session
	 * @param eventType
	 */
	public KeyEventMessageSequence (RfbClientSession session, int eventType, int keySym) {
		super (session, NormalMessage.MESSAGETYPE_C2S_POINTEREVENT);

		this.eventType = eventType;
		this.keySym = keySym;
	}

	@Override
	public void write (OutputStream out) throws IOException, MessageException {

		switch (eventType) {
			case EVENTTYPE_PRESSED:
				new KeyEventMessage (getSession (), keySym, true).write (out);
				break;
			case EVENTTYPE_RELEASED:
				new KeyEventMessage (getSession (), keySym, false).write (out);
				break;
			case EVENTTYPE_TYPED:
				new KeyEventMessage (getSession (), keySym, true).write (out);
				new KeyEventMessage (getSession (), keySym, false).write (out);
				break;
			default:
				throw new MessageException ("Unknown eventType: " + eventType);
		}
	}

}
