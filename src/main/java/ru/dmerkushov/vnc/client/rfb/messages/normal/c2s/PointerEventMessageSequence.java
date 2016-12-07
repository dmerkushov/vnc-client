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
import static ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.PointerEventMessage.BUTTONMASK_LEFT;
import static ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.PointerEventMessage.BUTTONMASK_RIGHT;
import static ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.PointerEventMessage.BUTTONMASK_WHEELDOWN;
import static ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.PointerEventMessage.BUTTONMASK_WHEELUP;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class PointerEventMessageSequence extends C2SMessage {

	public static final int EVENTTYPE_CAMEHERE = 0;
	public static final int EVENTTYPE_CLICK_LEFT = 1;
	public static final int EVENTTYPE_CLICK_RIGHT = 2;
	public static final int EVENTTYPE_WHEEL_DOWN = 3;
	public static final int EVENTTYPE_WHEEL_UP = 4;

	private int eventType;
	private int x;
	private int y;

	/**
	 *
	 * @param session
	 * @param eventType
	 */
	public PointerEventMessageSequence (RfbClientSession session, int eventType, int x, int y) {
		super (session, NormalMessage.MESSAGETYPE_C2S_POINTEREVENT);

		this.eventType = eventType;
		this.x = x;
		this.y = y;
	}

	@Override
	public void write (OutputStream out) throws IOException, MessageException {

		switch (eventType) {
			case EVENTTYPE_CAMEHERE:
				new PointerEventMessage (getSession (), 0, x, y).write (out);
				break;
			case EVENTTYPE_CLICK_LEFT:
				new PointerEventMessage (getSession (), BUTTONMASK_LEFT, x, y).write (out);
				new PointerEventMessage (getSession (), 0, x, y).write (out);
				break;
			case EVENTTYPE_CLICK_RIGHT:
				new PointerEventMessage (getSession (), BUTTONMASK_RIGHT, x, y).write (out);
				new PointerEventMessage (getSession (), 0, x, y).write (out);
				break;
			case EVENTTYPE_WHEEL_DOWN:
				new PointerEventMessage (getSession (), BUTTONMASK_WHEELDOWN, x, y).write (out);
				new PointerEventMessage (getSession (), 0, x, y).write (out);
				break;
			case EVENTTYPE_WHEEL_UP:
				new PointerEventMessage (getSession (), BUTTONMASK_WHEELUP, x, y).write (out);
				new PointerEventMessage (getSession (), 0, x, y).write (out);
				break;
			default:
				throw new MessageException ("Unknown eventType: " + eventType);
		}
	}

}
