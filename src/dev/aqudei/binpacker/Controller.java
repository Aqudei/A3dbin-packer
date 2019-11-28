package dev.aqudei.binpacker;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class Controller implements Initializable {

    public Label inputFileLabel;

    public ProgressBar progressBar;
    public Label outputFolderLabel;
    public Button runButton;
    public Text statusText;
    private Stage stage;
    private File inputFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        runButton.disableProperty().bind(inputFileLabel.textProperty().isEqualTo("[INPUT FILE]"));
        statusText.setText("Ready");
    }

    private ContainerData readSheet(Sheet sheet) throws Exception {
        ContainerData data = new ContainerData();

        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            Iterator<Cell> cells = row.cellIterator();
            Part newPart = new Part();

            while (cells.hasNext()) {
                try {
                    Cell cell = cells.next();

                    if (cell.getAddress().toString() == "B2") {
                        double containerWidth = cell.getNumericCellValue();
                        data.setContainerWidth(containerWidth);
                    }

                    if (cell.getAddress().toString() == "C2") {
                        double containerHeight = cell.getNumericCellValue();
                        data.setContainerHeight(containerHeight);
                    }

                    if (cell.getRowIndex() >= 10) {
                        if (cell.getColumnIndex() == 0) {
                            newPart.setPartQuantity((int) cell.getNumericCellValue());
                        }
                        if (cell.getColumnIndex() == 1) {
                            newPart.setLength(cell.getNumericCellValue());
                        }
                        if (cell.getColumnIndex() == 2) {
                            newPart.setWidth(cell.getNumericCellValue());
                        }
                        if (cell.getColumnIndex() == 3) {
                            newPart.setHeight(cell.getNumericCellValue());
                        }
                    }
                } catch (Exception error) {
                    System.err.println(error.getMessage());
                }
            }

            data.getParts().add(newPart);
        }

        if (data.getContainerHeight() == 0) {
            throw new Exception("Unable to Determine Container Height!");
        }
        if (data.getContainerWidth() == 0) {
            throw new Exception("Unable to Determine Container Width!");
        }


        return data;
    }

    public void run() throws Exception {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        if (inputFile == null) {
            System.err.println("No File Selected!");
            return;
        }

        Workbook workbook = WorkbookFactory.create(inputFile);
        Sheet sheet = workbook.getSheetAt(0);
        ContainerData data = readSheet(sheet);
    }

    public void browse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx")
        );

        inputFile = fileChooser.showOpenDialog(stage);
        if (inputFile != null) {
            inputFileLabel.setText(inputFile.getAbsolutePath());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
