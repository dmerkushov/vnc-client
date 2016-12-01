/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.initialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readString;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeString;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 * This is the ServerInit message, a message sent by the server to the client
 * (S2C) after the client sends the ClientInit message. Described in RFC 6143,
 * paragraph 7.3.2.
 *
 * Before this one is:
 * {@link ru.dmerkushov.vnc.client.rfb.messages.initialization.ClientInit_S2C}.
 *
 * After this one is the normal mode of RFB.
 *
 * @author dmerkushov
 */
public class ServerInit_S2C extends RfbMessage {

	private int framebufferWidth;
	private int framebufferHeight;
	private RfbPixelFormat pixelFormat;
	private String name;

	public ServerInit_S2C (RfbSession session) {
		super (session);
	}

	public ServerInit_S2C (RfbSession session, int framebufferWidth, int framebufferHeight, RfbPixelFormat pixelFormat, String name) {
		super (session);

		Objects.requireNonNull (pixelFormat, "pixelFormat");
		Objects.requireNonNull (name, "name");

		this.framebufferWidth = framebufferWidth;
		this.framebufferHeight = framebufferHeight;
		this.pixelFormat = pixelFormat;
		this.name = name;
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		Objects.requireNonNull (out, "out");
		Objects.requireNonNull (pixelFormat, "pixelFormat");
		Objects.requireNonNull (name, "name");

		writeU16 (out, framebufferWidth);
		writeU16 (out, framebufferHeight);
		pixelFormat.write (out);
		writeString (out, name);
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		framebufferWidth = readU16 (in);
		framebufferHeight = readU16 (in);

		pixelFormat = new RfbPixelFormat ();
		pixelFormat.read (in);

		name = readString (in);
	}

}
