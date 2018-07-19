package by.spalex.bmp.ui;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Utility class
 */
public enum Util {
    ;
    public final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static final ResourceBundle lang = getLangBundle();

    /**
     * create and return new FileChooser instance
     *
     * @param title            title filterName
     * @param filterName       name of filter
     * @param filterExtenstion extension of filter (*.[extenstion])
     * @return {@link FileChooser}
     */
    public static FileChooser getFileChooser(String title, String filterName, String filterExtenstion) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File("."));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(filterName, filterExtenstion);
        fileChooser.getExtensionFilters().add(filter);
        return fileChooser;
    }

    /**
     * show error message
     *
     * @param title title of alert
     * @param text  text of error
     */
    public static void showError(String title, String text) {
        showMessage(title, text, Alert.AlertType.ERROR);
    }

    /**
     * show info message
     *
     * @param title title of alert
     * @param text  text of error
     */
    public static void showInfo(String title, String text) {
        showMessage(title, text, Alert.AlertType.INFORMATION);
    }

    /**
     * show warning message
     *
     * @param title title of alert
     * @param text  text of error
     */
    public static void showWarning(String title, String text) {
        showMessage(title, text, Alert.AlertType.WARNING);
    }

    /**
     * show message
     *
     * @param title     title of alert
     * @param text      text of error
     * @param alertType alert type
     */
    private static void showMessage(String title, String text, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private static ResourceBundle getLangBundle() {
        return ResourceBundle.getBundle("by.spalex.bmp.ui.lang");
    }

    /**
     * @param key key of i18 value
     * @return i18 value
     */
    public static String getString(String key){
        return lang.getString(key);
    }

    /**
     * Converts bytes to string as Hex symbols
     */
    public static String hexToString(byte... bytes){
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            int v = aByte & 0xFF;
            builder.append(hexArray[v >> 4]);
            builder.append(hexArray[v & 0xF]);
        }
        return builder.toString();
    }
}
