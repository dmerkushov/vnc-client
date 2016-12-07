/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.s2c;

import ru.dmerkushov.vnc.client.rfb.messages.normal.NormalMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class BellMessage extends S2CMessage {

	public BellMessage (RfbClientSession session) {
		super (session, NormalMessage.MESSAGETYPE_S2C_BELL);
	}
}
