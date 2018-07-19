package by.spalex.bmp.bitmap;

import by.spalex.bmp.bitmap.header.*;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Class describing structure of bitmap file
 */
public class Bitmap {

    private static final int HEADER_SIZE = 14;
    private final byte[] bytes;
    private final Header header;
    private BitmapHeader bitmapHeader;
    private Color bitmask;
    private Color[] palette;
    private int paletteOffset = 0;

    /**
     * Constructs bitmap from file as byte array
     * @param bytes bitmap file as byte array
     */
    public Bitmap(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
        header = new Header(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        setBitmapHeader(buffer);
    }

    /**
     * determines bitmap's header structure type and set this header if it supported
     * @param buffer bitmap file as ByteBuffer
     * @throws IllegalArgumentException if bitmap header is not has CoreHeader or InfoHeader structures
     */
    private void setBitmapHeader(ByteBuffer buffer) {
        long bitmapHeaderSize = Integer.toUnsignedLong(buffer.getInt(HEADER_SIZE));
        int offset = HEADER_SIZE;
        if (BitmapInfoHeader.isSupportedSize((int) bitmapHeaderSize)) {
            BitmapInfoHeader bitmapInfoHeader = new BitmapInfoHeader(buffer);
            offset += bitmapInfoHeader.getSize();
            if (bitmapInfoHeader.getCompression().equals(Compression.BI_BITFIELDS)) {
                if (header.getOffset() >= offset + 16) {
                    bitmask = new Color(buffer.getInt(offset), buffer.getInt(offset + 4),
                            buffer.getInt(offset + 8));
                    offset = +24;
                }
            } else if (bitmapInfoHeader.getCompression().equals(Compression.BI_ALPHABITFIELDS)) {
                if (header.getOffset() >= offset + 20) {
                    bitmask = new Color(buffer.getInt(offset), buffer.getInt(offset + 4),
                            buffer.getInt(offset + 8), buffer.getInt(offset + 16));
                    offset = +24;
                }
            }
            this.bitmapHeader = bitmapInfoHeader;
        } else if (bitmapHeaderSize == BitmapCoreHeader.HEADER_SIZE) {
            bitmapHeader = new BitmapCoreHeader(buffer);
            offset += bitmapHeader.getSize();
        } else {
            throw new IllegalArgumentException("Invalid BitmapHeader size " + bitmapHeaderSize);
        }
        filPalette(bitmapHeader.getPaletteSize(), buffer, offset);
    }

    /**
     * fill color palette
     * @param size size of palette
     * @param buffer bitmap file as ByteBuffer
     * @param offset palette offset from begin of file
     */
    private void filPalette(short size, ByteBuffer buffer, int offset) {
        palette = new Color[size];
        paletteOffset = offset;
        for (int i = 0; i < size; i++) {
            int b = Byte.toUnsignedInt(buffer.get(offset));
            offset = +4;
            int g = Byte.toUnsignedInt(buffer.get(offset));
            offset = +4;
            int r = Byte.toUnsignedInt(buffer.get(offset));
            offset = +4;
            int a = Byte.toUnsignedInt(buffer.get(offset));
            offset = +4;
            palette[i] = new Color(r, g, b, a);
        }
    }

    public int getPaletteOffset() {
        return paletteOffset;
    }

    /**
     * @return copy of file as byte array
     */
    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public Header getHeader() {
        return header;
    }

    public BitmapHeader getBitmapHeader() {
        return bitmapHeader;
    }

    public Color getBitmask() {
        return bitmask;
    }

    /**
     * @return number of pixels in row
     */
    private long getRowSize() {
        double value = (bitmapHeader.getBitsPerPixel().getValue() * bitmapHeader.getWidth() + 31) / 32;
        return (long) (Math.floor(value) * 4);
    }

    /**
     * @return number of pixels image data
     */
    public long getPixelArraySize() {
        return getRowSize() * bitmapHeader.getHeight();
    }

    /**
     * Calculate byte that can be encoded into bitmap file
     * @return count of possible bytes for encoding
     */
    public int getEncodeCapacity() {
        int pixelCount = (int) (bitmapHeader.getHeight() * bitmapHeader.getWidth());
        switch (bitmapHeader.getBitsPerPixel()) {
            case DEEP_COLOR:
                return pixelCount - 4;
            case HIGH_COLOR: {
                switch (bitmapHeader.getCompression()) {
                    case BI_RGB:
                        return (int) (Math.ceil(pixelCount / 8) - 4);
                    case BI_BITFIELDS:
                        return (int) (Math.ceil(pixelCount / 2) - 4);
                }
                return 0;
            }
            case TRUE_COLOR:
                return (int) (Math.ceil(pixelCount / 4) - 4);
            case VGA:
            case EGA:
            case MONOCHROME:
                return palette.length - 1;
            default:
                return 0;
        }
    }
}
