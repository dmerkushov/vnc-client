/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 *
 * @author dmerkushov
 */
public class RfbMessagesUtil {

	public static ByteBuffer allocateBB (int byteCount) {
		ByteBuffer bb = ByteBuffer.allocate (byteCount);
		bb.order (ByteOrder.BIG_ENDIAN);
		return bb;
	}

	/**
	 * Read an unsigned 1-byte (8-bit) value to an int
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static int readU8 (InputStream in) throws IOException {
		Objects.requireNonNull (in, "in");

		if (Integer.BYTES != 4) {
			throw new IllegalStateException ("Integer.BYTES is not 4: " + Integer.BYTES);
		}

		ByteBuffer bb4 = allocateBB (4);
		byte[] bytes = readBytes (in, 1);
		bb4.put (bytes, 3, 1);
		bb4.flip ();

		return bb4.getInt ();
	}

	/**
	 * Read an unsigned 2-byte (16-bit) value to an int
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static int readU16 (InputStream in) throws IOException {
		Objects.requireNonNull (in, "in");

		if (Integer.BYTES != 4) {
			throw new IllegalStateException ("Integer.BYTES is not 4: " + Integer.BYTES);
		}

		ByteBuffer bb4 = allocateBB (4);
		byte[] bytes = readBytes (in, 2);
		bb4.put (bytes, 2, 2);
		bb4.flip ();

		return bb4.getInt ();
	}

	/**
	 * Read an unsigned 4-byte (32-bit) value to a long
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static long readU32 (InputStream in) throws IOException {
		Objects.requireNonNull (in, "in");

		if (Long.BYTES != 8) {
			throw new IllegalStateException ("Long.BYTES is not 8: " + Long.BYTES);
		}

		ByteBuffer bb8 = allocateBB (8);
		byte[] bytes = readBytes (in, 4);
		bb8.put (bytes, 4, 4);
		bb8.flip ();

		return bb8.getLong ();
	}

	/**
	 * Read a signed 4-byte (32-bit) value to an int
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static int readS32 (InputStream in) throws IOException {
		Objects.requireNonNull (in, "in");

		if (Integer.BYTES != 4) {
			throw new IllegalStateException ("Integer.BYTES is not 4: " + Integer.BYTES);
		}

		ByteBuffer bb4 = allocateBB (4);
		byte[] bytes = readBytes (in, 4);
		bb4.put (bytes);
		bb4.flip ();

		return bb4.getInt ();
	}

	public static byte[] readBytes (InputStream in, int len) throws IOException {
		Objects.requireNonNull (in);

		byte[] bytes = new byte[len];
		int readBytes = in.read (bytes);
		int startPos = readBytes;

		if (readBytes > -1 && startPos < len) {
			while (startPos < len) {
				int toRead = len - startPos;
				readBytes = in.read (bytes, startPos, toRead);
				if (readBytes == -1) {
					throw new IOException ("The end of input stream has been reached unexpectedly");
				}
				startPos += readBytes;
			}
		} else if (readBytes == -1) {
			throw new IOException ("The end of input stream has been reached unexpectedly");
		}

		return bytes;
	}

	public static boolean readBoolean (InputStream in) throws IOException {
		Objects.requireNonNull (in);

		return (in.read () != 0);
	}

	public static String readString (InputStream in) throws IOException {
		Objects.requireNonNull (in);

		int strLen = (int) readU32 (in);
		byte[] strBytes = readBytes (in, strLen);

		String str = new String (strBytes);

		return str;
	}

	/**
	 * Write an unsigned 1-byte (8-bit) value
	 *
	 * @param out
	 * @param value
	 * @throws IOException
	 */
	public static void writeU8 (OutputStream out, int value) throws IOException {
		Objects.requireNonNull (out, "out");

		if (Integer.BYTES != 4) {
			throw new IllegalStateException ("Integer.BYTES is not 4: " + Integer.BYTES);
		}

		ByteBuffer bb4 = allocateBB (4);
		bb4.putInt (value);
		bb4.flip ();
		out.write (bb4.array (), 3, 1);
	}

	/**
	 * Write an unsigned 2-byte (16-bit) value
	 *
	 * @param out
	 * @param value
	 * @throws IOException
	 */
	public static void writeU16 (OutputStream out, int value) throws IOException {
		Objects.requireNonNull (out, "out");

		if (Integer.BYTES != 4) {
			throw new IllegalStateException ("Integer.BYTES is not 4: " + Integer.BYTES);
		}

		ByteBuffer bb4 = allocateBB (4);
		bb4.putInt (value);
		bb4.flip ();
		out.write (bb4.array (), 2, 2);
	}

	/**
	 * Write an unsigned 4-byte (32-bit) value
	 *
	 * @param out
	 * @param value
	 * @throws IOException
	 */
	public static void writeU32 (OutputStream out, long value) throws IOException {
		Objects.requireNonNull (out, "out");

		if (Long.BYTES != 8) {
			throw new IllegalStateException ("Long.BYTES is not 8: " + Long.BYTES);
		}

		ByteBuffer bb8 = allocateBB (8);
		bb8.putLong (value);
		bb8.flip ();
		out.write (bb8.array (), 4, 4);
	}

	public static void writeS32 (OutputStream out, int value) throws IOException {
		Objects.requireNonNull (out, "out");

		if (Integer.BYTES != 4) {
			throw new IllegalStateException ("Integer.BYTES is not 4: " + Integer.BYTES);
		}

		ByteBuffer bb4 = allocateBB (4);
		bb4.putInt (value);
		bb4.flip ();
		out.write (bb4.array (), 0, 4);
	}

	public static void writeBoolean (OutputStream out, boolean value) throws IOException {
		Objects.requireNonNull (out);

		out.write (value ? 0xFF : 0x00);
	}

	public static void writeString (OutputStream out, String value) throws IOException {
		Objects.requireNonNull (out);
		Objects.requireNonNull (value);

		int valueLen = value.length ();

		writeU32 (out, valueLen);

		byte[] valueBytes = value.getBytes ();
		out.write (valueBytes);
	}

}
