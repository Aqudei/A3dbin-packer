package dev.aqudei.binpacker;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.net.URL;
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

    private Part parseRow(Row row) {
        try {
            Iterator<Cell> cells = row.cellIterator();
            Part newPart = new Part();
            newPart.setPartQuantity((int) row.getCell(0).getNumericCellValue());
            newPart.setLength(row.getCell(1).getNumericCellValue());
            newPart.setWidth(row.getCell(2).getNumericCellValue());
            newPart.setHeight(row.getCell(3).getNumericCellValue());
            return newPart;
        } catch (Exception e) {
            return null;
        }
    }


    private ContainerData readSheet(Sheet sheet) throws Exception {
        ContainerData data = new ContainerData();
        Iterator<Row> rows = sheet.rowIterator();
        int rowCount = 0;
        while (rows.hasNext()) {
            Row row = rows.next();

            if (rowCount == 1) {
                try {
                    data.setContainerWidth(row.getCell(1).getNumericCellValue());
                    data.setContainerHeight(row.getCell(2).getNumericCellValue());
                    rowCount++;
                    continue;
                } catch (Exception e) {
                    System.err.println("Unable to fetch Container Dimension!");
                    return null;
                }
            }

            Part part = parseRow(row);
            if (part == null) {
                continue;
            }

            data.getParts().add(part);
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
