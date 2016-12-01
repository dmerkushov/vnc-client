/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.c2s;

import java.io.IOException;
import java.io.InputStream;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 *
 * @author dmerkushov
 */
public class C2SMessageFactory {

	private static C2SMessageFactory instance;

	private C2SMessageFactory () {
	}

	public static C2SMessageFactory getInstance () {
		if (instance == null) {
			instance = new C2SMessageFactory ();
		}
		return instance;
	}

	public C2SMessage readMessage (RfbSession session, InputStream in) throws IOException {

	}

}
