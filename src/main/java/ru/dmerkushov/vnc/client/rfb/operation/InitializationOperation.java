/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbPixelData;
import ru.dmerkushov.vnc.client.rfb.messages.initialization.ClientInit_C2S;
import ru.dmerkushov.vnc.client.rfb.messages.initialization.ServerInit_S2C;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.SetEncodingsMessage;
import ru.dmerkushov.vnc.client.rfb.messages.normal.c2s.SetPixelFormatMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public class InitializationOperation extends Operation {

	public InitializationOperation (RfbClientSession session) {
		super (session);
	}

	@Override
	public void operate () throws RfbOperationException, IOException {
		InputStream in = session.getIn ();
		OutputStream out = session.getOut ();

		ClientInit_C2S clientInit_C2S = new ClientInit_C2S (session, true);
		clientInit_C2S.write (out);

		ServerInit_S2C serverInit_S2C = new ServerInit_S2C (session);
		serverInit_S2C.read (in);

		new RfbFramebuffer (session, serverInit_S2C.getFramebufferWidth (), serverInit_S2C.getFramebufferHeight ());

		// SetPixelFormat and SetEncoding also go here not to drop the logic of NormalOperation
		ArrayList<Integer> encodings = new ArrayList<> (1);
		encodings.add (RfbPixelData.ENCODINGTYPE_RAW);
		SetEncodingsMessage setEncodingsMsg = new SetEncodingsMessage (session, encodings);
		setEncodingsMsg.write (out);

		RfbPixelFormat pixelFormat = RfbPixelFormat.getDefaultPixelFormat ();
//		RfbPixelFormat pixelFormat = serverInit_S2C.getPixelFormat ();

		SetPixelFormatMessage setPixelFormatMsg = new SetPixelFormatMessage (session, pixelFormat);
		setPixelFormatMsg.write (out);

		session.setPixelFormat (pixelFormat);
	}
}
