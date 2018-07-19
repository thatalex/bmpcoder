package by.spalex.bmp.bitmap.header;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class describe main header of bitmap
 */
public class Header {

    // every bitmap must contain this magic bytes in begin of file
    private final byte[] magic = new byte[]{0x42, 0x4D};
    private long size;
    private long offset;


    /**
     * Constructs bitmap's header
     * @param bytes bitmap file as byte array
     * @throws IllegalArgumentException in case of absent magic bytes, invalid size or invalid offset of image data
     */
    public Header(byte[] bytes) {
        if (bytes.length < 15) throw new IllegalArgumentException("Invalid header size " + bytes.length);
        if (magic[0] != bytes[0] || magic[1] != bytes[1]) {
            throw new IllegalArgumentException("Invalid header " + (char) bytes[0] + (char) bytes[1]);
        }
        long size = Integer.toUnsignedLong(ByteBuffer.wrap(bytes, 2, 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
        if (size != bytes.length) {
            throw new IllegalArgumentException("Invalid size " + size);
        }
        long offset = Integer.toUnsignedLong(ByteBuffer.wrap(bytes, 10, 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
        if (offset + 14 > bytes.length) {
            throw new IllegalArgumentException("Invalid offset " + offset);
        }

        this.size = size;
        this.offset = offset;
    }

    public byte[] getMagic() {
        return magic;
    }

    public long getSize() {
        return size;
    }

    public long getOffset() {
        return offset;
    }
}