/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.c2s;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readS32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeS32;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class SetEncodingsMessage extends C2SMessage {

	List<Integer> encodings;

	public SetEncodingsMessage (RfbClientSession session, Collection<Integer> encodings) {
		super (session);

		this.encodings = new ArrayList<> ();

		if (encodings != null) {
			this.encodings.addAll (encodings);
		}
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		readU8 (in);
		readU8 (in);
		int encodingsCount = readU16 (in);
		for (int i = 0; i < encodingsCount; i++) {
			encodings.add (readS32 (in));
		}
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		super.write (out);

		writeU8 (out, 0);	// Padding
		writeU16 (out, encodings.size ());
		for (int encoding : encodings) {
			writeS32 (out, encoding);
		}
	}

}
