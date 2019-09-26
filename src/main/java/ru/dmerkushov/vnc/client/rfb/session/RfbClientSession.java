/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.session;

import ru.dmerkushov.lib.threadhelper.AbstractTHRunnable;
import ru.dmerkushov.lib.threadhelper.ThreadHelper;
import ru.dmerkushov.lib.threadhelper.ThreadHelperException;
import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.C2SMessage;
import ru.dmerkushov.vnc.client.rfb.operation.HandshakeOperation;
import ru.dmerkushov.vnc.client.rfb.operation.InitializationOperation;
import ru.dmerkushov.vnc.client.rfb.operation.NormalOperation;
import ru.dmerkushov.vnc.client.rfb.operation.Operation;
import ru.dmerkushov.vnc.client.rfb.session.password.PasswordSupplier;
import ru.dmerkushov.vnc.client.rfb.session.password.UiPasswordSupplier;
import ru.dmerkushov.vnc.client.ui.VncView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

import static ru.dmerkushov.vnc.client.rfb.session.RfbSessionState.Error;
import static ru.dmerkushov.vnc.client.rfb.session.RfbSessionState.*;

/**
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
	private volatile RfbSessionState sessionState = Initial;

	/**
	 * Socket to use in this session
	 */
	private Socket socket;

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

	private Set<VncView> vncViews;

	private InputStream in;

	private OutputStream out;

	private PasswordSupplier passwordSupplier;

	private RfbPixelFormat pixelFormat;

	private NormalOperation normalOperation;

	private volatile boolean suspended;

	private UUID uuid;

	public final Map<String, Object> sessionObjects;

	public RfbClientSession (String serverHost, int serverPort) throws IOException {
		this (InetAddress.getByName (serverHost), serverPort);
	}

	public RfbClientSession (InetAddress serverHost, int serverPort) {
		Objects.requireNonNull (serverHost, "serverHost");

		// new Socket is created in startSession()
//		this.socket = new Socket ();
		this.serverHost = serverHost;
		this.serverPort = serverPort;

		this.passwordSupplier = new UiPasswordSupplier ();

		this.uuid = UUID.randomUUID ();

		this.sessionObjects = Collections.synchronizedMap (new LinkedHashMap<> ());

		this.vncViews = Collections.synchronizedSet (new HashSet<> ());

		// Must do this to initialize a group for this session in thread-helper
		ThreadHelper.getInstance ().addRunnable (this.getThreadGroupName (), new AbstractTHRunnable () {

			@Override
			public void doSomething () {
			}

			@Override
			public void finish () throws ThreadHelperException {
			}
		});
	}

	public RfbVersion getRfbVersion () {
		return this.rfbVersion;
	}

	public void setRfbVersion (RfbVersion rfbVersion) throws RfbSessionException {
		Objects.requireNonNull (rfbVersion);

		if (this.sessionState != Initial) {
			throw new RfbSessionException ("Cannot change RFB version when the session is not in state " + Initial + ". Current state is " + this.sessionState);
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

		RfbSessionState prevState = this.sessionState;
		this.sessionState = sessionState;

		if ((prevState != Error && sessionState == Error) || (prevState != Finished && sessionState == Finished)) {

			this.finishSession (sessionState);
		}

		if (prevState != Error && sessionState == Error) {
			System.out.println ("Session state: Error. Will restart after a while...");
			try {
				Thread.sleep (2000L);
			} catch (InterruptedException ex) {
				VncCommon.getLogger ().throwing ("", "", ex);
			}

			// DEBUG
			System.out.println ("...but currently in DEBUG state: exiting on error");
			System.exit (1);

			try {
				this.restartSession (sessionState);
			} catch (IOException ex) {
				throw new RfbSessionException (ex);
			}
		}
	}

	public RfbSessionState getSessionState () {
		return this.sessionState;
	}

	public Socket getSocket () {
		return this.socket;
	}

	public void startSession () throws RfbSessionException, IOException {
		this.refreshSocket ();

		this.operation = new HandshakeOperation (this);
		this.operation.operate ();
		this.operation = new InitializationOperation (this);
		this.operation.operate ();
		this.operation = new NormalOperation (this);
		this.normalOperation = (NormalOperation) this.operation;
		this.operation.operate ();
	}

	private void finishSession (RfbSessionState sessionState) throws RfbSessionException {
		Objects.requireNonNull (sessionState);

		try {
			ThreadHelper.getInstance ().finish (this.getThreadGroupName (), 1000L);
		} catch (ThreadHelperException ex) {
			throw new RfbSessionException (ex);
		}
		try {
			this.getSocket ().close ();
		} catch (IOException ex) {
			throw new RfbSessionException (ex);
		}
	}

	public void restartSession (RfbSessionState sessionState) throws RfbSessionException, IOException {
		VncCommon.getLogger ().entering (this.getClass ().getCanonicalName (), "restartSession");
		this.finishSession (sessionState);
		this.sessionState = Initial;
		this.startSession ();
		VncCommon.getLogger ().exiting (this.getClass ().getCanonicalName (), "restartSession");
	}

	private void refreshSocket () {
		this.socket = new Socket ();
	}

	public RfbFramebuffer getFramebuffer () {
		return this.framebuffer;
	}

	void attachFramebuffer (RfbFramebuffer framebuffer) {
		Objects.requireNonNull (framebuffer, "framebuffer");

		this.framebuffer = framebuffer;
	}

	void detachFramebuffer () {
		this.framebuffer = null;
	}

	public boolean isFramebufferAttached () {
		return this.framebuffer != null;
	}

	public Set<VncView> getViews () {
		return this.vncViews;
	}

	public void attachView (VncView vncView) {
		Objects.requireNonNull (vncView, "vncView");

		this.vncViews.add (vncView);
	}

	public void detachView (VncView vncView) {
		Objects.requireNonNull (vncView, "vncView");

		this.vncViews.remove (vncView);
	}

	public boolean isViewAttached () {
		return this.vncViews.size () > 0;
	}

	public boolean isViewAttached (VncView vncView) {
		Objects.requireNonNull (vncView, "vncView");

		return this.vncViews.contains (vncView);
	}

	public InetAddress getServerHost () {
		return this.serverHost;
	}

	public int getServerPort () {
		return this.serverPort;
	}

	public Operation getOperation () {
		return this.operation;
	}

	public InputStream getIn () throws RfbSessionException {
		Objects.requireNonNull (this.socket, "socket");

		InputStream in;
		try {
			in = this.socket.getInputStream ();
		} catch (IOException ex) {
			throw new RfbSessionException (ex);
		}

		return in;
	}

	public OutputStream getOut () throws RfbSessionException {
		Objects.requireNonNull (this.socket, "socket");

		OutputStream out;
		try {
			out = this.socket.getOutputStream ();
		} catch (IOException ex) {
			throw new RfbSessionException (ex);
		}

		return out;
	}

	public PasswordSupplier getPasswordSupplier () {
		return this.passwordSupplier;
	}

	public void setPasswordSupplier (PasswordSupplier passwordSupplier) {
		Objects.requireNonNull (passwordSupplier, "passwordSupplier");

		this.passwordSupplier = passwordSupplier;
	}

	public RfbPixelFormat getPixelFormat () {
		return this.pixelFormat;
	}

	public void setPixelFormat (RfbPixelFormat pixelFormat) {
		this.pixelFormat = pixelFormat;
	}

	public void sendMessage (C2SMessage message) {
		if (this.normalOperation != null) {
			this.normalOperation.sendMessage (message);
		}
	}

	public final String getThreadGroupName () {
		return this.getClass ().getName () + "_" + this.uuid.toString ();
	}

	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder ();
		sb.append (this.getClass ().getName ()).append (" {uuid=").append (this.uuid.toString ()).append ("; framebuffer: ");

		RfbFramebuffer framebuffer = this.getFramebuffer ();
		synchronized (framebuffer) {
			if (framebuffer == null) {
				sb.append ("(not attached); ");
			} else {
				sb.append (framebuffer.toString ()).append ("; ");
			}
		}
		sb.append ("vncViews: [");

		Iterator<VncView> vncViewIter = this.vncViews.iterator ();
		while (vncViewIter.hasNext ()) {
			VncView vncView = vncViewIter.next ();
			sb.append (vncView.toString ());
			if (vncViewIter.hasNext ()) {
				sb.append (", ");
			}
		}
		sb.append ("]; socket: ");
		sb.append (this.getSocket ().toString ());
		sb.append ("}");
		return sb.toString ();
	}

	public void suspend () {
		this.suspended = true;
	}

	public void resume () {
		this.suspended = false;
	}

	public boolean isSuspended () {
		return this.suspended;
	}

}
