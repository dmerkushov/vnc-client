/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readString;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeString;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 * This is the security result handshake, a message sent by the server to the
 * client (S2C) after the client has sent its password response, to inform of
 * the results of the security check. Described in RFC 6143, paragraph 7.1.3
 *
 * @author dmerkushov
 */
public class SecurityResultHandshake_S2C extends RfbMessage {

	public static final long SECRESULT_STATUS_OK = 0;
	public static final long SECRESULT_STATUS_FAILED = 1;
	public static final Set<Long> SECRESULT_POSSIBLE = new HashSet<> (Arrays.asList (SECRESULT_STATUS_OK, SECRESULT_STATUS_FAILED));

	private long status;
	private String reason = null;

	public SecurityResultHandshake_S2C (RfbSession session) {
		super (session);
	}

	public SecurityResultHandshake_S2C (RfbSession session, long status, String reason) {
		this (session);

		setStatus (status);

		if (status == SECRESULT_STATUS_FAILED) {
			Objects.requireNonNull (reason, "Reason may not be null if result is FAILED");

			this.reason = reason;
		}
	}

	public final void setStatus (long status) {
		if (!SECRESULT_POSSIBLE.contains (status)) {
			throw new IllegalArgumentException ("Status " + status + " is not one of the possible: " + Arrays.toString (SECRESULT_POSSIBLE.toArray (new Integer[0])));
		}

		this.status = status;
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		writeU32 (out, (int) status);

		if (status != SECRESULT_STATUS_OK) {
			if (reason == null) {
				throw new IllegalStateException ("reason is null when status is FAILED");
			}

			writeString (out, reason);
		}

	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		setStatus (readU32 (in));

		if (status != SECRESULT_STATUS_OK) {
			reason = readString (in);
		}
	}

}
