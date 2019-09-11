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
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readString;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeString;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU32;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbVersion;

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
	public static final long SECRESULT_STATUS_FAILED_TIGHT_TOOMANYATTEMPTS = 2;
	public static final Set<Long> SECRESULT_POSSIBLE = new HashSet<> (Arrays.asList (SECRESULT_STATUS_OK, SECRESULT_STATUS_FAILED, SECRESULT_STATUS_FAILED_TIGHT_TOOMANYATTEMPTS));

	private long status;
	private String reason = null;

	public SecurityResultHandshake_S2C (RfbClientSession session) {
		super (session);
	}

	public SecurityResultHandshake_S2C (RfbClientSession session, long status, String reason) {
		this (session);

		setStatus (status);

		if (getSession ().getRfbVersion ().compareTo (RfbVersion.RFB_VER_3_8) < 0) {
			this.reason = "Reason cannot set, because this is supported starting from RFB 3.8. Current RFB version is " + getSession ().getRfbVersion ();
		} else if (status == SECRESULT_STATUS_FAILED) {
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

	public long getStatus () {
		return status;
	}

	public String getReason () {
		return reason;
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		writeU32 (out, (int) status, true);

		if (status != SECRESULT_STATUS_OK) {
			if (reason == null) {
				throw new IllegalStateException ("reason is null when status is FAILED");
			}

			writeString (out, reason);
		}

	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		setStatus (readU32 (in, true));

		if (status != SECRESULT_STATUS_OK && getSession ().getRfbVersion ().compareTo (RfbVersion.RFB_VER_3_8) >= 0) {
			reason = readString (in);
		}
	}

}
