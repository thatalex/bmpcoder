package by.spalex.bmp.ui.controller;

import by.spalex.bmp.bitmap.Bitmap;
import by.spalex.bmp.coder.Encoder;
import by.spalex.bmp.ui.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Controller for encoding text view
 */
public class EncodeController extends Controller {
    @FXML
    private TextArea textArea;
    @FXML
    private Button encodeButton;
    @FXML
    private Label statusLabel;
    private Bitmap bitmap;

    private int encodeCapacity;

    /**
     * Encode text from TextArea into bitmap
     */
    public void encode(ActionEvent actionEvent) {
        byte[] text = textArea.getText().getBytes();
        if (text.length > encodeCapacity) {
            Util.showWarning(Util.getString("text_encode"),
                    String.format(Util.getString("text.exceeded.encoding.capacity"),
                            text.length - encodeCapacity));
            text = Arrays.copyOf(text, encodeCapacity);
            textArea.setText(new String(text));
            return;
        }
        Encoder encoder = new Encoder(bitmap);
        Window window = ((Node) actionEvent.getSource()).getScene().getWindow();
        File file = Util.getFileChooser(Util.getString("text_encode"), "bitmap", "*.bmp").showSaveDialog(window);
        if (file != null) {
            try {
                byte[] encoded = encoder.encode(text);
                Files.write(file.toPath(), encoded, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                Util.showInfo(Util.getString("text_encode"), Util.getString("text.successfully.encoded"));
            } catch (Exception e) {
                Util.showError(Util.getString("text_encode"), e.toString());
            }
        }
    }

    /**
     * load text for encoding from text file
     */
    public void loadText(ActionEvent actionEvent) {
        Window window = ((Node) actionEvent.getSource()).getScene().getWindow();
        File file = Util.getFileChooser(Util.getString("load_text"), "text", "*.txt").showOpenDialog(window);
        if (file != null && file.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                textArea.setText(new String(bytes));
                onKeyReleased(null);
            } catch (IOException e) {
                Util.showError(Util.getString("load_text"), e.toString());
            }
        }
    }


    /**
     * set Bitmap instance for text encoding
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        encodeCapacity = bitmap.getEncodeCapacity();
    }

    /**
     * calculate and show available encode capacity in bytes
     */
    public void onKeyReleased(KeyEvent keyEvent) {
        int length = textArea.getLength();
        encodeButton.setDisable(length == 0);
        statusLabel.setText(Util.getString("chars_left") + (encodeCapacity - (textArea.getText().getBytes().length)));
    }
}
