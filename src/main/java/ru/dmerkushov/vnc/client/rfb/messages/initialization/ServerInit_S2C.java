/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.initialization;

import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readString;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU16;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeString;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeU16;

/**
 * This is the ServerInit message, a message sent by the server to the client (S2C) after the client sends the
 * ClientInit message. Described in RFC 6143, paragraph 7.3.2.
 * <p>
 * Before this one is: {@link ru.dmerkushov.vnc.client.rfb.messages.initialization.ClientInit_C2S}.
 * <p>
 * After this one is the normal mode of RFB.
 *
 * @author dmerkushov
 */
public class ServerInit_S2C extends RfbMessage {

	private int framebufferWidth;
	private int framebufferHeight;
	private RfbPixelFormat pixelFormat;
	private String name;

	public ServerInit_S2C (RfbClientSession session) {
		super (session);
	}

	public ServerInit_S2C (RfbClientSession session, int framebufferWidth, int framebufferHeight, RfbPixelFormat pixelFormat, String name) {
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
		Objects.requireNonNull (this.pixelFormat, "pixelFormat");
		Objects.requireNonNull (this.name, "name");

		writeU16 (out, this.framebufferWidth, true);
		writeU16 (out, this.framebufferHeight, true);
		this.pixelFormat.write (out);
		writeString (out, this.name);
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		this.framebufferWidth = readU16 (in, true);
		this.framebufferHeight = readU16 (in, true);

		this.pixelFormat = new RfbPixelFormat ();
		this.pixelFormat.read (in);

		this.name = readString (in);
	}

	public int getFramebufferWidth () {
		return this.framebufferWidth;
	}

	public int getFramebufferHeight () {
		return this.framebufferHeight;
	}

	public RfbPixelFormat getPixelFormat () {
		return this.pixelFormat;
	}

}
