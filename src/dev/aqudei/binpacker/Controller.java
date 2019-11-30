package dev.aqudei.binpacker;

import com.github.skjolber.packing.*;
import com.github.skjolber.packing.Container;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

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
        runButton.disableProperty().bind(inputFileLabel.textProperty().isEqualTo("[INPUT FILE]").or(outputFolderLabel.textProperty().isEqualTo("[OUTPUT FOLDER]")));
        statusText.setText("Ready");
    }

    private Part parseRow(Row row) {
        try {
            Part newPart = new Part();
            newPart.setPartName(row.getCell(0).getStringCellValue());
            newPart.setPartQuantity((int) row.getCell(1).getNumericCellValue());
            newPart.setLength((int) row.getCell(2).getNumericCellValue());
            newPart.setWidth((int) row.getCell(3).getNumericCellValue());
            newPart.setHeight((int) row.getCell(4).getNumericCellValue());
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
                    data.setContainerLength((int) row.getCell(0).getNumericCellValue());
                    data.setContainerWidth((int) row.getCell(1).getNumericCellValue());
                    data.setContainerHeight((int) row.getCell(2).getNumericCellValue());
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
        containers.add(new Container((int) data.getContainerWidth(), (int) data.getContainerLength(),
                (int) data.getContainerHeight(), 0)); // x y z and weight
        Packager packager = new LargestAreaFitFirstPackager(containers);
        List<BoxItem> products = new ArrayList<BoxItem>();

        for (int i = 0; i < data.getParts().size(); i++) {
            Part part = data.getParts().get(i);
            products.add(new BoxItem(new Box(part.getPartName(), (int) part.getWidth(),
                    (int) part.getLength(), (int) part.getHeight(), 0), part.getPartQuantity()));
        }

        Container match = packager.pack(products);

        if (match != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Input File: " + inputFile.getAbsolutePath() + "\n");
            stringBuilder.append("Container Depth = " + match.getDepth() + "\n");
            stringBuilder.append("Container Width = " + match.getWidth() + "\n");
            stringBuilder.append("Container Height = " + match.getHeight() + "\n");
            stringBuilder.append("Placements: @ (x, y, z)\n");
            for (int i = 0; i < match.getLevels().size(); i++) {
                Level level = match.getLevels().get(i);
                for (int j = 0; j < level.size(); j++) {
                    Placement placement = level.get(j);
                    stringBuilder.append(String.format("\tPlacement of Box '%s' is @ (%d, %d, %d)\n",
                            placement.getBox().getName(), placement.getSpace().getX(), placement.getSpace().getY(), placement.getSpace().getZ()));
                }
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(String.valueOf(Paths.get(outputFolderLabel.getText(), "results.txt"))));
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.close();

            Desktop.getDesktop().open(new File(outputFolderLabel.getText()));
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

    public void browseOutput(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(stage);
        if (directory != null) {
            outputFolderLabel.setText(directory.getAbsolutePath());
        }
    }
}
