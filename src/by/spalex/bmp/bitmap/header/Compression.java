package by.spalex.bmp.bitmap.header;

/**
 * enum determines possible compression types of bitmap
 */
public enum Compression {
    BI_RGB(0),
    BI_RLE8(1),
    BI_RLE4(2),
    BI_BITFIELDS(3),
    BI_ALPHABITFIELDS(6);

    private final int value;

    Compression(int value) {
        this.value = value;
    }

    /**
     * @param value raw unsigned value of compression type from file
     * @return enum value
     */
    public static Compression parse(int value) {
        for (Compression compression : values()) {
            if (compression.value == value) {
                return compression;
            }
        }
        throw new IllegalArgumentException("Unsupported compression " + value);
    }
}
