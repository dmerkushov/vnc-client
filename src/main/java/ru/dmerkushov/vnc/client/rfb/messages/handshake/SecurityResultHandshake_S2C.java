/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
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

		if (!SECRESULT_POSSIBLE.contains (status)) {
			throw new IllegalArgumentException ("Status " + status + " is not one of the possible: " + Arrays.toString (SECRESULT_POSSIBLE.toArray (new Integer[0])));
		}

		this.status = status;

		if (status == SECRESULT_STATUS_FAILED) {
			Objects.requireNonNull (reason, "Reason may not be null if result is FAILED");

			this.reason = reason;
		}
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		ByteBuffer bb = ByteBuffer.allocate (4);
		bb.order (ByteOrder.BIG_ENDIAN);
		bb.putInt ((int) ((int) status & 0xFFFFFFFFL));
		byte[] bytes = bb.array ();

		out.write (bb.);

	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
