package by.spalex.bmp.bitmap.header;

import java.nio.ByteBuffer;

/**
 * Describes core header structure of bitmap
 */
public class BitmapCoreHeader implements BitmapHeader {
    public static final int HEADER_SIZE = 12;
    private long width;
    private long height;
    // 1
    private short planes;
    // must be 1, 4, 8, 24
    private Bpp bitsPerPixel;

    /**
     * Constructs core header from bitmap file contained at ByteBuffer
     * @param buffer bitmap file as ByteBuffer
     * @throws IllegalArgumentException in case of invalid header's size or invalid Bpp
     */
    public BitmapCoreHeader(ByteBuffer buffer) {
        int headerSize = (int) Integer.toUnsignedLong(buffer.getInt(14));
        if (headerSize != HEADER_SIZE) {
            throw new IllegalArgumentException("Header size are not equal to 12");
        }
        width = (short) Short.toUnsignedInt(buffer.getShort(18));
        height = (short) Short.toUnsignedInt(buffer.getShort(20));
        planes = (short) Short.toUnsignedInt(buffer.getShort(22));
        bitsPerPixel = Bpp.parse(Short.toUnsignedInt(buffer.getShort(24)));
        if (bitsPerPixel.equals(Bpp.HIGH_COLOR) || bitsPerPixel.equals(Bpp.DEEP_COLOR)) {
            throw new IllegalArgumentException("Invalid bpp " + bitsPerPixel.name());
        }
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
        return bitsPerPixel.isColorTable() ? (short) Math.pow(2, bitsPerPixel.getValue()) : 0;
    }

    @Override
    public int getSize() {
        return HEADER_SIZE;
    }

    @Override
    public Compression getCompression() {
        return null;
    }

    public Bpp getBitsPerPixel() {
        return bitsPerPixel;
    }
}
