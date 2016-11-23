/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.io;

import ru.dmerkushov.vnc.client.VncException;

/**
 *
 * @author dmerkushov
 */
public class RfbIoException extends VncException {

	public RfbIoException () {
	}

	public RfbIoException (String message) {
		super (message);
	}

	public RfbIoException (String message, Throwable cause) {
		super (message, cause);
	}

	public RfbIoException (Throwable cause) {
		super (cause);
	}

}
