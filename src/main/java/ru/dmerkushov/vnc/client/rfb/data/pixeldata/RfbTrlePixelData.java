/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.data.pixeldata;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.data.RfbPixelFormat;
import ru.dmerkushov.vnc.client.rfb.data.RfbRectangle;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readU8;
import ru.dmerkushov.vnc.client.rfb.session.RfbFramebuffer;

/**
 *
 * @author dmerkushov
 */
public class RfbTrlePixelData extends RfbPixelData {

	Tile[] tiles;

	int lastTileWidth;
	int lastTileHeight;
	int tileCountX;
	int tileCountY;

	public RfbTrlePixelData (RfbRectangle rectangle) {
		super (rectangle);

		tileCountX = (rectangle.getWidth () + 1) / 16;
		tileCountY = (rectangle.getHeight () + 1) / 16;
		tiles = new Tile[tileCountX * tileCountY];

		lastTileWidth = rectangle.getWidth () % 16;
		if (lastTileWidth == 0) {
			lastTileWidth = 16;
		}
		lastTileHeight = rectangle.getHeight () % 16;
		if (lastTileHeight == 0) {
			lastTileHeight = 16;
		}
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		PaletteTile previousPaletteTile = null;

		for (int tileY = 0; tileY < tileCountY; tileY++) {
			int y = tileY * 16;

			int tileHeight = y < tileCountY - 1 ? 16 : lastTileHeight;

			for (int tileX = 0; tileX < tileCountX; tileX++) {
				int x = tileX * 16;

				int tileWidth = x < tileCountX - 1 ? 16 : lastTileWidth;

				Tile tile = Tile.readTile (rectangle.pixelFormat, tileWidth, tileHeight, x, y, in, previousPaletteTile);

				tiles[tileY * tileCountX + tileX] = tile;

				if (tile instanceof PaletteTile) {
					previousPaletteTile = (PaletteTile) tile;
				}
			}
		}
	}

	@Override
	public void write (OutputStream out) throws IOException {
		//TODO Implement generated write() in RfbTrlePixelData
		throw new UnsupportedOperationException ("Not supported yet.");
	}

	@Override
	public void updateFramebuffer (RfbFramebuffer framebuffer) throws RfbPixelDataException {
		//TODO Implement generated updateFramebuffer() in RfbTrlePixelData
		throw new UnsupportedOperationException ("Not supported yet.");
	}

	static abstract class Tile extends BufferedImage {

		public final RfbPixelFormat pixelFormat;
		public final int x;
		public final int y;
		public final int width;
		public final int height;
		protected int subencoding;

		public Tile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height) {
			super (width, height, BufferedImage.TYPE_INT_ARGB);

			Objects.requireNonNull (pixelFormat, "pixelFormat");

			this.pixelFormat = pixelFormat;
			this.x = x;
			this.y = y;
			this.width = getWidth ();
			this.height = getHeight ();
			this.subencoding = subencoding;
		}

		public void updateImage (BufferedImage image) {
			WritableRaster dstRaster = image.getRaster ();
			dstRaster.setRect (x, y, this.getData ());
		}

		public abstract void read (InputStream in) throws IOException;

		public static Tile readTile (RfbPixelFormat pixelFormat, int width, int height, int x, int y, InputStream in, PaletteTile previousPaletteTile) throws IOException {
			Tile tile = null;

			int subencoding = readU8 (in);
			if (subencoding == 0) {
				tile = new RawTile (pixelFormat, subencoding, x, y, width, height);
			} else if (subencoding == 1) {
				tile = new SolidTile (pixelFormat, subencoding, x, y, width, height);
			} else if (2 <= subencoding && subencoding <= 16) {
				tile = new PackedPaletteTile (pixelFormat, subencoding, x, y, width, height, previousPaletteTile, null);
			} else if (17 <= subencoding && subencoding <= 126) {
				//TODO Maybe throw an exception like "unused subencoding"
			} else if (subencoding == 127) {
				tile = new ReusedPackedPaletteTile (pixelFormat, subencoding, x, y, width, height, previousPaletteTile, null);
			} else if (subencoding == 128) {
				tile = new PlainRleTile (pixelFormat, subencoding, x, y, width, height);
			} else if (subencoding == 129) {
				tile = new ReusedPaletteRleTile (pixelFormat, subencoding, x, y, width, height, previousPaletteTile, null);
			} else if (130 <= subencoding && subencoding <= 255) {
				tile = new PaletteRleTile (pixelFormat, subencoding, x, y, width, height, previousPaletteTile, null);
			}

			tile.read (in);

			return tile;
		}

	}

	static class RawTile extends Tile {

		public RawTile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height) {
			super (pixelFormat, subencoding, x, y, width, height);
		}

		@Override
		public void read (InputStream in) throws IOException {
			BufferedImage image = pixelFormat.readArgbCompressedImage (width, height, in);
			Raster raster = image.getData ();
			this.setData (raster);
		}

	}

	static class SolidTile extends Tile {

		public SolidTile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height) {
			super (pixelFormat, subencoding, x, y, width, height);
		}

		@Override
		public void read (InputStream in) throws IOException {
			int singlePixelColor = pixelFormat.readArgbCompressedPixels (in, 1)[0];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					this.setRGB (x, y, singlePixelColor);
				}
			}
		}

	}

	static abstract class PaletteTile extends Tile {

		protected PaletteTile previousPaletteTile;
		protected int[] palette;

		public PaletteTile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height, PaletteTile previousPaletteTile, int[] palette) {
			super (pixelFormat, subencoding, x, y, width, height);

			this.palette = palette;		// palette may be null
			this.previousPaletteTile = previousPaletteTile;
		}

	}

	static class PackedPaletteTile extends PaletteTile {

		public PackedPaletteTile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height, PaletteTile previousPaletteTile, int[] palette) {
			super (pixelFormat, subencoding, x, y, width, height, previousPaletteTile, palette);
		}

		public void readPalette (InputStream in, int paletteSize) throws IOException {

			palette = pixelFormat.readArgbCompressedPixels (in, paletteSize);

		}

		public static int[] readPixels (InputStream in, int[] palette, int width, int height) throws IOException {

			int paletteSize = palette.length;

			int packedPixelsSize;

			if (paletteSize == 2) {
				packedPixelsSize = (width + 7) / 8 * height;
			} else if (3 <= paletteSize && paletteSize <= 4) {
				packedPixelsSize = (width + 3) / 4 * height;
			} else if (5 <= paletteSize && paletteSize <= 16) {
				packedPixelsSize = (width + 1) / 2 * height;
			} else {
				throw new IllegalStateException ("Palette size not between 2 and 16: " + paletteSize);
			}
			byte[] packedPixels = new byte[packedPixelsSize];

			DataInputStream dis = new DataInputStream (in);
			dis.readFully (packedPixels);

			ByteArrayInputStream packedBais = new ByteArrayInputStream (packedPixels);
			ByteArrayOutputStream unpackedBaos = new ByteArrayOutputStream ();

			for (int y = 0; y < height; y++) {
				int currentX = 0;
				while (currentX < width) {
					int packed = packedBais.read ();
					int unpacked;
					if (paletteSize == 2) {
						unpacked = (packed & 0x80) >> 7;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x40) >> 6;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x20) >> 5;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x10) >> 4;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x08) >> 3;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x04) >> 2;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x02) >> 1;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x01) >> 0;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}
					} else if (paletteSize <= 4) {
						unpacked = (packed & 0xC0) >> 6;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x30) >> 4;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x0C) >> 2;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x03) >> 0;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}
					} else if (paletteSize <= 16) {

						unpacked = (packed & 0xF0) >> 4;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}

						unpacked = (packed & 0x0F) >> 0;
						unpackedBaos.write (unpacked);
						currentX++;
						if (currentX >= width) {
							break;
						}
					}
				}
			}

			byte[] unpackedBytes = unpackedBaos.toByteArray ();
			int[] argbPixels = new int[unpackedBytes.length];
			for (int i = 0; i < unpackedBytes.length; i++) {
				argbPixels[i] = palette[unpackedBytes[i]];
			}

			return argbPixels;
		}

		@Override
		public void read (InputStream in) throws IOException {
			int paletteSize = subencoding;		// 2 to 16, inclusive

			readPalette (in, paletteSize);

			int[] argbPixels = readPixels (in, palette, width, height);

			this.setRGB (0, 0, width, height, argbPixels, 0, width);
		}

	}

	static class ReusedPackedPaletteTile extends PaletteTile {

		public ReusedPackedPaletteTile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height, PaletteTile previousPaletteTile, int[] palette) {
			super (pixelFormat, subencoding, x, y, width, height, previousPaletteTile, palette);

			Objects.requireNonNull (previousPaletteTile, "previousPaletteTile");
		}

		@Override
		public void read (InputStream in) throws IOException {

			this.palette = previousPaletteTile.palette;

			int[] argbPixels = PackedPaletteTile.readPixels (in, palette, width, height);
		}

	}

	static class PlainRleTile extends Tile {

		public PlainRleTile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height) {
			super (pixelFormat, subencoding, x, y, width, height);
		}

		@Override
		public void read (InputStream in) throws IOException {
			int[] argbPixels = new int[width * height];

			int pixelsLen = argbPixels.length;

			int index = 0;
			while (index < pixelsLen) {
				int argbPixel = pixelFormat.readArgbCompressedPixels (in, 1)[0];

				int runLength = 0;
				int currLength = 0;
				do {
					currLength = readU8 (in);
					runLength += currLength;
				} while (currLength >= 255);

				for (int i = index; i < Math.min (index + runLength, pixelsLen); i++) {
					argbPixels[i] = argbPixel;
				}

				index += runLength;
			}

			this.setRGB (0, 0, width, height, argbPixels, 0, width);
		}

	}

	static class PaletteRleTile extends PaletteTile {

		public PaletteRleTile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height, PaletteTile previousPaletteTile, int[] palette) {
			super (pixelFormat, subencoding, x, y, width, height, previousPaletteTile, palette);
		}

		public void readPalette (InputStream in, int paletteSize) throws IOException {
			palette = pixelFormat.readArgbCompressedPixels (in, paletteSize);
		}

		@Override
		public void read (InputStream in) throws IOException {
			int paletteSize = subencoding - 128;		// 130<=subencoding<=255, so this ,ay resolve into 2 to 127, inclusive

			readPalette (in, paletteSize);

			int[] argbPixels = readPixels (in, palette, width, height, pixelFormat);

			this.setRGB (0, 0, width, height, argbPixels, 0, width);
		}

		public static int[] readPixels (InputStream in, int[] palette, int width, int height, RfbPixelFormat pixelFormat) throws IOException {
			int[] argbPixels = new int[width * height];

			int pixelsLen = argbPixels.length;

			int index = 0;
			while (index < pixelsLen) {
				int palettePixel = readU8 (in);

				int argbPixel = palette[palettePixel];

				int runLength = 0;
				int currLength = 0;
				do {
					currLength = readU8 (in);
					runLength += currLength;
				} while (currLength >= 255);

				for (int i = index; i < Math.min (index + runLength, pixelsLen); i++) {
					argbPixels[i] = argbPixel;
				}

				index += runLength;
			}

			return argbPixels;
		}

	}

	static class ReusedPaletteRleTile extends PaletteTile {

		public ReusedPaletteRleTile (RfbPixelFormat pixelFormat, int subencoding, int x, int y, int width, int height, PaletteTile previousPaletteTile, int[] palette) {
			super (pixelFormat, subencoding, x, y, width, height, previousPaletteTile, palette);

			Objects.requireNonNull (previousPaletteTile, "previousPaletteTile");
		}

		@Override
		public void read (InputStream in) throws IOException {
			this.palette = previousPaletteTile.palette;

			int[] argbPixels = PaletteRleTile.readPixels (in, palette, width, height, pixelFormat);

			this.setRGB (0, 0, width, height, argbPixels, 0, width);
		}

	}

}
