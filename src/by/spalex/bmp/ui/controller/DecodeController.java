package by.spalex.bmp.ui.controller;

import by.spalex.bmp.ui.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Controller for text decoding view
 */
public class DecodeController extends Controller {

    @FXML
    private TextArea textArea;

    /**
     * set text at view's TextArea
     */
    public void setText(String text) {
        textArea.setText(text);
    }

    /**
     * Saves decoded text to file
     */
    public void save(ActionEvent actionEvent) {
        Window window = ((Node) actionEvent.getSource()).getScene().getWindow();
        File file = Util.getFileChooser(Util.getString("save.decoded.text"), "text", "*.txt").showSaveDialog(window);
        if (file != null) {
            String text = textArea.getText();
            try {
                Files.write(file.toPath(), text.getBytes(), StandardOpenOption.CREATE);
            } catch (IOException e) {
                Util.showError(Util.getString("save.decoded.text"), e.toString());
            }
        }
    }
}
