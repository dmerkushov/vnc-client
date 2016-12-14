/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBytes;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU32;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public class RfbZlibPixelData extends RfbPixelData {

	public static final String ZLIB_INFLATER_SESSIONOBJECT_NAME = "ZlibInflater";

	Inflater inflater;

	public static final int[] ALLOWED_BITS_PER_PIXEL = {8, 16, 32};
	public static final HashSet<Integer> ALLOWED_BITS_PER_PIXEL_SET = new HashSet<> (Arrays.asList (8, 16, 32));
	RfbRawPixelData innerPixelData;

	private byte[] bytes;

	RfbClientSession session;

	public RfbZlibPixelData (RfbRectangle rectangle) {
		super (rectangle);

		this.session = rectangle.getSession ();

		inflater = (Inflater) session.sessionObjects.get (ZLIB_INFLATER_SESSIONOBJECT_NAME);
		if (inflater == null) {
			inflater = new Inflater ();
			session.sessionObjects.put (ZLIB_INFLATER_SESSIONOBJECT_NAME, inflater);
		}
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		int len = (int) readU32 (in, true);
		byte[] inBytes = readBytes (in, len);

		inflater.setInput (inBytes);

		ByteArrayOutputStream baos = new ByteArrayOutputStream ();
		byte[] outBytes = new byte[100];
		while (!inflater.needsInput ()) {
			int inflated;
			try {
				inflated = inflater.inflate (outBytes);
			} catch (DataFormatException ex) {
				throw new IOException (ex);
			}
			baos.write (outBytes, 0, inflated);
		}
		byte[] uncompressedBytes = baos.toByteArray ();

		ByteArrayInputStream uncompressedBais = new ByteArrayInputStream (uncompressedBytes);
		innerPixelData = new RfbRawPixelData (rectangle);
		innerPixelData.read (uncompressedBais);
	}

	@Override
	public void write (OutputStream out) throws IOException {
		//TODO Implement generated write() in RfbZlibPixelData
		throw new UnsupportedOperationException ("Not supported yet.");
	}

	@Override
	public void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException {
		Objects.requireNonNull (framebuffer, "framebuffer");

		Objects.requireNonNull (innerPixelData, "innerPixelData");

		innerPixelData.updateFramebuffer (framebuffer);
	}

}
