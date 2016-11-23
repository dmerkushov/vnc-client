/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session;

/**
 *
 * @author dmerkushov
 */
public enum RfbSessionState {

	Initial (0), Handshake_ProtocolVersion (1), Handshake_Security (2), Handshake_SecurityResult (3), Init_Client (3), Init_Server (4), Started (5), Finished (6), Error (7);
	int value;

	RfbSessionState (int value) {
		this.value = value;
	}

	public int value () {
		return value;
	}

}
