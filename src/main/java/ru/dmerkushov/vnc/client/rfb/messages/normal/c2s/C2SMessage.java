/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.c2s;

import ru.dmerkushov.vnc.client.rfb.messages.normal.NormalMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public abstract class C2SMessage extends NormalMessage {

	public C2SMessage (RfbClientSession session) {
		super (session);
	}

}
