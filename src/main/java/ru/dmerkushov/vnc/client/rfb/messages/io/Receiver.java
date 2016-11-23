/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.io;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 *
 * @author dmerkushov
 */
public class Receiver {

	final RfbSession session;

	private final Queue<byte[]> byteArraysQueue = new ConcurrentLinkedQueue<> ();

	public Receiver (RfbSession session) {
		Objects.requireNonNull (session);

		this.session = session;

		//TODO Start receiving thread
	}

	public boolean isEmpty () {
		return byteArraysQueue.isEmpty ();
	}

	public byte[] getNext () {
		while (byteArraysQueue.isEmpty ()) {
			try {
				Thread.sleep (10l);
			} catch (InterruptedException ex) {
			}
		}
		return byteArraysQueue.poll ();
	}

	//TODO Implement receiving thread
}
