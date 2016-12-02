/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class NormalOperation extends Operation {

	public NormalOperation (RfbClientSession session) {
		super (session);
	}

	@Override
	public void operate () {
		//TODO Implement operate() in NormalOperation
		throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
