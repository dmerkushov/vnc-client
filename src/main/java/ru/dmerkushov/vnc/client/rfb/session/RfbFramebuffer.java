/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 *
 * @author dmerkushov
 */
public final class RfbFramebuffer extends BufferedImage {

	private RfbClientSession session;

	public RfbFramebuffer (RfbClientSession session, int width, int height) {
		super (width, height, BufferedImage.TYPE_INT_RGB);

		attachSession (session);
	}

	/**
	 * Attach a session to this framebuffer. The session cannot be detached from
	 * here, since the incoming and the outgoing messages are attached to the
	 * session, so they go to the active framebuffer of that session.
	 *
	 * @param session
	 * @see
	 * RfbClientSession#attachFramebuffer(ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer)
	 * @see RfbClientSession#detachFramebuffer()
	 */
	private void attachSession (RfbClientSession session) {
		Objects.requireNonNull (session, "session");

		if (this.session != null) {
			this.session.detachFramebuffer ();
		}

		this.session = session;
		session.attachFramebuffer (this);
	}

	public RfbClientSession getSession () {
		return session;
	}

}
