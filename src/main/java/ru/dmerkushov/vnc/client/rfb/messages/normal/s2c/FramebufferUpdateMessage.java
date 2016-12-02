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
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class FramebufferUpdateMessage extends S2CMessage {

	private RfbRectangle[] rectangles;

	public FramebufferUpdateMessage (RfbClientSession session) {
		super (session);
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		readU8 (in);		// Padding

		int rectangleCount = readU16 (in);

		rectangles = new RfbRectangle[rectangleCount];

		for (int i = 0; i < rectangleCount; i++) {
			RfbRectangle rectangle = new RfbRectangle (null);
			rectangle.read (in);
		}
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		Objects.requireNonNull (out, "out");
		Objects.requireNonNull (rectangles, "rectangles");

		super.write (out);

		writeU8 (out, 0);	// Padding

		writeU16 (out, rectangles.length);

		for (RfbRectangle rectangle : rectangles) {
			rectangle.write (out);
		}

	}

	public RfbRectangle[] getRectangles () {
		return rectangles;
	}

	public void setRectangles (RfbRectangle[] rectangles) {
		this.rectangles = rectangles;
	}
}
