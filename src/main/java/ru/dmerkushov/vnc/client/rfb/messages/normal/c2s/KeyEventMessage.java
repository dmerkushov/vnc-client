/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.c2s;

import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class KeyEventMessage extends C2SMessage {

	public KeyEventMessage (RfbClientSession session) {
		super (session);
	}

	//TODO Implement KeyEventMessage
}
