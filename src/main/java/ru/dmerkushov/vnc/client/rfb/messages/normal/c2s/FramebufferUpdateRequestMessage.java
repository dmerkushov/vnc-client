/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.c2s;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class FramebufferUpdateRequestMessage extends C2SMessage {

	int xPosition;
	int yPosition;
	int width;
	int height;
	boolean incremental;

	public FramebufferUpdateRequestMessage (RfbClientSession session, RfbFramebuffer framebuffer) {
		super (session);

		xPosition = 0;
		yPosition = 0;
		width = framebuffer.getWidth ();
		height = framebuffer.getWidth ();
		incremental = false;
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		incremental = readBoolean (in);
		xPosition = readU16 (in);
		yPosition = readU16 (in);
		width = readU16 (in);
		height = readU16 (in);
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		super.write (out);

		writeBoolean (out, incremental);
		writeU16 (out, xPosition);
		writeU16 (out, yPosition);
		writeU16 (out, width);
		writeU16 (out, height);
	}

}
