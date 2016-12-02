/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.s2c;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbColorMap;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class SetColorMapEntriesMessage extends S2CMessage {

	RfbColorMap colorMap;

	public SetColorMapEntriesMessage (RfbClientSession session) {
		super (session);
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		readU8 (in);		// Padding

		colorMap = new RfbColorMap (getSession ());
		colorMap.read (in);
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		Objects.requireNonNull (out, "out");
		Objects.requireNonNull (colorMap, "colorMap");

		super.write (out); //To change body of generated methods, choose Tools | Templates.

		colorMap.write (out);
	}
}
