/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import static ru.dmerkushov.vnc.client.rfb.session.RfbSessionState.Error;
import static ru.dmerkushov.vnc.client.rfb.session.RfbSessionState.Finished;
import static ru.dmerkushov.vnc.client.rfb.session.RfbSessionState.Initial;

/**
 *
 * @author dmerkushov
 */
public class RfbSession {

	/**
	 * RFB version to use in this session
	 */
	private RfbVersion rfbVersion = RfbVersion.Rfb38;

	/**
	 * Current session state
	 */
	private RfbSessionState sessionState = Initial;

	/**
	 * Socket to use in this session
	 */
	private Socket socket;

	/**
	 * Framebuffer to use in this session
	 */
	private RfbFramebuffer framebuffer;

	public RfbSession (String serverHost, int serverPort) throws UnknownHostException, IOException {
		this (new Socket (serverHost, serverPort));
	}

	public RfbSession (InetAddress serverHost, int serverPort) throws UnknownHostException, IOException {
		this (new Socket (serverHost, serverPort));
	}

	public RfbSession (Socket socket) {
		Objects.requireNonNull (socket);

		this.socket = socket;
	}

	public RfbVersion getRfbVersion () {
		return rfbVersion;
	}

	public void setRfbVersion (RfbVersion rfbVersion) throws RfbSessionException {
		Objects.requireNonNull (rfbVersion);

		if (sessionState != Initial) {
			throw new RfbSessionException ("Cannot change RFB version when the session is not in state " + Initial + ". Current state is " + sessionState);
		}

		this.rfbVersion = rfbVersion;
	}

	public void setSessionState (RfbSessionState sessionState) throws RfbSessionException {
		Objects.requireNonNull (sessionState);

		boolean ok = false;

		if (sessionState.value () >= this.sessionState.value) {
			ok = true;
		} else if (sessionState == Error || sessionState == Finished) {
			ok = true;
		}

		if (!ok) {
			throw new RfbSessionException ("Cannot transfer session from state " + this.sessionState + " to " + sessionState);
		}

		if ((this.sessionState != Error && sessionState == Error) || (this.sessionState != Finished && sessionState == Finished)) {
			finishSessionClientSide (sessionState);
		}

		this.sessionState = sessionState;
	}

	public RfbSessionState getSessionState () {
		return sessionState;
	}

	public Socket getSocket () {
		return socket;
	}

	public void startSession () throws RfbSessionException {
		//TODO Implement startSession()
	}

	private void finishSessionClientSide (RfbSessionState sessionState) throws RfbSessionException {
		Objects.requireNonNull (sessionState);

		if (sessionState != Error && sessionState != Finished) {
			throw new RfbSessionException ("Cannot finish a session with a state other than " + Error + " or " + Finished + ". Ordered state: " + sessionState);
		}

		// TODO Implement finishSessionClientSide()
	}

	public RfbFramebuffer getFramebuffer () {
		return framebuffer;
	}

	void attachFramebuffer (RfbFramebuffer framebuffer) {
		Objects.requireNonNull (framebuffer, "framebuffer");

		this.framebuffer = framebuffer;
	}

	public void detachFramebuffer () {
		this.framebuffer = null;
	}

	public boolean isFramebufferAttached () {
		return framebuffer != null;
	}

}
