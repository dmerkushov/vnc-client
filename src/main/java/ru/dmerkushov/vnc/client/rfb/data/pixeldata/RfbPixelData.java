/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;

/**
 *
 * @author dmerkushov
 */
public abstract class RfbPixelData {

	public final RfbRectangle rectangle;

	public RfbPixelData (RfbRectangle rectangle) {
		Objects.requireNonNull (rectangle, "rectangle");

		this.rectangle = rectangle;
	}

	public abstract void read (InputStream in) throws IOException;

	public abstract void write (OutputStream out) throws IOException;
}
