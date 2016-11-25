/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages;

import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 *
 * @author dmerkushov
 */
public abstract class RfbMessage implements Message {

	final RfbSession session;

	public RfbMessage (RfbSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
	}

	public final RfbSession getSession () {
		return session;
	}

}
