/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public class RfbRrePixelData extends RfbPixelData {

	public final int width;
	public final int height;

	public RfbRrePixelData (RfbRectangle rectangle, int width, int height) {
		super (rectangle);

		this.width = width;
		this.height = height;
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		//TODO Implement generated read() in RfbRrePixelData
		throw new UnsupportedOperationException ("Not supported yet.");
	}

	@Override
	public void write (OutputStream out) throws IOException {
		//TODO Implement generated write() in RfbRrePixelData
		throw new UnsupportedOperationException ("Not supported yet.");
	}

	@Override
	public void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException {
		//TODO Implement generated updateFramebuffer() in RfbRrePixelData
		throw new UnsupportedOperationException ("Not supported yet.");
	}

}
