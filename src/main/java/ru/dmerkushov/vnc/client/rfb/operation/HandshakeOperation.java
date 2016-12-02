/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Set;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.ProtocolVersionHandshake;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityHandshake1_S2C;
import ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityHandshake2_C2S;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbSecurityType;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;

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

		SocketAddress socketAddress = new InetSocketAddress (session.getServerHost (), session.getServerPort ());

		socket.connect (socketAddress);

		InputStream in;
		in = socket.getInputStream ();
		session.setIn (in);

		OutputStream out;
		out = socket.getOutputStream ();
		session.setOut (out);

		ProtocolVersionHandshake protocolVersionHandshake = new ProtocolVersionHandshake (session);
		protocolVersionHandshake.write (out);
		protocolVersionHandshake.read (in);
		try {
			session.setRfbVersion (protocolVersionHandshake.getVersion ());
		} catch (RfbSessionException ex) {
			throw new RfbOperationException (ex);
		}

		SecurityHandshake1_S2C securityHandshake1_S2C = new SecurityHandshake1_S2C (session);
		securityHandshake1_S2C.write (out);
		Set<RfbSecurityType> securityTypes = securityHandshake1_S2C.getSecurityTypes ();
		RfbSecurityType secType;
		if (securityTypes.contains (RfbSecurityType.None)) {
			secType = RfbSecurityType.None;
		} else if (securityTypes.contains (RfbSecurityType.VNC)) {
			secType = RfbSecurityType.VNC;
		} else {
			throw new RfbOperationException ("The server supports neither None nor VNC security types");
		}

		SecurityHandshake2_C2S securityHandshake2_C2S = new SecurityHandshake2_C2S (session, secType);
		securityHandshake2_C2S.write (out);

		//TODO go on implement operate() in HandshakeOperation
	}

}
