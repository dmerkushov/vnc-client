/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class VncCommon {

	/**
	 * The only supported encoding for RFB is ISO-8859-1 (Latin-1). Other
	 * encodings are not supported (see RFC 6143, sections 7.5.6, 7.6.4).
	 */
	public static final String STRINGENCODING = "ISO-8859-1";

	private static Logger logger;

	public static synchronized Logger getLogger () {
		if (logger == null) {
			logger = Logger.getLogger (VncCommon.class.getName ());
		}
		return logger;
	}

	private static Set<RfbClientSession> clientSessions;

	public static synchronized Set<RfbClientSession> getClientSessions () {
		if (clientSessions == null) {
			clientSessions = Collections.synchronizedSet (new LinkedHashSet<> ());
		}
		return clientSessions;
	}

}
