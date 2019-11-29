package dev.aqudei.binpacker;

import com.github.skjolber.packing.*;
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
            newPart.setPartName(row.getCell(0).getStringCellValue());
            newPart.setPartQuantity((int) row.getCell(1).getNumericCellValue());
            newPart.setLength(row.getCell(2).getNumericCellValue());
            newPart.setWidth(row.getCell(3).getNumericCellValue());
            newPart.setHeight(row.getCell(4).getNumericCellValue());
            newPart.setWeight(row.getCell(5).getNumericCellValue());
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
                    data.setContainerLength(row.getCell(0).getNumericCellValue());
                    data.setContainerWidth(row.getCell(1).getNumericCellValue());
                    data.setContainerHeight(row.getCell(2).getNumericCellValue());
                    data.setContainerWeight(row.getCell(3).getNumericCellValue());
                    rowCount++;
                    continue;
                } catch (Exception e) {
                    System.err.println("Unable to fetch Container Dimension!");
                    return null;
                }
            }

            if (rowCount < 1) {
                rowCount++;
                continue;
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
        if (data == null)
            return;

        // initialization
        List<Container> containers = new ArrayList<Container>();
        Packager packager = new LargestAreaFitFirstPackager(containers);
        containers.add(new Container((int) data.getContainerWidth(), (int) data.getContainerLength(),
                (int) data.getContainerHeight(), (int) data.getContainerWeight())); // x y z and weight
        List<BoxItem> products = new ArrayList<BoxItem>();

        for (int i = 0; i < data.getParts().size(); i++) {
            Part part = data.getParts().get(i);
            products.add(new BoxItem(new Box(part.getPartName(), (int)part.getWidth(),
                    (int)part.getLength(), (int)part.getHeight(), (int)part.getWeight()), part.getPartQuantity()));
        }

        Container match = packager.pack(products);



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
