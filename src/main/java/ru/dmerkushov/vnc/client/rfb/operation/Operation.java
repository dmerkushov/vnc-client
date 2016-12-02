/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import java.io.IOException;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public abstract class Operation {

	final RfbClientSession session;

	public Operation (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;
	}

	public abstract void operate () throws IOException, RfbOperationException;

}
