package ru.dmerkushov.vnc.client.ui.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.KeyEventMessageSequence;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class SwingVncViewKeyboardEvents implements KeyListener {

	private final RfbClientSession session;

	public SwingVncViewKeyboardEvents (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
	}

	@Override
	public void keyTyped (KeyEvent e) {
		// Do nothing: this will be translated into two events: keyPressed(), and keyReleased()
	}

	@Override
	public void keyPressed (KeyEvent e) {
		int keySym = Keysyms.translateAwtKeyEvent (e);

		KeyEventMessageSequence seq = new KeyEventMessageSequence (session, KeyEventMessageSequence.EVENTTYPE_PRESSED, keySym);
		session.sendMessage (seq);
	}

	@Override
	public void keyReleased (KeyEvent e) {
		int keySym = Keysyms.translateAwtKeyEvent (e);

		KeyEventMessageSequence seq = new KeyEventMessageSequence (session, KeyEventMessageSequence.EVENTTYPE_RELEASED, keySym);
		session.sendMessage (seq);
	}

}
