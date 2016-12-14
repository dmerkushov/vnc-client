/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbVersion;

/**
 * This is the first handshake to send by, first, server, then client. Described
 * in RFC 6143, paragraph 7.1.1
 *
 * @author dmerkushov
 */
public class ProtocolVersionHandshake extends RfbMessage {

	RfbVersion version;

	public static final String PROTOSTR_VER33 = "RFB 003.003\n";
	public static final String PROTOSTR_VER37 = "RFB 003.007\n";
	public static final String PROTOSTR_VER38 = "RFB 003.008\n";

	public ProtocolVersionHandshake (RfbClientSession session) {
		super (session);

		this.version = RfbVersion.RFB_VER_3_8;
	}

	public ProtocolVersionHandshake (RfbClientSession session, RfbVersion version) {
		super (session);

		Objects.requireNonNull (version, "version");

		this.version = version;
	}

	@Override
	public void write (OutputStream out) throws IOException {
		Objects.requireNonNull (out);

		String protoString = "";
		switch (version) {
			case RFB_VER_3_3:
				protoString = PROTOSTR_VER33;
				break;
			case RFB_VER_3_7:
				protoString = PROTOSTR_VER37;
				break;
			case RFB_VER_3_8:
				protoString = PROTOSTR_VER38;
				break;
		}

		out.write (protoString.getBytes (VncCommon.STRINGENCODING));
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		Objects.requireNonNull (in);

		byte[] bytes = new byte[12];
		int byteCount;
		byteCount = in.read (bytes);

		if (byteCount != 12) {
			throw new MessageException ("Read not 12 bytes: " + byteCount);
		}

		String protoString = new String (bytes).toUpperCase ();

		System.out.println ("ProtoString by the server: " + protoString);

		switch (protoString) {
			case PROTOSTR_VER33:
				version = RfbVersion.RFB_VER_3_3;
				break;
			case PROTOSTR_VER37:
				version = RfbVersion.RFB_VER_3_7;
				break;
			case PROTOSTR_VER38:
				version = RfbVersion.RFB_VER_3_8;
				break;
			default:
				version = RfbVersion.RFB_VER_3_3;
		}
	}

	public RfbVersion getVersion () {
		return version;
	}

}
