/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session;

import ru.dmerkushov.vnc.client.VncException;

/**
 *
 * @author dmerkushov
 */
public class RfbSessionException extends VncException {

	public RfbSessionException () {
	}

	public RfbSessionException (String message) {
		super (message);
	}

	public RfbSessionException (String message, Throwable cause) {
		super (message, cause);
	}

	public RfbSessionException (Throwable cause) {
		super (cause);
	}

}
