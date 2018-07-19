package by.spalex.bmp.ui.controller;

import by.spalex.bmp.ui.Util;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

/**
 * Controller for file comparison view
 */
public class CompareController extends Controller {

    private static final int BYTE_PER_ROW = 16;
    private static final int COLUMN_WIDTH = 24;
    private static final String[] COLORS = new String[]{"#CC7777", "#77CC77", "#7777CC", "#CCCC77", "#77CCCC", "#CC77CC",
            "#C0C0C0", "#CC7777", "#77CC77", "#7777CC", "#CCCC77", "#77CCCC", "#CC77CC", "#C0C0C0", "#CC7777", "#77CC77"};

    // byte array of first compared file
    private byte[] referenced = null;
    @FXML
    private TableView<HexItem> view;
    @FXML
    private TableColumn columnAddress;
    // header for first compared file ()
    @FXML
    private Label header1;
    // header for second compared file
    @FXML
    private Label header2;

    // name of first compared file
    private String referencedName;

    /**
     * set first compared file
     * @param referenced file as byte array
     * @param referencedName name of file
     */
    public void setReferenced(byte[] referenced, String referencedName) {
        this.referenced = referenced;
        this.referencedName = referencedName;
    }

    /**
     * performs loading second image for comparing
     */
    public void loadImage(ActionEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        File file = Util.getFileChooser(Util.getString("open_image"), "bitmap", "*.bmp").showOpenDialog(window);
        byte[] compared;
        view.getItems().clear();
        if (file != null && file.exists()) {
            try {
                compared = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                Util.showError(Util.getString("open_image"), e.toString());
                return;
            }
            int size = Math.max(referenced.length, compared.length);

            columnAddress.setText(Util.getString("address"));
            header1.setText(referencedName);
            header2.setText(file.getName());
            createColumns(true);
            TableColumn<HexItem, HexItem> tableColumn = new TableColumn<>();
            tableColumn.setText("<=>");
            tableColumn.setPrefWidth(25);
            view.getColumns().add(tableColumn);
            createColumns(false);
            for (int i = 0; i < size; i = i + BYTE_PER_ROW) {
                HexItem hexItem = new HexItem();
                int rowSize = Math.min(BYTE_PER_ROW, referenced.length - i);
                if (rowSize < 0) rowSize = 0;
                hexItem.referencedRow = new byte[rowSize];
                if (i < referenced.length) {
                    System.arraycopy(referenced, i, hexItem.referencedRow, 0, hexItem.referencedRow.length);
                }
                rowSize = Math.min(BYTE_PER_ROW, compared.length - i);
                if (rowSize < 0) rowSize = 0;
                hexItem.comparedRow = new byte[rowSize];
                if (i < compared.length) {
                    System.arraycopy(compared, i, hexItem.comparedRow, 0, hexItem.comparedRow.length);
                }
                hexItem.address = ByteBuffer.allocate(4).putInt((i * BYTE_PER_ROW)).array();
                view.getItems().add(hexItem);
            }
        }
    }

    /**
     * create table columns
     * @param isReferenced is columns for first compared file
     */
    private void createColumns(boolean isReferenced) {
        for (int i = 0; i < BYTE_PER_ROW; i++) {
            TableColumn<HexItem, HexItem> tableColumn = new TableColumn<>();
            tableColumn.setText("0" + Util.hexArray[i]);
            tableColumn.setPrefWidth(COLUMN_WIDTH);
            tableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue()));
            tableColumn.setCellFactory(getCellCallback(isReferenced, i));
            view.getColumns().add(tableColumn);
        }
    }

    /**
     * create callback for cell factory showing compared file's bytes
     * @param isReferenced is columns for first compared file
     * @param index column index
     * @return callback for cell factory
     */
    private Callback<TableColumn<HexItem, HexItem>, TableCell<HexItem, HexItem>> getCellCallback(boolean isReferenced, int index) {
        return new Callback<TableColumn<HexItem, HexItem>, TableCell<HexItem, HexItem>>() {
            @Override
            public TableCell<HexItem, HexItem> call(TableColumn<HexItem, HexItem> param) {
                return new TableCell<HexItem, HexItem>() {
                    @Override
                    protected void updateItem(HexItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            return;
                        }
                        byte[] bytes = isReferenced ? item.referencedRow : item.comparedRow;
                        if (index < bytes.length) {
                            setText(Util.hexToString(isReferenced ? item.referencedRow[index] : item.comparedRow[index]));
                        } else {
                            setText("");
                        }
                        if (item.referencedRow.length == item.comparedRow.length
                                && (index >= bytes.length || item.referencedRow[index] == item.comparedRow[index])) {
                            setStyle("");
                        } else {
                            setStyle("-fx-background-color: " + COLORS[index]);

                        }
                    }
                };
            }
        };
    }

    /**
     * auxiliary class representing table row
     */
    private static class HexItem {
        private byte[] referencedRow;
        private byte[] comparedRow;
        private byte[] address;


        public String getAddress() {
            return Util.hexToString(address);
        }

        public void setAddress(byte[] address) {
            this.address = address;
        }
    }
}
