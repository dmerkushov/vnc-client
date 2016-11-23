/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.io;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import ru.dmerkushov.vnc.client.rfb.messages.Message;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 *
 * @author dmerkushov
 */
public final class Sender {

	final RfbSession session;

	private final Queue<byte[]> byteArraysQueue = new ConcurrentLinkedQueue<> ();

	public Sender (RfbSession session) {
		Objects.requireNonNull (session);

		this.session = session;
	}

	public void send (Message message) throws RfbIoException {
		Objects.requireNonNull (message);

		byte[] bytes;
		try {
			bytes = message.getBytes ();
		} catch (MessageException ex) {
			throw new RfbIoException (ex);
		}

		byteArraysQueue.add (bytes);
	}

	//TODO Implement sending thread
}
