/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session;

import java.util.HashMap;

/**
 *
 * @author dmerkushov
 */
public enum RfbSecurityType {

	Invalid (0),
	None (1),
	VNC (2);

	private final int value;
	private static final HashMap<Integer, RfbSecurityType> secTypes = new HashMap<> ();

	static {
		for (RfbSecurityType secType : RfbSecurityType.values ()) {
			secTypes.put (secType.getValue (), secType);
		}
	}

	RfbSecurityType (int value) {
		this.value = value;
	}

	public int getValue () {
		return value;
	}

	public static RfbSecurityType getSecTypeByValue (int value) {
		return secTypes.get (value);
	}

}
