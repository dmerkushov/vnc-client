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

	private RfbSession session;

	public RfbFramebuffer (RfbSession session, int width, int height) {
		super (width, height, BufferedImage.TYPE_INT_RGB);

		Objects.requireNonNull (session, "session");

		attachSession (session);
	}

	/**
	 * Attach a session to this framebuffer. The session cannot be detached from
	 * here, since the incoming and the outgoing messages are attached to the
	 * session, so they go to the active framebuffer of that session.
	 *
	 * @param session
	 */
	public void attachSession (RfbSession session) {
		if (this.session != null) {
			this.session.detachFramebuffer ();
		}

		this.session = session;
		session.attachFramebuffer (this);
	}

}
