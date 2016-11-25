/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 * This is the security handshake, phase 4, in case of VNC authentication. Sent
 * by the client to the server (C2S) after the user entered a password.
 * Described in RFC 6143, paragraph 7.2.2
 *
 * @author dmerkushov
 */
public class SecurityHandshake4_VNCauth_C2S extends RfbMessage {

	byte[] response;

	public static final int RESPONSE_STD_LENGTH = 16;
	public static final int PASSWORD_STD_LENGTH = 8;

	public SecurityHandshake4_VNCauth_C2S (RfbSession session) {
		super (session);
	}

	public SecurityHandshake4_VNCauth_C2S (RfbSession session, byte[] challenge, String password) throws MessageException {
		this (session);

		Objects.requireNonNull (challenge);
		Objects.requireNonNull (password);

		if (challenge.length != SecurityHandshake3_VNCauth_S2C.CHALLENGE_STD_LENGTH) {
			throw new IllegalArgumentException ("Challenge is not of standard length (" + SecurityHandshake3_VNCauth_S2C.CHALLENGE_STD_LENGTH + "): " + challenge.length);
		}
		if (password.length () > PASSWORD_STD_LENGTH) {
			password = password.substring (0, PASSWORD_STD_LENGTH);
		}

		byte[] passwordBytes;
		try {
			passwordBytes = Arrays.copyOf (password.getBytes ("UTF-8"), PASSWORD_STD_LENGTH);
		} catch (UnsupportedEncodingException ex) {
			throw new MessageException (ex);
		}

		SecretKey cipherKey = new SecretKeySpec (passwordBytes, 0, passwordBytes.length, "DES");

		Cipher cipher;
		try {
			cipher = Cipher.getInstance ("DES");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
			throw new MessageException (ex);
		}
		try {
			cipher.init (Cipher.ENCRYPT_MODE, cipherKey);
		} catch (InvalidKeyException ex) {
			throw new MessageException (ex);
		}
		try {
			response = cipher.doFinal (challenge);
		} catch (IllegalBlockSizeException | BadPaddingException ex) {
			throw new MessageException (ex);
		}

		if (response.length != RESPONSE_STD_LENGTH) {
			throw new MessageException ("Response is not of standard length (" + RESPONSE_STD_LENGTH + "): " + response.length);
		}
	}

	@Override
	public void write (OutputStream out) throws IOException {
		Objects.requireNonNull (out);

		out.write (response);
	}

	@Override
	public void read (InputStream in) throws IOException, MessageException {
		Objects.requireNonNull (in);

		int bytesRead = in.read (response);

		if (bytesRead != RESPONSE_STD_LENGTH) {
			throw new MessageException ("Read not " + RESPONSE_STD_LENGTH + " bytes as expected: " + bytesRead);
		}
	}

}
