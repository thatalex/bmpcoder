package by.spalex.bmp.bitmap.header;

/**
 * enum determines possible bits per pixel values in bitmap format
 */
public enum Bpp {
    MONOCHROME(1),
    EGA(4),
    VGA(8),
    HIGH_COLOR(16),
    TRUE_COLOR(24),
    DEEP_COLOR(32);

    private final int value;

    Bpp(int value){
        this.value = value;
    }

    /**
     * @param value bits per pixel
     * @return enum value
     */
    public static Bpp parse(int value){
        for (Bpp bpp: values()){
            if (bpp.value == value){
                return bpp;
            }
        }
        throw new IllegalArgumentException("Unsupported bpp " + value);
    }

    /**
     * @return true if bpp has color table
     */
    public boolean isColorTable() {
        return value <= 8;
    }

    public int getValue() {
        return value;
    }
}
