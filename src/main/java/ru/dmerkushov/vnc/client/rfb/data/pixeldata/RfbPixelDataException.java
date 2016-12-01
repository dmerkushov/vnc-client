/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import ru.dmerkushov.vnc.client.VncException;

/**
 *
 * @author dmerkushov
 */
public class RfbPixelDataException extends VncException {

	public RfbPixelDataException () {
	}

	public RfbPixelDataException (String message) {
		super (message);
	}

	public RfbPixelDataException (String message, Throwable cause) {
		super (message, cause);
	}

	public RfbPixelDataException (Throwable cause) {
		super (cause);
	}

}
