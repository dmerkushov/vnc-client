/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.normal.s2c;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 *
 * @author dmerkushov
 */
public class FramebufferUpdateMessage extends S2CMessage {

	private int rectangleCount;
	private RfbRectangle[] rectangles;

	public FramebufferUpdateMessage (RfbSession session) {
		super (session);
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		super.write (out);

	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		readU8 (in);		// Padding
		rectangleCount = readU16 (in);
	}

	//TODO Implement FramebufferUpdateMessage
}
