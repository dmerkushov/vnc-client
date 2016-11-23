/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbVersion;

/**
 * This is the first handshake to send by, first, server, then client
 *
 * @author dmerkushov
 */
public class ProtocolVersionHandshake extends RfbMessage {

	RfbVersion version;

	public static final String PROTOSTR_VER33 = "RFB 003.003\n";
	public static final String PROTOSTR_VER37 = "RFB 003.007\n";
	public static final String PROTOSTR_VER38 = "RFB 003.008\n";

	public ProtocolVersionHandshake () {
		this.version = RfbVersion.Rfb33;
	}

	public ProtocolVersionHandshake (RfbVersion version) {
		Objects.requireNonNull (version);

		this.version = version;
	}

	@Override
	public byte[] getBytes () throws MessageException {
		String protoString = "";
		switch (version) {
			case Rfb33:
				protoString = PROTOSTR_VER33;
				break;
			case Rfb37:
				protoString = PROTOSTR_VER37;
				break;
			case Rfb38:
				protoString = PROTOSTR_VER38;
				break;
		}
		return protoString.getBytes ();
	}

	@Override
	public void fromBytes (byte[] bytes) throws MessageException {
		String protoString = new String (bytes).substring (0, 12).toUpperCase ();
		switch (protoString) {
			case PROTOSTR_VER33:
				version = RfbVersion.Rfb33;
				break;
			case PROTOSTR_VER37:
				version = RfbVersion.Rfb37;
				break;
			case PROTOSTR_VER38:
				version = RfbVersion.Rfb38;
				break;
			default:
				version = RfbVersion.Rfb33;
		}
	}

}
