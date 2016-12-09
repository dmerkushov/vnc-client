/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.UUID;
import ru.dmerkushov.lib.threadhelper.AbstractTHRunnable;
import ru.dmerkushov.lib.threadhelper.ThreadHelper;
import ru.dmerkushov.lib.threadhelper.ThreadHelperException;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.C2SMessage;
import ru.dmerkushov.vnc.client.rfb.operation.HandshakeOperation;
import ru.dmerkushov.vnc.client.rfb.operation.InitializationOperation;
import ru.dmerkushov.vnc.client.rfb.operation.NormalOperation;
import ru.dmerkushov.vnc.client.rfb.operation.Operation;
import static ru.dmerkushov.vnc.client.rfb.session.RfbSessionState.Error;
import static ru.dmerkushov.vnc.client.rfb.session.RfbSessionState.Finished;
import static ru.dmerkushov.vnc.client.rfb.session.RfbSessionState.Initial;
import ru.dmerkushov.vnc.client.rfb.session.password.PasswordSupplier;
import ru.dmerkushov.vnc.client.rfb.session.password.UiPasswordSupplier;
import ru.dmerkushov.vnc.client.ui.VncView;

/**
 *
 * @author dmerkushov
 */
public class RfbClientSession {

	/**
	 * RFB version to use in this session
	 */
	private RfbVersion rfbVersion = RfbVersion.RFB_VER_3_8;

	/**
	 * Current session state
	 */
	private RfbSessionState sessionState = Initial;

	/**
	 * Socket to use in this session
	 */
	private final Socket socket;

	private final InetAddress serverHost;

	private final int serverPort;

	/**
	 * Operation of the session
	 */
	private Operation operation;

	/**
	 * Framebuffer to use in this session
	 */
	private RfbFramebuffer framebuffer;

	private VncView vncView;

	private InputStream in;

	private OutputStream out;

	private PasswordSupplier passwordSupplier;

	private RfbPixelFormat pixelFormat;

	private NormalOperation normalOperation;

	private UUID uuid;

	public RfbClientSession (String serverHost, int serverPort) throws UnknownHostException, IOException {
		this (new Socket (), InetAddress.getByName (serverHost), serverPort);
	}

	public RfbClientSession (InetAddress serverHost, int serverPort) throws UnknownHostException, IOException {
		this (new Socket (), serverHost, serverPort);
	}

	public RfbClientSession (Socket socket, InetAddress serverHost, int serverPort) {
		Objects.requireNonNull (socket);
		Objects.requireNonNull (serverHost, "serverHost");

		this.socket = socket;
		this.serverHost = serverHost;
		this.serverPort = serverPort;

		this.passwordSupplier = new UiPasswordSupplier ();

		this.uuid = UUID.randomUUID ();

		// Must do this to initialize a group for this session in thread-helper
		ThreadHelper.getInstance ().addRunnable (getThreadGroupName (), new AbstractTHRunnable () {

			@Override
			public void doSomething () {
			}

			@Override
			public void finish () throws ThreadHelperException {
			}
		});
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
			finishSession (sessionState);
		}

		this.sessionState = sessionState;
	}

	public RfbSessionState getSessionState () {
		return sessionState;
	}

	public Socket getSocket () {
		return socket;
	}

	public void startSession () throws RfbSessionException, IOException {

		operation = new HandshakeOperation (this);
		operation.operate ();
		operation = new InitializationOperation (this);
		operation.operate ();
		operation = new NormalOperation (this);
		this.normalOperation = (NormalOperation) operation;
		operation.operate ();
	}

	private void finishSession (RfbSessionState sessionState) throws RfbSessionException {
		Objects.requireNonNull (sessionState);

		try {
			ThreadHelper.getInstance ().finish (getThreadGroupName (), 1000l);
		} catch (ThreadHelperException ex) {
			throw new RfbSessionException (ex);
		}
		try {
			getSocket ().close ();
		} catch (IOException ex) {
			throw new RfbSessionException (ex);
		}
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

	public VncView getView () {
		return vncView;
	}

	public void attachView (VncView vncView) {
		Objects.requireNonNull (vncView, "vncView");

		this.vncView = vncView;
	}

	public void detachView () {
		this.vncView = null;
	}

	public boolean isViewAttached () {
		return vncView != null;
	}

	public InetAddress getServerHost () {
		return serverHost;
	}

	public int getServerPort () {
		return serverPort;
	}

	public Operation getOperation () {
		return operation;
	}

	public InputStream getIn () {
		return in;
	}

	public void setIn (InputStream in) {
		this.in = in;
	}

	public OutputStream getOut () {
		return out;
	}

	public void setOut (OutputStream out) {
		this.out = out;
	}

	public PasswordSupplier getPasswordSupplier () {
		return passwordSupplier;
	}

	public void setPasswordSupplier (PasswordSupplier passwordSupplier) {
		Objects.requireNonNull (passwordSupplier, "passwordSupplier");

		this.passwordSupplier = passwordSupplier;
	}

	public RfbPixelFormat getPixelFormat () {
		return pixelFormat;
	}

	public void setPixelFormat (RfbPixelFormat pixelFormat) {
		this.pixelFormat = pixelFormat;
	}

	public void sendMessage (C2SMessage message) {
		if (normalOperation != null) {
			normalOperation.sendMessage (message);
		}
	}

	public final String getThreadGroupName () {
		return getClass ().getName () + "_" + uuid.toString ();
	}

	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder ();
		sb.append (getClass ().getName ()).append (" {uuid=").append (uuid.toString ()).append ("; framebuffer: ");

		RfbFramebuffer framebuffer = this.getFramebuffer ();
		synchronized (framebuffer) {
			if (framebuffer == null) {
				sb.append ("(not attached); ");
			} else {
				sb.append (framebuffer.toString ()).append ("; ");
			}
		}
		sb.append ("vncView: ");
		sb.append (vncView.toString ());
		sb.append ("; socket: ");
		sb.append (getSocket ().toString ());
		sb.append ("}");
		return sb.toString ();
	}

}
