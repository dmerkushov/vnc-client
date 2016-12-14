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
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.normal.NormalMessage;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

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

	public FramebufferUpdateRequestMessage (RfbClientSession session, boolean incremental) {
		super (session, NormalMessage.MESSAGETYPE_C2S_FRAMEBUFFERUPDATEREQUEST);

		RfbFramebuffer framebuffer = session.getFramebuffer ();

		Objects.requireNonNull (framebuffer, "framebuffer");

		synchronized (framebuffer) {
			width = framebuffer.getWidth ();
			height = framebuffer.getHeight ();
		}

		xPosition = 0;
		yPosition = 0;
		this.incremental = incremental;
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		incremental = readBoolean (in);
		xPosition = readU16 (in, true);
		yPosition = readU16 (in, true);
		width = readU16 (in, true);
		height = readU16 (in, true);
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		super.write (out);

		writeBoolean (out, incremental);
		writeU16 (out, xPosition, true);
		writeU16 (out, yPosition, true);
		writeU16 (out, width, true);
		writeU16 (out, height, true);
	}

}
