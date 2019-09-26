/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client;

import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * @author dmerkushov
 */
public class VncCommon {

	/**
	 * The only supported encoding for RFB is ISO-8859-1 (Latin-1). Other encodings are not supported (see RFC 6143,
	 * sections 7.5.6, 7.6.4).
	 */
	public static final String STRINGENCODING = "ISO-8859-1";

	private static Logger logger;

	public static synchronized Logger getLogger () {
		if (ru.dmerkushov.vnc.client.VncCommon.logger == null) {
			ru.dmerkushov.vnc.client.VncCommon.logger = Logger.getLogger (VncCommon.class.getName ());
			ru.dmerkushov.vnc.client.VncCommon.logger.setLevel (Level.ALL);
		}
		return ru.dmerkushov.vnc.client.VncCommon.logger;
	}

	private static Set<RfbClientSession> clientSessions;

	public static synchronized Set<RfbClientSession> getClientSessions () {
		if (ru.dmerkushov.vnc.client.VncCommon.clientSessions == null) {
			ru.dmerkushov.vnc.client.VncCommon.clientSessions = Collections.synchronizedSet (new LinkedHashSet<> ());
		}
		return ru.dmerkushov.vnc.client.VncCommon.clientSessions;
	}

	public static final Preferences vncPrefs = Preferences.systemNodeForPackage (VncCommon.class);

	////////////////////////////////////////////////////////////////////////////
	//
	// HexDump
	//
	////////////////////////////////////////////////////////////////////////////
	private static final Set<Byte> printable = new HashSet<> ();

	static {
		for (byte i = (byte) 0x20; i <= (byte) 0x40; i++) {        // Marks
			ru.dmerkushov.vnc.client.VncCommon.printable.add (i);
		}
		for (byte i = (byte) 0x41; i <= (byte) 0x5A; i++) {        // Latin capital
			ru.dmerkushov.vnc.client.VncCommon.printable.add (i);
		}
		for (byte i = (byte) 0x61; i <= (byte) 0x7A; i++) {        // Latin small
			ru.dmerkushov.vnc.client.VncCommon.printable.add (i);
		}
	}

	public static String hexdump (byte[] bytes) {
		StringBuilder sb = new StringBuilder ();

		int bytesLen = bytes.length;
		int currentStartByte = 0;

		while (currentStartByte < bytesLen) {
			sb.append (String.format ("%08X ", currentStartByte));

			sb.append ("|");

			for (int i = currentStartByte; i < currentStartByte + 8; i++) {
				try {
					sb.append (String.format (" %02X", bytes[i]));
				} catch (ArrayIndexOutOfBoundsException ex) {
					sb.append ("   ");
				}
			}
			sb.append (" ");
			for (int i = currentStartByte + 8; i < currentStartByte + 16; i++) {
				try {
					sb.append (String.format (" %02X", bytes[i]));
				} catch (ArrayIndexOutOfBoundsException ex) {
					sb.append ("   ");
				}
			}
			sb.append (" | ");
			for (int i = currentStartByte; i < currentStartByte + 8; i++) {
				try {
					if (ru.dmerkushov.vnc.client.VncCommon.printable.contains (bytes[i])) {
						sb.append ((char) bytes[i]);
					} else {
						sb.append (".");
					}
				} catch (ArrayIndexOutOfBoundsException ex) {
					sb.append (" ");
				}
			}
			sb.append (" ");
			for (int i = currentStartByte + 8; i < currentStartByte + 16; i++) {
				try {
					if (ru.dmerkushov.vnc.client.VncCommon.printable.contains (bytes[i])) {
						sb.append ((char) bytes[i]);
					} else {
						sb.append (".");
					}
				} catch (ArrayIndexOutOfBoundsException ex) {
					sb.append (" ");
				}
			}
			currentStartByte += 16;
			sb.append (" |\n");

		}

		return sb.toString ();
	}

}
