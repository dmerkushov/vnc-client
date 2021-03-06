/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbPixelData;
import ru.dmerkushov.vnc.client.rfb.messages.initialization.ClientInit_C2S;
import ru.dmerkushov.vnc.client.rfb.messages.initialization.ServerInit_S2C;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.SetEncodingsMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.SetPixelFormatMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.rfb.session.RfbSessionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static ru.dmerkushov.vnc.client.VncCommon.logger;

/**
 * @author dmerkushov
 */
public class InitializationOperation extends Operation {

	public InitializationOperation (RfbClientSession session) {
		super (session);
	}

	@Override
	public void operate () throws RfbOperationException, IOException {
		InputStream in;
		try {
			in = this.session.getIn ();
		} catch (RfbSessionException ex) {
			throw new RfbOperationException (ex);
		}
		OutputStream out;
		try {
			out = this.session.getOut ();
		} catch (RfbSessionException ex) {
			throw new RfbOperationException (ex);
		}

		ClientInit_C2S clientInit_C2S = new ClientInit_C2S (this.session, true);
		clientInit_C2S.write (out);

		ServerInit_S2C serverInit_S2C = new ServerInit_S2C (this.session);
		serverInit_S2C.read (in);

		new RfbFramebuffer (this.session, serverInit_S2C.getFramebufferWidth (), serverInit_S2C.getFramebufferHeight ());

		// SetPixelFormat and SetEncoding also go here not to drop the logic of NormalOperation
		ArrayList<Integer> encodings = new ArrayList<> (1);

		/*
		RFC 6143, p.7.7.1 says:

		"All RFB clients must be able to handle pixel data in this raw encoding,
		and RFB servers should only produce raw encoding unless the client
		specifically asks for some other encoding type."

		We support RAW, but RAW is inefficient. So we don't want the server to
		use RAW, and we don't ask for it. Instead, we specifically ask for ZLIB,
		that provides data compression at a rate of about 50 (compared to RAW
		on regular office applications).
		 */
//		encodings.add (RfbPixelData.ENCODINGTYPE_RAW);
//		encodings.add (RfbPixelData.ENCODINGTYPE_ZLIB);
		encodings.add (RfbPixelData.ENCODINGTYPE_TIGHT);
//		encodings.add (RfbPixelData.ENCODINGTYPE_COPYRECT);
//		encodings.add (RfbPixelData.ENCODINGTYPE_TRLE);
		encodings.add (RfbPixelData.ENCODINGTYPE_PSEUDO_CURSOR);

		/*
		Preferences indicate the JPEG quality to use:
		NOJPEG (default) - don't use JPEG
		HIGH - high quality
		MEDIUM - medium quality
		LOW - low quality
		 */
		String jpegQuality = VncCommon.vncPrefs.get ("JPEG_QUALITY", "NOJPEG");

		int jpegQualityVnc = Integer.MIN_VALUE;
		switch (jpegQuality.trim ().toUpperCase ()) {
			case "HIGH":
				jpegQualityVnc = RfbPixelData.ENCODINGTYPE_PSEUDO_JPEG_QUALITY_LVL_HIGH;
				break;
			case "MEDIUM":
				jpegQualityVnc = RfbPixelData.ENCODINGTYPE_PSEUDO_JPEG_QUALITY_LVL_MEDIUM;
				break;
			case "LOW":
				jpegQualityVnc = RfbPixelData.ENCODINGTYPE_PSEUDO_JPEG_QUALITY_LVL_LOW;
				break;
			default:
				logger.warning ("Tight encoding won't use JPEG compression");
		}
		if (jpegQualityVnc > Integer.MIN_VALUE) {
			encodings.add (jpegQualityVnc);
		}

		SetEncodingsMessage setEncodingsMsg = new SetEncodingsMessage (this.session, encodings);
		setEncodingsMsg.write (out);

		RfbPixelFormat pixelFormat = RfbPixelFormat.getDefaultPixelFormat ();
//		RfbPixelFormat pixelFormat = serverInit_S2C.getPixelFormat ();

		SetPixelFormatMessage setPixelFormatMsg = new SetPixelFormatMessage (this.session, pixelFormat);
		setPixelFormatMsg.write (out);

		this.session.setPixelFormat (pixelFormat);
	}
}
