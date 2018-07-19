package by.spalex.bmp.coder;

import by.spalex.bmp.bitmap.Bitmap;
import by.spalex.bmp.bitmap.header.Bpp;
import by.spalex.bmp.bitmap.header.Compression;

import java.nio.ByteBuffer;

/**
 * Class performs encoding of text to bitmap file
 */
public class Encoder {
    private final Bitmap bitmap;

    /**
     * Create Encoder instance
     * @param bitmap bitmap instance
     */
    public Encoder(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Encode text into bitmap
     * @param textBytes text data
     * @return bitmap as array of byte
     * @throws IllegalArgumentException if bitmap Bpp is not supported. Supported Bpp is: Monochrome; EGA; VGA;
     * HIGH COLOR with BI_RGB, BI_BITFIELDS or  BI_ALPHABITFIELDS compressions; TRUE COLOR or DEEP COLOR
     */
    public byte[] encode(byte[] textBytes) {
        Bpp bpp = bitmap.getBitmapHeader().getBitsPerPixel();
        byte[] textSize;
        if (bpp.ordinal() < Bpp.HIGH_COLOR.ordinal()) {
            textSize = new byte[]{(byte) textBytes.length};
        } else {
            textSize = ByteBuffer.allocate(4).putInt(textBytes.length).array();
        }
        byte[] encodeBytes = new byte[textBytes.length + textSize.length];
        // first 2/4 byte -  size of textBytes array
        System.arraycopy(textSize, 0, encodeBytes, 0, textSize.length);
        System.arraycopy(textBytes, 0, encodeBytes, textSize.length, textBytes.length);

        Compression compression = bitmap.getBitmapHeader().getCompression();
        switch (bpp) {
            case MONOCHROME:
            case EGA:
            case VGA:
                return encodeColorPalette(encodeBytes);
            case HIGH_COLOR:
                switch (compression) {
                    case BI_RGB:
                        return encodeRGB555(encodeBytes);
                    case BI_BITFIELDS:
                    case BI_ALPHABITFIELDS:
                        return encodeRGB444(encodeBytes);
                }
                break;
            case TRUE_COLOR:
                return encodeTrueColor(encodeBytes);
            case DEEP_COLOR:
                return encodeRGB888(encodeBytes);
            default:

        }
        throw new IllegalStateException("Unsupported bitmap format");
    }

    /**
     * Encode text into bitmap using X2 B6 G8 R8 pattern
     * @param encodeBytes text data
     * @return bitmap as array of byte
     */
    private byte[] encodeTrueColor(byte[] encodeBytes) {
        byte[] image = bitmap.getBytes();
        int offset = (int) bitmap.getHeader().getOffset();
        for (byte toEncode : encodeBytes) {
            for (int y = 0; y < 8; y = y + 2) {
                if (((toEncode >> y) & 1) > 0) {
                    image[offset] |= 1;
                } else {
                    image[offset] &= 0xfe;
                }
                if (((toEncode >> (y + 1)) & 1) > 0) {
                    image[offset] |= 2;
                } else {
                    image[offset] &= 0xfd;
                }
                offset = offset + 3;
            }
        }
        return image;
    }

    /**
     * Encode text into bitmap using RGBQUAD pattern
     * @param encodeBytes text data
     * @return bitmap as array of byte
     */
    private byte[] encodeColorPalette(byte[] encodeBytes) {
        byte[] image = bitmap.getBytes();
        int offset = bitmap.getPaletteOffset() + 3;
        for (byte encodeByte : encodeBytes) {
            image[offset] = encodeByte;
            offset += 4;
        }
        return image;
    }


    /**
     * Encode text into bitmap using X8 R8 G8 B8 pattern
     * @param encodeBytes text data
     * @return bitmap as array of byte
     */
    private byte[] encodeRGB888(byte[] encodeBytes) {
        byte[] image = bitmap.getBytes();
        int offset = (int) bitmap.getHeader().getOffset();
        for (byte encodeByte : encodeBytes) {
            image[offset] = encodeByte;
            offset = offset + 4;
        }
        return image;
    }


    /**
     * Encoding text into bitmap using X1 R5 G5 B5 pattern
     * @param encodeBytes text data
     * @return image as byte's array
     */
    private byte[] encodeRGB555(byte[] encodeBytes) {
        byte[] image = bitmap.getBytes();
        int offset = (int) bitmap.getHeader().getOffset();
        for (byte toEncode : encodeBytes) {
            for (int y = 0; y < 8; y++) {
                if (((toEncode >> y) & 1) > 0) {
                    image[offset] |= 1;
                } else {
                    image[offset] &= ~1;
                }
                offset = offset + 2;
            }
        }
        return image;
    }

    /**
     * Encoding text into bitmap using X4 R4 G4 B4 pattern
     * @param encodeBytes text data
     * @return image as byte's array
     */
    private byte[] encodeRGB444(byte[] encodeBytes) {
        byte[] image = bitmap.getBytes();
        int offset = (int) bitmap.getHeader().getOffset() + 1;
        for (byte toEncode : encodeBytes) {
            byte low = (byte) (toEncode & 0xf0);
            byte high = (byte) ((toEncode & 0x0f) << 4);
            image[offset] = (byte) (image[offset] & 0x0f);
            image[offset] = (byte) (image[offset] | low);
            offset = offset + 2;
            image[offset] = (byte) (image[offset] & 0x0f);
            image[offset] = (byte) (image[offset] | high);
            offset = offset + 2;
        }
        return image;
    }

}

