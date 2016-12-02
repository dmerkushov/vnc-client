/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.c2s;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class SetPixelFormatMessage extends C2SMessage {

	RfbPixelFormat pixelFormat;

	public SetPixelFormatMessage (RfbClientSession session, RfbPixelFormat pixelFormat) {
		super (session);

		Objects.requireNonNull (pixelFormat, "pixelFormat");

		this.pixelFormat = pixelFormat;
	}

	@Override
	public void read (InputStream in) throws IOException, MessageException {
		readU8 (in);		// Padding
		readU8 (in);		//
		readU8 (in);		//

		pixelFormat.read (in);
	}

	@Override
	public void write (OutputStream out) throws IOException, MessageException {
		super.write (out);

		writeU8 (out, 0);	// Padding
		writeU8 (out, 0);	//
		writeU8 (out, 0);	//
		pixelFormat.write (out);
	}

}
