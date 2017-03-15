/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Set;
import java.util.logging.Level;
import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.ProtocolVersionHandshake;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityHandshake1_S2C;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityHandshake2_C2S;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityHandshake3_VNCauth_S2C;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityHandshake4_VNCauth_C2S;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityResultHandshake_S2C;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbSecurityType;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;
import ru.dmerkushov.vnc.client.rfb.session.RfbVersion;

/**
 *
 * @author dmerkushov
 */
public class HandshakeOperation extends Operation {

	public HandshakeOperation (RfbClientSession session) {
		super (session);
	}

	@Override
	public void operate () throws IOException, RfbOperationException {
		Socket socket = session.getSocket ();

		if (socket.isConnected ()) {
			throw new RfbOperationException ("The session's socket has already been connected");
		}
		if (socket.isClosed ()) {
			throw new RfbOperationException ("The session's socket has already been closed");
		}

		SocketAddress socketAddress = new InetSocketAddress (session.getServerHost (), session.getServerPort ());

		try {
			socket.connect (socketAddress);
		} catch (ConnectException ex) {
			throw new RfbOperationException ("Could not connect to RFB server " + socketAddress.toString () + ": " + ex.getMessage (), ex);
		}

		InputStream in;
		in = socket.getInputStream ();

		OutputStream out;
		out = socket.getOutputStream ();

		ProtocolVersionHandshake protocolVersionHandshake = new ProtocolVersionHandshake (session, RfbVersion.RFB_VER_3_8);
		protocolVersionHandshake.write (out);
		protocolVersionHandshake.read (in);
		try {
			session.setRfbVersion (protocolVersionHandshake.getVersion ());
		} catch (RfbSessionException ex) {
			throw new RfbOperationException (ex);
		}

		VncCommon.getLogger ().log (Level.INFO, "Set RFB version: {0}", session.getRfbVersion ().name ());

		SecurityHandshake1_S2C securityHandshake1_S2C = new SecurityHandshake1_S2C (session);
		securityHandshake1_S2C.read (in);

		Set<RfbSecurityType> securityTypes = securityHandshake1_S2C.getSecurityTypes ();

		if (VncCommon.getLogger ().isLoggable (Level.INFO)) {
			StringBuilder logMsgBuilder = new StringBuilder ();
			logMsgBuilder.append ("Security types supported:");
			securityTypes.forEach ((secType) -> {
				logMsgBuilder.append (" ").append (secType.name ());
			});
			VncCommon.getLogger ().info (logMsgBuilder.toString ());
		}

		RfbSecurityType secType;
		if (securityTypes.contains (RfbSecurityType.None)) {
			secType = RfbSecurityType.None;
		} else if (securityTypes.contains (RfbSecurityType.VNC)) {
			secType = RfbSecurityType.VNC;
		} else {
			throw new RfbOperationException ("The server supports neither None nor VNC security types. Since this client only supports these security types, cannot continue");
		}

		VncCommon.getLogger ().log (Level.INFO, "Sending request for security handshake, type {0}", secType);

		SecurityHandshake2_C2S securityHandshake2_C2S = new SecurityHandshake2_C2S (session, secType);
		securityHandshake2_C2S.write (out);

		if (secType == RfbSecurityType.VNC) {
			SecurityHandshake3_VNCauth_S2C securityHandshake3_VNCauth_S2C = new SecurityHandshake3_VNCauth_S2C (session);
			securityHandshake3_VNCauth_S2C.read (in);

			String passwordEntered = session.getPasswordSupplier ().getPassword ();

			SecurityHandshake4_VNCauth_C2S securityHandshake4_VNCauth_C2S = new SecurityHandshake4_VNCauth_C2S (session, securityHandshake3_VNCauth_S2C.getChallenge (), passwordEntered);
			securityHandshake4_VNCauth_C2S.write (out);
		}

		SecurityResultHandshake_S2C securityResultHandshake_S2C = new SecurityResultHandshake_S2C (session);
		securityResultHandshake_S2C.read (in);
		if (securityResultHandshake_S2C.getStatus () != SecurityResultHandshake_S2C.SECRESULT_STATUS_OK) {
			throw new RfbOperationException ("Security result is not OK: reason supplied by the server is: " + securityResultHandshake_S2C.getReason ());
		}
	}

}
