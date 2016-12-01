/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal;

import ru.dmerkushov.vnc.client.rfb.messages.MessageException;

/**
 *
 * @author dmerkushov
 */
public class MessageFactoryException extends MessageException {

	public MessageFactoryException () {
	}

	public MessageFactoryException (String message) {
		super (message);
	}

	public MessageFactoryException (String message, Throwable cause) {
		super (message, cause);
	}

	public MessageFactoryException (Throwable cause) {
		super (cause);
	}

}
