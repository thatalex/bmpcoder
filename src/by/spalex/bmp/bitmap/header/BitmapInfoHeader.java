package by.spalex.bmp.bitmap.header;

import java.nio.ByteBuffer;

/**
 * Describes info header structure of bitmap
 */
public class BitmapInfoHeader implements BitmapHeader {
    private static final int[] HEADER_SIZE = {40, 52, 56, 124};
    private final int headerSize;

    private long width;
    // if height > 0 then bitmap data is upside down (i.e. last scanline first)
    // else height = abs(height) and bitmap data is ordered normally
    private long height;
    // 1
    private short planes;
    private Bpp bitsPerPixel;
    private Compression compression;
    private long bitmapDataSize;
    private int horPixelsPerMeter;
    private int vertPixelsPerMeter;
    // only relevant for bits_per_pixel < 16; if set to zero, it's assumed to be 2^bits_per_pixel
    private long numberOfColors;
    private int numberOfImportantColors;

    /**
     * Constructs info header from bitmap file contained at ByteBuffer
     * @param buffer bitmap file as ByteBuffer
     * @throws IllegalArgumentException in case of invalid header's size
     */
    public BitmapInfoHeader(ByteBuffer buffer) {
        headerSize = (int) (Integer.toUnsignedLong(buffer.getInt(14)));

        if (!isSupportedSize(headerSize)) {
            throw new IllegalArgumentException("Header size " + headerSize + " are not supported");
        }

        width = Integer.toUnsignedLong(buffer.getInt(18));
        height = Integer.toUnsignedLong(buffer.getInt(22));
        planes = (short) Short.toUnsignedInt(buffer.getShort(26));
        bitsPerPixel = Bpp.parse(Short.toUnsignedInt(buffer.getShort(28)));
        compression = Compression.parse((int) Integer.toUnsignedLong(buffer.getInt(30)));
        bitmapDataSize = Integer.toUnsignedLong(buffer.getInt(34));
        horPixelsPerMeter = (int) (Integer.toUnsignedLong(buffer.getInt(38)));
        vertPixelsPerMeter = (int) (Integer.toUnsignedLong(buffer.getInt(42)));
        numberOfColors = Integer.toUnsignedLong(buffer.getInt(46));
        numberOfImportantColors = (int) (Integer.toUnsignedLong(buffer.getInt(50)));
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    public short getPlanes() {
        return planes;
    }

    @Override
    public short getPaletteSize() {
        if (bitsPerPixel.isColorTable()) {
            if (numberOfColors != 0) {
                return (short) numberOfColors;
            } else {
                return (short) Math.pow(2, bitsPerPixel.getValue());
            }
        } else {
            return 0;
        }
    }

    @Override
    public int getSize() {
        return headerSize;
    }

    public Bpp getBitsPerPixel() {
        return bitsPerPixel;
    }

    public Compression getCompression() {
        return compression;
    }

    public long getBitmapDataSize() {
        return bitmapDataSize;
    }

    public int getHorPixelsPerMeter() {
        return horPixelsPerMeter;
    }

    public int getVertPixelsPerMeter() {
        return vertPixelsPerMeter;
    }

    public long getNumberOfColors() {
        return numberOfColors;
    }

    public int getNumberOfImportantColors() {
        return numberOfImportantColors;
    }

    public static boolean isSupportedSize(int bitmapHeaderSize) {
        for (int size : HEADER_SIZE) {
            if (size == bitmapHeaderSize) {
                return true;
            }
        }
        return false;
    }
}
