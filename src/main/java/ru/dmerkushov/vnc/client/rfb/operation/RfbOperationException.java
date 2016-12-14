/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;

/**
 *
 * @author dmerkushov
 */
public class RfbOperationException extends RfbSessionException {

	public RfbOperationException () {
	}

	public RfbOperationException (String message) {
		super (message);
	}

	public RfbOperationException (String message, Throwable cause) {
		super (message, cause);
	}

	public RfbOperationException (Throwable cause) {
		super (cause);
	}

}
