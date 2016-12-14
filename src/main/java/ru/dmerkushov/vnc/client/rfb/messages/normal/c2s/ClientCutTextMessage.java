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
public class ClientCutTextMessage extends C2SMessage {

	public ClientCutTextMessage (RfbClientSession session) {
		super (session, NormalMessage.MESSAGETYPE_C2S_CLIENTCUTTEXT);
	}

	//TODO Implement ClientCutTextMessage
}
