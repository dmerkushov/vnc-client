/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author dmerkushov
 */
public interface Message {

	/**
	 * Write this message to an output stream (with message type byte, if
	 * needed). Won't close the stream.
	 *
	 * @param out
	 * @throws MessageException
	 * @throws java.io.IOException
	 */
	public void write (OutputStream out) throws MessageException, IOException;

	/**
	 * Read this message from an input stream (without message type byte, for
	 * some RFB messages). Won't close the stream.
	 *
	 * @param in
	 * @throws MessageException
	 * @throws java.io.IOException
	 */
	public void read (InputStream in) throws MessageException, IOException;

}
