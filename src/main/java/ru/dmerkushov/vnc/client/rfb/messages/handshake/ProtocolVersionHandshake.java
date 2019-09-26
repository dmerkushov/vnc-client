/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import static ru.dmerkushov.vnc.client.VncCommon.logger;

/**
 * This is the first handshake to send by, first, server, then client. Described in RFC 6143, paragraph 7.1.1
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
		switch (this.version) {
			case RFB_VER_3_3:
				protoString = ProtocolVersionHandshake.PROTOSTR_VER33;
				break;
			case RFB_VER_3_7:
				protoString = ProtocolVersionHandshake.PROTOSTR_VER37;
				break;
			case RFB_VER_3_8:
				protoString = ProtocolVersionHandshake.PROTOSTR_VER38;
				break;
		}

		out.write (protoString.getBytes (VncCommon.STRING_ENCODING));
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

		logger.finest ("ProtoString by the server: " + protoString);

		switch (protoString) {
			case ProtocolVersionHandshake.PROTOSTR_VER33:
				this.version = RfbVersion.RFB_VER_3_3;
				break;
			case ProtocolVersionHandshake.PROTOSTR_VER37:
				this.version = RfbVersion.RFB_VER_3_7;
				break;
			case ProtocolVersionHandshake.PROTOSTR_VER38:
				this.version = RfbVersion.RFB_VER_3_8;
				break;
			default:
				this.version = RfbVersion.RFB_VER_3_3;
		}
	}

	public RfbVersion getVersion () {
		return this.version;
	}

}
