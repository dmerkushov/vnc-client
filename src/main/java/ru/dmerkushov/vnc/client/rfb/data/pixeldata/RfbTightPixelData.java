/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import com.jcraft.jzlib.InflaterInputStream;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;
import ru.dmerkushov.vnc.client.util.RepeatingInputStream;
import ru.dmerkushov.vnc.client.util.WrappingInputStream;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;

/**
 * Support for Tight-encoded pixel data, as described in the rfbproto document, p.7.6.7
 *
 * @author dmerkushov
 */
public class RfbTightPixelData extends RfbPixelData {

	public static final String ZLIB_INFLATERS_SESSIONOBJECT_NAME = "ZlibInflaters";
	public static final String ZLIB_PACKEDINPUTSTREAMS_SESSIONOBJECT_NAME = "ZlibPackedInputStreams";

	private InflaterInputStream[] infIss;
	private WrappingInputStream[] packedInputStreams;

	public static final int[] ALLOWED_BITS_PER_PIXEL = {8, 16, 32};
	public static final HashSet<Integer> ALLOWED_BITS_PER_PIXEL_SET = new HashSet<> (Arrays.asList (8, 16, 32));
	private RfbRawPixelData innerPixelData;

	private byte[] bytes;

	private RfbClientSession session;

	public RfbTightPixelData (RfbRectangle rectangle) {
		super (rectangle);

		this.session = rectangle.getSession ();

		this.packedInputStreams = (WrappingInputStream[]) this.session.sessionObjects.get (ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbTightPixelData.ZLIB_PACKEDINPUTSTREAMS_SESSIONOBJECT_NAME);
		if (this.packedInputStreams == null) {
			this.packedInputStreams = new WrappingInputStream[4];
			this.packedInputStreams[0] = new WrappingInputStream ();
			this.packedInputStreams[1] = new WrappingInputStream ();
			this.packedInputStreams[2] = new WrappingInputStream ();
			this.packedInputStreams[3] = new WrappingInputStream ();

			this.session.sessionObjects.put (ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbTightPixelData.ZLIB_PACKEDINPUTSTREAMS_SESSIONOBJECT_NAME, this.packedInputStreams);
		}

		this.infIss = (InflaterInputStream[]) this.session.sessionObjects.get (ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbTightPixelData.ZLIB_INFLATERS_SESSIONOBJECT_NAME);
		if (this.infIss == null) {
			this.infIss = new InflaterInputStream[4];
			try {
				this.infIss[0] = new InflaterInputStream (this.packedInputStreams[0], false);
				this.infIss[1] = new InflaterInputStream (this.packedInputStreams[1], false);
				this.infIss[2] = new InflaterInputStream (this.packedInputStreams[2], false);
				this.infIss[3] = new InflaterInputStream (this.packedInputStreams[3], false);
			} catch (IOException ex) {
				throw new RuntimeException (ex);
			}

			this.session.sessionObjects.put (ru.dmerkushov.vnc.client.rfb.data.pixeldata.RfbTightPixelData.ZLIB_INFLATERS_SESSIONOBJECT_NAME, this.infIss);
		}

		if (this.infIss.length != 4) {
			throw new IllegalStateException ("infIss array has length != 4 : " + this.infIss.length);
		}
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		int compressionControl = readU8 (in);

		// DEBUG
		System.out.println ("Compression control byte: " + String.format ("%8s", Integer.toBinaryString (compressionControl)).replaceAll (" ", "0"));

		if ((compressionControl & 0x80) == 0x00) {
			this.readBasicCompression (in, compressionControl);
		} else if ((compressionControl & 0xF0) == 0x80) {
			this.readFillCompression (in);
		} else if ((compressionControl & 0xF0) == 0x90) {
			this.readJpegCompression (in);
		} else {
			throw new MessageException ("Unknown compression control byte in Tight encoding: 0x" + Integer.toHexString (compressionControl));
		}
	}

	private void readBasicCompression (InputStream in, int compressionControl) throws MessageException, IOException {
		if ((compressionControl & 0x01) == 0x01) {

			// DEBUG
			System.out.println ("Reset infIs 0");

			this.packedInputStreams[0] = new WrappingInputStream ();
			this.infIss[0] = new InflaterInputStream (this.packedInputStreams[0], false);
		}
		if ((compressionControl & 0x02) == 0x02) {

			// DEBUG
			System.out.println ("Reset infIs 1");

			this.packedInputStreams[1] = new WrappingInputStream ();
			this.infIss[1] = new InflaterInputStream (this.packedInputStreams[1], false);
		}
		if ((compressionControl & 0x04) == 0x04) {

			// DEBUG
			System.out.println ("Reset infIs 2");

			this.packedInputStreams[2] = new WrappingInputStream ();
			this.infIss[2] = new InflaterInputStream (this.packedInputStreams[2], false);
		}
		if ((compressionControl & 0x08) == 0x08) {

			// DEBUG
			System.out.println ("Reset infIs 3");

			this.packedInputStreams[3] = new WrappingInputStream ();
			this.infIss[3] = new InflaterInputStream (this.packedInputStreams[3], false);
		}

		int usedInflaterIndex = (compressionControl >> 4) & 0x03;

		// DEBUG
		System.out.println ("Use ZLIB infIs (stream) #" + usedInflaterIndex);

		InflaterInputStream infIs = this.infIss[usedInflaterIndex];
		WrappingInputStream packedIs = this.packedInputStreams[usedInflaterIndex];

		int filterId = 0;    // CopyFilter (by default)
		if ((compressionControl & 0x40) == 0x40) {
			filterId = readU8 (in);
		}

		// DEBUG
		System.out.println ("filter-id byte: " + String.format ("%8s", Integer.toBinaryString (filterId)).replaceAll (" ", "0"));

		if (filterId == 0) {
			this.readBasicCompressionCopyFilter (in, infIs, packedIs);
		} else if (filterId == 1) {
			this.readBasicCompressionPaletteFilter (in, infIs, packedIs);
		} else if (filterId == 2) {
			this.readBasicCompressionGradientFilter (in, infIs, packedIs);
		} else {
			throw new MessageException ("Unknown filter-id byte in Tight encoding, Basic compression: 0x" + Integer.toHexString (filterId));
		}
	}

	private void readBasicCompressionCopyFilter (InputStream in, InflaterInputStream infIs, WrappingInputStream packedIs) throws MessageException, IOException {

		//DEBUG
		System.out.println ("+readBasicCompressionCopyFilter");

		boolean useTightPixel = this.session.getPixelFormat ().mayApplyTightPixel ();

		//DEBUG
		System.out.println ("Using tight pixel: " + useTightPixel);

		int pixelBytes = this.session.getPixelFormat ().getBitsPerPixel () / 8;
		if (useTightPixel) {
			pixelBytes = 3;
		}
		int width = this.rectangle.getWidth ();
		int height = this.rectangle.getHeight ();
		int len = pixelBytes * width * height;

		byte[] data = this.readBasicCompressionData (in, len, infIs, packedIs);

		this.innerPixelData = new RfbRawPixelData (this.rectangle);
		byte[] uncompressedData;
		if (useTightPixel) {
			ByteArrayInputStream tightIs = new java.io.ByteArrayInputStream (data);
			ByteArrayOutputStream uncompressedOs = new java.io.ByteArrayOutputStream ();
			DataOutputStream uncompressedDos = new java.io.DataOutputStream (uncompressedOs);
			RfbPixelFormat pixelFormat = this.session.getPixelFormat ();
			for (int i = 0; i < len / pixelBytes; i++) {
				int argb = pixelFormat.readArgbTightPixels (tightIs, 1)[0];

				uncompressedDos.writeInt (argb);
			}

			uncompressedData = uncompressedOs.toByteArray ();
		} else {
			uncompressedData = data;
		}

		ByteArrayInputStream uncompressedBais = new ByteArrayInputStream (uncompressedData);
		this.innerPixelData = new RfbRawPixelData (this.rectangle);
		this.innerPixelData.read (uncompressedBais);

		//DEBUG
		System.out.println ("-readBasicCompressionCopyFilter");

	}

	private void readBasicCompressionPaletteFilter (InputStream in, InflaterInputStream currentInflaterIs, WrappingInputStream currentPackedIs) throws MessageException, IOException {

		//DEBUG
		System.out.println ("+readBasicCompressionPaletteFilter");

		int paletteColorCount = readU8 (in) + 1;

		//DEBUG
		System.out.println ("Palette color count: " + paletteColorCount);

		int[] argbPalette = this.session.getPixelFormat ().readArgbTightPixels (in, paletteColorCount);

		int width = this.rectangle.getWidth ();
		int height = this.rectangle.getHeight ();

		int expectedLen;
		if (paletteColorCount > 2) {
			expectedLen = width * height;
		} else {
			expectedLen = (width + 7) / 8 * height;
		}

		//DEBUG
		System.out.println ("WxH: " + width + "x" + height);
		System.out.println ("Expected data len: " + expectedLen);

		byte[] data = this.readBasicCompressionData (in, expectedLen, currentInflaterIs, currentPackedIs);

		int pixelByteCount = this.session.getPixelFormat ().getBitsPerPixel () / 8;
		ByteArrayOutputStream argbBaos = new ByteArrayOutputStream (width * height * pixelByteCount);
		DataOutputStream argbDos = new DataOutputStream (argbBaos);
		if (paletteColorCount > 2) {
			for (int i = 0; i < data.length; i++) {
				argbDos.writeInt (argbPalette[Byte.toUnsignedInt (data[i])]);
			}
		} else {
			int i = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x += 8) {
					int currByte = Byte.toUnsignedInt (data[i]);
					for (int xo = 0; (xo < 8) && (x + xo < width); xo++) {
						int paletteColor = ((currByte << xo) & 0x80) >> 7;
						argbDos.writeInt (argbPalette[paletteColor]);
					}
					i++;
				}
			}
		}

		byte[] argbBytes = argbBaos.toByteArray ();
		ByteArrayInputStream uncompressedBais = new ByteArrayInputStream (argbBytes);
		this.innerPixelData = new RfbRawPixelData (this.rectangle);
		this.innerPixelData.read (uncompressedBais);

		//DEBUG
		System.out.println ("-readBasicCompressionPaletteFilter");

	}

	private void readBasicCompressionGradientFilter (InputStream in, InflaterInputStream infIs, WrappingInputStream packedIs) throws MessageException, IOException {

		//DEBUG
		System.out.println ("+readBasicCompressionGradientFilter");

		int width = this.rectangle.getWidth ();
		int height = this.rectangle.getHeight ();
		int pixelCount = width * height;

		int expectedLen = pixelCount * (this.session.getPixelFormat ().mayApplyTightPixel () ? 3 : Integer.BYTES);
		byte[] tightBytes = this.readBasicCompressionData (in, expectedLen, infIs, packedIs);

		ByteArrayInputStream tightBais = new ByteArrayInputStream (tightBytes);

		RfbPixelFormat pixelFormat = this.session.getPixelFormat ();
		int[] diffs = pixelFormat.readArgbTightPixels (tightBais, pixelCount);

		int predicted;
		int left;
		int top;
		int leftTop;
		int[] pixels = new int[pixelCount];

		ByteArrayOutputStream argbBaos = new ByteArrayOutputStream (pixelCount * Integer.BYTES);
		DataOutputStream argbDos = new DataOutputStream (argbBaos);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int leftPixel = x > 0 ? pixels[y * width + x - 1] : 0;
				int leftRed = pixelFormat.getRed (leftPixel);
				int leftGreen = pixelFormat.getGreen (leftPixel);
				int leftBlue = pixelFormat.getBlue (leftPixel);

				int topPixel = y > 0 ? pixels[(y - 1) * width + x] : 0;
				int topRed = pixelFormat.getRed (topPixel);
				int topGreen = pixelFormat.getGreen (topPixel);
				int topBlue = pixelFormat.getBlue (topPixel);

				int leftTopPixel = x > 0 && y > 0 ? pixels[(y - 1) * width + x - 1] : 0;
				int leftTopRed = pixelFormat.getRed (leftTopPixel);
				int leftTopGreen = pixelFormat.getGreen (leftTopPixel);
				int leftTopBlue = pixelFormat.getBlue (leftTopPixel);

				int predictedRed = leftRed + topRed - leftTopRed;
				if (predictedRed < 0) {
					predictedRed = 0;
				} else if (predictedRed > pixelFormat.getRedMax ()) {
					predictedRed = pixelFormat.getRedMax ();
				}

				int predictedGreen = leftGreen + topGreen - leftTopGreen;
				if (predictedGreen < 0) {
					predictedGreen = 0;
				} else if (predictedGreen > pixelFormat.getGreenMax ()) {
					predictedGreen = pixelFormat.getGreenMax ();
				}

				int predictedBlue = leftBlue + topBlue - leftTopBlue;
				if (predictedBlue < 0) {
					predictedBlue = 0;
				} else if (predictedBlue > pixelFormat.getBlueMax ()) {
					predictedBlue = pixelFormat.getBlueMax ();
				}

				int diff = diffs[y * width + x];
				int diffRed = pixelFormat.getRed (diff);
				int diffGreen = pixelFormat.getGreen (diff);
				int diffBlue = pixelFormat.getBlue (diff);

				int resultRed = predictedRed + (byte) diffRed;
				int resultGreen = predictedGreen + (byte) diffGreen;
				int resultBlue = predictedBlue + (byte) diffBlue;

				pixels[y * width + x] = pixelFormat.createPixel (resultRed, resultGreen, resultBlue);

				argbDos.writeInt (pixels[y * width + x]);
			}
		}

		byte[] argbBytes = argbBaos.toByteArray ();
		ByteArrayInputStream argbBais = new ByteArrayInputStream (argbBytes);
		this.innerPixelData = new RfbRawPixelData (this.rectangle);
		this.innerPixelData.read (argbBais);

		//DEBUG
		System.out.println ("-readBasicCompressionGradientFilter");

	}

	private byte[] readBasicCompressionData (InputStream in, int expectedLen, InflaterInputStream infIs, WrappingInputStream packedIs) throws IOException {

		// DEBUG
		System.out.println ("+readBasicCompressionData expectedLen: " + expectedLen);

		byte[] unpackedData = new byte[expectedLen];
		if (expectedLen > 12) {

			// DEBUG
			System.out.println ("Reading packed data length...");

			int packedLength = this.readTightLength (in);

			//DEBUG
			System.out.println ("Received packed data length: " + packedLength + "=0x" + Integer.toHexString (packedLength));

			byte[] packedData = new byte[packedLength];
			DataInputStream packedDataDis = new DataInputStream (in);
			packedDataDis.readFully (packedData);

			//DEBUG
			File p = File.createTempFile ("packedData", ".zlib");
			FileOutputStream pfos = new FileOutputStream (p);
			DataOutputStream pdos = new DataOutputStream (pfos);
			pdos.write (packedData);
			pdos.close ();
			pfos.close ();
			System.out.println ("Packed data array size (receivedLen + 1): " + packedData.length + "=0x" + Integer.toHexString (packedData.length));
			System.out.println ("Packed data written to " + p.getAbsolutePath ());
//			System.out.println ("Packed data (maximum first 4096 bytes):\n" + VncCommon.hexdump (packedData.length > 4096 ? Arrays.copyOf (packedData, 4096) : packedData));

			ByteArrayInputStream packedBais = new ByteArrayInputStream (packedData);
			packedIs.appendInputStreams (packedBais);

			DataInputStream unpackedDataDis = new DataInputStream (infIs);
			unpackedDataDis.readFully (unpackedData);

		} else {
			// DEBUG
			System.out.println ("Reading non-compressed data of len " + unpackedData.length);

			DataInputStream dis = new DataInputStream (in);
			dis.readFully (unpackedData);
		}

		// DEBUG
//		System.out.println ("Unpacked data:\n" + VncCommon.hexdump (data));
		// DEBUG
		System.out.println ("-readBasicCompressionData expectedLen: " + expectedLen);

		return Arrays.copyOfRange (unpackedData, 0, expectedLen);
	}

	private void readJpegCompression (InputStream in) throws MessageException, IOException {
	}

	private void readFillCompression (InputStream in) throws MessageException, IOException {
		// DEBUG
		System.out.println ("+readFillCompression()");

		int len = this.rectangle.getSession ().getPixelFormat ().mayApplyTightPixel () ? 3 : this.rectangle.getSession ().getPixelFormat ().getBitsPerPixel () / 8;

		int[] argbFillColor = this.session.getPixelFormat ().readArgbTightPixels (in, 1);

		ByteArrayOutputStream fillBytesBaos = new ByteArrayOutputStream (Integer.SIZE / 8);
		DataOutputStream fillBytesDos = new DataOutputStream (fillBytesBaos);
		fillBytesDos.writeInt (argbFillColor[0]);

		RepeatingInputStream ris = new RepeatingInputStream (fillBytesBaos.toByteArray ());

		this.innerPixelData = new RfbRawPixelData (this.rectangle);
		this.innerPixelData.read (ris);

		// DEBUG
		System.out.println ("-readFillCompression()");
	}

	private int readTightLength (InputStream in) throws IOException {
		// DEBUG
		System.out.println ("+readTightLength()");

		int b1 = in.read ();

		// DEBUG
//		System.out.println ("b1=" + b1 + " = " + Integer.toBinaryString (b1));

		if (b1 < 0) {
			throw new EOFException ("b1");
		}
		if (b1 <= 127) {
			int result = b1;

			// DEBUG
			System.out.println ("-readTightLength() result=" + result + " = " + Integer.toBinaryString (result));

			return result;
		}

		int b2 = in.read ();

		// DEBUG
//		System.out.println ("b2=" + b2 + " = " + Integer.toBinaryString (b2));

		if (b2 < 0) {
			throw new EOFException ("b2");
		}
		if (b2 <= 127) {
			int result = (b2 << 7) | (b1 & 0x7F);

			// DEBUG
			System.out.println ("-readTightLength() result=" + result + " = " + Integer.toBinaryString (result));

			return result;
		}

		int b3 = in.read ();

		// DEBUG
//		System.out.println ("b3=" + b3 + " = " + Integer.toBinaryString (b3));

		if (b3 < 0) {
			throw new EOFException ("b3");
		}

		int result = (b3 << 14) | ((b2 & 0x7F) << 7) | (b1 & 0x7F);

		// DEBUG
		System.out.println ("-readTightLength() result=" + result + " = " + Integer.toBinaryString (result));

		return result;
	}

	@Override
	public void write (OutputStream out) throws IOException {
		//TODO Implement generated write() in RfbTightPixelData
		throw new UnsupportedOperationException ("Not supported yet.");
	}

	@Override
	public void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException {
		Objects.requireNonNull (framebuffer, "framebuffer");

		Objects.requireNonNull (this.innerPixelData, "innerPixelData");

		this.innerPixelData.updateFramebuffer (framebuffer);
	}

}
