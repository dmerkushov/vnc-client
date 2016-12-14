/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages;

import ru.dmerkushov.vnc.client.rfb.operation.RfbOperationException;

/**
 *
 * @author dmerkushov
 */
public class MessageException extends RfbOperationException {

	public MessageException () {
	}

	public MessageException (String message) {
		super (message);
	}

	public MessageException (String message, Throwable cause) {
		super (message, cause);
	}

	public MessageException (Throwable cause) {
		super (cause);
	}

}
