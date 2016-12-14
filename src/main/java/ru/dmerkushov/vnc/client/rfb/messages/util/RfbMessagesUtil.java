/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import ru.dmerkushov.vnc.client.VncCommon;

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

		int result = in.read ();

		if (result == -1) {
			throw new IOException ("End of input stream");
		}

		return result;
	}

	/**
	 * Read an unsigned 2-byte (16-bit) value to an int
	 *
	 * @param in
	 * @param bigEndian
	 * @return
	 * @throws IOException
	 */
	public static int readU16 (InputStream in, boolean bigEndian) throws IOException {
		Objects.requireNonNull (in, "in");

		DataInput dis = new DataInputStream (in);
		short read;
		try {
			read = dis.readShort ();
		} catch (EOFException ex) {
			throw new IOException ("End of input stream", ex);
		}

		byte[] padding = new byte[]{0, 0};

		ByteBuffer bb4 = allocateBB (4);

		if (bigEndian) {
			bb4.order (ByteOrder.BIG_ENDIAN);
			bb4.put (padding);
			bb4.putShort (read);
		} else {
			bb4.order (ByteOrder.LITTLE_ENDIAN);
			bb4.putShort (read);
			bb4.put (padding);
		}

		return bb4.getInt (0);
	}

	/**
	 * Read an unsigned 4-byte (32-bit) value to a long
	 *
	 * @param in
	 * @param bigEndian
	 * @return
	 * @throws IOException
	 */
	public static long readU32 (InputStream in, boolean bigEndian) throws IOException {
		Objects.requireNonNull (in, "in");

		DataInput dis = new DataInputStream (in);

		byte[] bytes = new byte[4];
		try {
			dis.readFully (bytes);
		} catch (EOFException ex) {
			throw new IOException ("End of input stream", ex);
		}

		byte[] padding = new byte[]{0, 0, 0, 0};

		ByteBuffer bb8 = allocateBB (8);

		if (bigEndian) {
			bb8.order (ByteOrder.BIG_ENDIAN);
			bb8.put (padding);
			bb8.put (bytes);
		} else {
			bb8.order (ByteOrder.LITTLE_ENDIAN);
			bb8.put (bytes);
			bb8.put (padding);
		}

		return bb8.getLong (0);
	}

	/**
	 * Read a signed 4-byte (32-bit) value to an int
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static int readS32 (InputStream in, boolean bigEndian) throws IOException {
		Objects.requireNonNull (in, "in");

		DataInputStream dis = new DataInputStream (in);
		byte[] bytes = new byte[4];
		try {
			dis.readFully (bytes);
		} catch (EOFException ex) {
			throw new IOException ("End of input stream", ex);
		}

		ByteBuffer bb4 = allocateBB (4);
		bb4.order (bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		bb4.put (bytes);

		return bb4.getInt (0);
	}

	public static byte[] readBytes (InputStream in, int len) throws IOException {
		Objects.requireNonNull (in);

		byte[] bytes = new byte[len];

		DataInputStream dis = new DataInputStream (in);
		try {
			dis.readFully (bytes);
		} catch (EOFException ex) {
			throw new IOException ("End of input stream", ex);
		}

		return bytes;
	}

	public static boolean readBoolean (InputStream in) throws IOException {
		Objects.requireNonNull (in);

		return (in.read () != 0);
	}

	public static String readString (InputStream in) throws IOException {
		Objects.requireNonNull (in);

		int strLen = (int) readU32 (in, true);
		byte[] strBytes = readBytes (in, strLen);

		String str = new String (strBytes, VncCommon.STRINGENCODING);

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
	 * @param bigEndian
	 * @throws IOException
	 */
	public static void writeU16 (OutputStream out, int value, boolean bigEndian) throws IOException {
		Objects.requireNonNull (out, "out");

		ByteBuffer bb4 = allocateBB (4);
		if (bigEndian) {
			bb4.order (ByteOrder.BIG_ENDIAN);
			bb4.putInt (value);
			out.write (Arrays.copyOfRange (bb4.array (), 2, 4));
		} else {
			bb4.order (ByteOrder.LITTLE_ENDIAN);
			bb4.putInt (value);
			out.write (Arrays.copyOfRange (bb4.array (), 0, 2));
		}
	}

	/**
	 * Write an unsigned 4-byte (32-bit) value
	 *
	 * @param out
	 * @param value
	 * @param bigEndian
	 * @throws IOException
	 */
	public static void writeU32 (OutputStream out, long value, boolean bigEndian) throws IOException {
		Objects.requireNonNull (out, "out");

		ByteBuffer bb8 = allocateBB (8);
		if (bigEndian) {
			bb8.order (ByteOrder.BIG_ENDIAN);
			bb8.putLong (value);
			out.write (Arrays.copyOfRange (bb8.array (), 4, 8));
		} else {
			bb8.order (ByteOrder.LITTLE_ENDIAN);
			bb8.putLong (value);
			out.write (Arrays.copyOfRange (bb8.array (), 0, 4));
		}
	}

	public static void writeS32 (OutputStream out, int value, boolean bigEndian) throws IOException {
		Objects.requireNonNull (out, "out");

		ByteBuffer bb4 = allocateBB (4);
		bb4.order (bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		bb4.putInt (value);
		out.write (bb4.array ());
	}

	public static void writeBoolean (OutputStream out, boolean value) throws IOException {
		Objects.requireNonNull (out);

		out.write (value ? 0x01 : 0x00);
	}

	public static void writeString (OutputStream out, String value) throws IOException {
		Objects.requireNonNull (out);
		Objects.requireNonNull (value);

		int valueLen = value.length ();

		writeU32 (out, valueLen, true);

		byte[] valueBytes = value.getBytes (VncCommon.STRINGENCODING);
		out.write (valueBytes);
	}

	public static void writeBytes (OutputStream out, byte[] bytes) throws IOException {
		Objects.requireNonNull (out);
		Objects.requireNonNull (bytes);

		out.write (bytes);
	}

}
