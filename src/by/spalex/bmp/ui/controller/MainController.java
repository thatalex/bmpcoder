package by.spalex.bmp.ui.controller;

import by.spalex.bmp.bitmap.Bitmap;
import by.spalex.bmp.coder.Decoder;
import by.spalex.bmp.ui.Main;
import by.spalex.bmp.ui.Util;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.sanselan.Sanselan;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Controller for main view
 */
public class MainController extends Controller {

    @FXML
    private Button encodeButton;
    @FXML
    private Button decodeButton;
    @FXML
    private Button compareButton;

    @FXML
    private Canvas canvas;

    private final InvalidationListener listener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            if (image != null) {
                paint(image);
            }
        }
    };
    private Image image = null;
    private Bitmap bitmap;
    @FXML
    private Label statusLabel;
    private String fileName;

    /**
     * load bitmap image and show it in main view
     */
    @FXML
    void openImage(ActionEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        File imageFile = Util.getFileChooser(Util.getString("open_image"), "bitmap", "*.bmp").showOpenDialog(window);
        if (imageFile != null && imageFile.exists()) {
            try {
                bitmap = new Bitmap(Files.readAllBytes(imageFile.toPath()));
                fileName = imageFile.getName();
                image = SwingFXUtils.toFXImage(Sanselan.getBufferedImage(imageFile), null);
                paint(image);
                long capacity = bitmap.getEncodeCapacity();
                statusLabel.setText(Util.getString("file") + imageFile.getName() +
                        Util.getString("encode_capacity") + capacity + Util.getString("bytes"));
                encodeButton.setDisable(capacity == 0);
                compareButton.setDisable(false);
            } catch (Exception e) {
                Util.showError(Util.getString("open_image"), e.toString());
                encodeButton.setDisable(true);
                compareButton.setDisable(true);
            }
            String text = null;
            if (bitmap != null) {
                try {
                    Decoder decoder = new Decoder(bitmap);
                    text = decoder.decode();
                } catch (IllegalStateException ignored) {
                }
            }
            decodeButton.setDisable(text == null || text.isEmpty());
            if (text != null) {
                decodeButton.setUserData(text);
            }
        }
    }

    private void paint(Image image) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    void initialize() {
        canvas.widthProperty().addListener(listener);
        canvas.heightProperty().addListener(listener);
    }

    private void createStage(String encode, Window primaryStage, Parent root) {
        Stage stage = new Stage();
        stage.setTitle(encode);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    /**
     * open encode view
     */
    public void onEncode(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        Window primaryStage = button.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("encodePane.fxml"), Util.lang);

        try {
            Parent root = loader.load();
            EncodeController controller = loader.getController();
            controller.setBitmap(bitmap);
            createStage(Util.getString("encode"), primaryStage, root);
        } catch (IOException e) {
            Util.showError(Util.getString("encode"), e.toString());
        }
    }

    /**
     * open decode view
     */
    public void onDecode(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        Window primaryStage = button.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("decodePane.fxml"), Util.lang);
        try {
            Parent root = loader.load();
            DecodeController controller = loader.getController();
            String text = (String) decodeButton.getUserData();
            controller.setText(text);
            createStage(Util.getString("decode"), primaryStage, root);
        } catch (Exception e) {
            Util.showError(Util.getString("decode"), e.toString());
        }

    }

    /**
     * open comparison view
     */
    public void compareImage(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        Window primaryStage = button.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("comparePane.fxml"), Util.lang);

        try {
            Parent root = loader.load();
            CompareController controller = loader.getController();
            controller.setReferenced(bitmap.getBytes(), fileName);
            createStage(Util.getString("comparing"), primaryStage, root);
        } catch (IOException e) {
            Util.showError(Util.getString("comparing"), e.toString());
        }
    }
}
