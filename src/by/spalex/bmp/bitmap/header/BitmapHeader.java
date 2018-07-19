package by.spalex.bmp.bitmap.header;

/**
 * Interface describing generic bitmap's header properties
 */
public interface BitmapHeader {
    short getPaletteSize();

    int getSize();

    Compression getCompression();

    Bpp getBitsPerPixel();

    long getWidth();

    long getHeight();
}
