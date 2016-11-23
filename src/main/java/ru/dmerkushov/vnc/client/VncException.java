/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client;

/**
 *
 * @author dmerkushov
 */
public class VncException extends Exception {

	public VncException () {
	}

	public VncException (String message) {
		super (message);
	}

	public VncException (String message, Throwable cause) {
		super (message, cause);
	}

	public VncException (Throwable cause) {
		super (cause);
	}

}
