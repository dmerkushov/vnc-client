/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public class RfbColorMap {

	RfbClientSession session;
	HashMap<Integer, Integer> colors;

	public RfbColorMap (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		this.session = session;

		this.colors = new HashMap<> ();
	}

	public void read (InputStream in) throws IOException {
		int first = readU16 (in);
		int colorCount = readU16 (in);

		for (int i = 0; i < colorCount; i++) {
			int current = first + i;

			int red = readU16 (in) >> 8;
			int green = readU16 (in) >> 8;
			int blue = readU16 (in) >> 8;
			int rgb = (red << 16) | (green << 8) | blue;

			colors.put (current, rgb);
		}
	}

	public void write (OutputStream out) throws IOException {

		int min = Integer.MAX_VALUE;
		int max = 0;
		for (int key : colors.keySet ()) {
			if (key < min) {
				min = key;
			}
			if (key > max) {
				max = key;
			}
		}

		int colorCount = max - min;

		writeU16 (out, min);
		writeU16 (out, colorCount);

		for (int i = min; i < max; i++) {
			Integer current = colors.get (i);
			if (current == null) {
				current = 0;
			}
			int red = (current & 0xFF0000) >> 8;
			int green = (current & 0xFF00);
			int blue = (current & 0xFF) << 8;

			writeU16 (out, red);
			writeU16 (out, green);
			writeU16 (out, blue);
		}

	}

	public Integer getRGB (int index) {
		return colors.get (index);
	}

}
