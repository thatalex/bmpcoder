package by.spalex.bmp.ui.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Parent controller of all views
 */
abstract class Controller {
    /**
     * close current view
     */
    public void close(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
