package dev.aqudei.binpacker;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Label inputFileLabel;

    public ProgressBar progressBar;
    private Stage stage;
    private File inputFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void run() throws IOException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        if(inputFile==null)
        {
            System.err.println("No File Selected!");
            return;
        }

        Workbook workbook = WorkbookFactory.create(inputFile);

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()){
            Row row = rows.next();
            Iterator<Cell> cells = row.cellIterator();
            while (cells.hasNext()){
                Cell cell = cells.next();
            }
        }
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
