package ru.dmerkushov.vnc.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * An input stream that will always repeat the given byte array as its output
 *
 * @author dmerkushov
 */
public class RepeatingInputStream extends InputStream {

	private byte[] bytes;
	private int currentIndex = -1;

	public RepeatingInputStream (byte[] bytes) {
		Objects.requireNonNull (bytes, "bytes");
		if (bytes.length == 0) {
			throw new IllegalArgumentException ("bytes array must be of non-0 length");
		}

		this.bytes = bytes;
	}

	@Override
	public int read () throws IOException {
		this.currentIndex++;
		if (this.currentIndex >= this.bytes.length) {
			this.currentIndex = 0;
		}

		return Byte.toUnsignedInt (this.bytes[this.currentIndex]);
	}
}
