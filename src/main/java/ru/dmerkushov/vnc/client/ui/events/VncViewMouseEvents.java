/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.ui.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.PointerEventMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.PointerEventMessageSequence;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class VncViewMouseEvents implements MouseListener, MouseMotionListener, MouseWheelListener {

	private final RfbClientSession session;

	public VncViewMouseEvents (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
	}

	@Override
	public void mouseClicked (MouseEvent e) {
		// Do nothing: this will be translated into two events: mousePressed(), and mouseReleased()
	}

	@Override
	public void mousePressed (MouseEvent e) {
		int x = e.getX ();
		int y = e.getY ();

		int buttonMask = PointerEventMessage.BUTTONMASK_LEFT;

		PointerEventMessage pointerEventMsg = new PointerEventMessage (session, buttonMask, x, y);
		session.sendMessage (pointerEventMsg);
	}

	@Override
	public void mouseReleased (MouseEvent e) {
		int x = e.getX ();
		int y = e.getY ();

		PointerEventMessage pointerEventMsg = new PointerEventMessage (session, 0, x, y);
		session.sendMessage (pointerEventMsg);
	}

	@Override
	public void mouseEntered (MouseEvent e) {
		int x = e.getX ();
		int y = e.getY ();

		PointerEventMessageSequence pointerEventMsg = new PointerEventMessageSequence (session, PointerEventMessageSequence.EVENTTYPE_CAMEHERE, x, y);
		session.sendMessage (pointerEventMsg);
	}

	@Override
	public void mouseExited (MouseEvent e) {
		// Do nothing, this will be not sent to VNC server
	}

	@Override
	public void mouseDragged (MouseEvent e) {
		// Do nothing, this will be not sent to VNC server
	}

	@Override
	public void mouseMoved (MouseEvent e) {
		int x = e.getX ();
		int y = e.getY ();
		PointerEventMessageSequence pointerEventMsg = new PointerEventMessageSequence (session, PointerEventMessageSequence.EVENTTYPE_CAMEHERE, x, y);
		session.sendMessage (pointerEventMsg);
	}

	@Override
	public void mouseWheelMoved (MouseWheelEvent e) {
		int x = e.getX ();
		int y = e.getY ();

		int eventType;
		int times;

		int wheelRotation = e.getWheelRotation ();
		if (wheelRotation < 0) {		// wheel rotated up
			eventType = PointerEventMessageSequence.EVENTTYPE_WHEEL_UP;
			times = -wheelRotation;
		} else {						// wheel rotated down
			eventType = PointerEventMessageSequence.EVENTTYPE_WHEEL_DOWN;
			times = wheelRotation;
		}

		PointerEventMessageSequence pointerEventMsg = new PointerEventMessageSequence (session, eventType, x, y);

		for (int i = 0; i < times; i++) {
			session.sendMessage (pointerEventMsg);
		}
	}

}
