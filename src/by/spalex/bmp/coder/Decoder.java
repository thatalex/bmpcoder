package by.spalex.bmp.coder;

import by.spalex.bmp.bitmap.Bitmap;
import by.spalex.bmp.bitmap.header.Bpp;
import by.spalex.bmp.bitmap.header.Compression;

import java.nio.ByteBuffer;

/**
 * Class performs decoding of encoded text from bitmap file
 */
public class Decoder {

    private final Bitmap bitmap;

    /**
     * Create Decoder instance
     * @param bitmap bitmap instance
     */
    public Decoder(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Decode text from bitmap
     * @return encoded text
     * @throws IllegalArgumentException if bitmap Bpp is not supported. Supported Bpp is: Monochrome; EGA; VGA;
     * HIGH COLOR with BI_RGB, BI_BITFIELDS or  BI_ALPHABITFIELDS compressions; TRUE COLOR or DEEP COLOR
     */
    public String decode() {
        Bpp bpp = bitmap.getBitmapHeader().getBitsPerPixel();
        Compression compression = bitmap.getBitmapHeader().getCompression();
        switch (bpp) {
            case MONOCHROME:
            case EGA:
            case VGA:
                return decodeColorPallete();
            case HIGH_COLOR:
                switch (compression) {
                    case BI_RGB:
                        return decodeRGB555();
                    case BI_BITFIELDS:
                    case BI_ALPHABITFIELDS:
                        return decodeRGB444();
                }
                break;
            case TRUE_COLOR:
                return decodeTrueColor();
            case DEEP_COLOR:
                return decodeRGB888();
        }
        throw new IllegalStateException("Unsupported bitmap format");
    }

    /**
     * Decode text from true color bitmap with R8 G8 B8 pattern
     * @return decoded text
     */
    private String decodeTrueColor() {
        byte[] bitmapBytes = bitmap.getBytes();
        int offset = (int) bitmap.getHeader().getOffset();
        byte[] sizeBytes = new byte[4];
        // first 4 byte -  size of encoded bytes array
        for (int i = 0; i < 4; i++) {
            sizeBytes[i] = getTrueColorByte(bitmapBytes, offset);
            offset += 12;
        }
        int size = ByteBuffer.wrap(sizeBytes).getInt();
        if (size > bitmap.getHeader().getSize() - 14 || size <= 0) return "";
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = getTrueColorByte(bitmapBytes, offset);
            offset += 12;
        }
        return new String(bytes);
    }

    /**
     * retrieve byte by picking 2 bit from every 3 bytes
     * @param bitmapByte file as byte array
     * @param offset encoded byte offset
     * @return encoded byte
     */
    private byte getTrueColorByte(byte[] bitmapByte, int offset) {
        byte value = 0x0;
        for (int y = 0; y < 8; y = y + 2) {
            if ((bitmapByte[offset] & 1) > 0) {
                value |= (1 << y);
            }
            if ((bitmapByte[offset] & 2) > 0) {
                value |= (1 << (y + 1));
            }
            offset = offset + 3;
        }
        return value;
    }

    /**
     * Decode text from bitmap with color palette and RGBQUAD pattern
     * @return decoded text
     */
    private String decodeColorPallete() {
        byte[] bitmapBytes = bitmap.getBytes();

        int offset = bitmap.getPaletteOffset() + 3;
        int size = Byte.toUnsignedInt(bitmapBytes[offset]);
        if (size == 0) return "";
        offset += 4;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = bitmapBytes[offset];
            offset = offset + 4;
        }
        return new String(bytes);
    }

    /**
     * Decode text from bitmap with X8 R8 G8 B8 pattern
     * @return decoded text
     */
    private String decodeRGB888() {
        byte[] bitmapBytes = bitmap.getBytes();
        int offset = (int) bitmap.getHeader().getOffset();
        byte[] sizeBytes = new byte[4];
        // first 4 byte -  size of encoded bytes array
        for (int i = 0; i < 4; i++) {
            sizeBytes[i] = bitmapBytes[offset];
            offset = offset + 4;
        }
        int size = ByteBuffer.wrap(sizeBytes).getInt();
        if (size > bitmap.getHeader().getSize() - 14 || size <= 0) return "";
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = bitmapBytes[offset];
            offset = offset + 4;
        }
        return new String(bytes);
    }

    /**
     * Decode text from bitmap with  X1 R5 G5 B5 pattern
     * @return decoded text
     */
    private String decodeRGB555() {
        byte[] bitmapBytes = bitmap.getBytes();
        int offset = (int) bitmap.getHeader().getOffset();
        byte[] sizeBytes = new byte[4];
        // first 4 byte -  size of encoded bytes array
        for (int i = 0; i < 4; i++) {
            sizeBytes[i] = getRGB555Byte(bitmapBytes, offset);
            offset += 16;
        }
        int size = ByteBuffer.wrap(sizeBytes).getInt();
        if (size > bitmap.getHeader().getSize() - 14 || size <= 0) return "";
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = getRGB555Byte(bitmapBytes, offset);
            offset += 16;
        }
        return new String(bytes);
    }

    /**
     * retrieves encoded byte from byte array by RGB555 pattern
     * @param bitmapByte file as byte array
     * @param offset encoded byte offset
     * @return encoded byte
     */
    private byte getRGB555Byte(byte[] bitmapByte, int offset) {
        byte value = 0x0;
        for (int y = 0; y < 8; y++) {
            if ((bitmapByte[offset] & 1) > 0) {
                value |= (1 << y);
            }
            offset = offset + 2;
        }
        return value;
    }

    /**
     * Decode text from bitmap with  X4 R4 G4 B4 pattern
     * @return decoded text
     */
    private String decodeRGB444() {
        byte[] bitmapBytes = bitmap.getBytes();
        int offset = (int) bitmap.getHeader().getOffset() + 1;
        byte[] sizeBytes = new byte[4];
        // first 4 byte -  size of encoded bytes array
        for (int i = 0; i < 4; i++) {
            sizeBytes[i] = getRGB444Byte(bitmapBytes, offset);
            offset += 4;
        }
        int size = ByteBuffer.wrap(sizeBytes).getInt();
        if (size > bitmap.getHeader().getSize() - 14 || size <= 0) return "";
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = getRGB444Byte(bitmapBytes, offset);
            offset += 4;
        }
        return new String(bytes);
    }

    /**
     * retrieves encoded byte from byte array by RGB444 pattern
     * @param imageBytes file as byte array
     * @param offset encoded byte offset
     * @return encoded byte
     */
    private byte getRGB444Byte(byte[] imageBytes, int offset) {
        byte value = 0x0;
        value = (byte) (value | ((imageBytes[offset] & 0xf0)));
        value = (byte) (value | ((imageBytes[offset + 2] & 0xf0)) >> 4);
        return value;
    }
}
