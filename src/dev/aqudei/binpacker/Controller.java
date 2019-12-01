package dev.aqudei.binpacker;

import com.github.skjolber.packing.*;
import com.github.skjolber.packing.Container;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Controller implements Initializable {

    public Label inputFileLabel;

    public ProgressBar progressBar;
    public Label outputFolderLabel;
    public Button runButton;
    public Text statusText;
    public TextField inputMultiplierTextField;

    private Stage stage;
    private File inputFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        runButton.disableProperty().bind(inputFileLabel.textProperty().isEqualTo("[INPUT FILE]").or(outputFolderLabel.textProperty().isEqualTo("[OUTPUT FOLDER]")));
        statusText.setText("Ready");
    }

    private int getMultiplier() {
        try {
            return Integer.parseInt(inputMultiplierTextField.getText());
        } catch (Exception e) {
            return 10;
        }
    }

    private Part parseRow(Row row) {
        try {
            Part newPart = new Part();
            newPart.setPartName("" + ((int) row.getCell(0).getNumericCellValue()));
            newPart.setPartQuantity((int) row.getCell(1).getNumericCellValue());
            newPart.setLength((int) (row.getCell(2).getNumericCellValue() * getMultiplier()));
            newPart.setWidth((int) (row.getCell(3).getNumericCellValue() * getMultiplier()));
            newPart.setHeight((int) (row.getCell(4).getNumericCellValue() * getMultiplier()));
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
                    data.setContainerLength((int) (row.getCell(1).getNumericCellValue() * getMultiplier()));
                    data.setContainerWidth((int) (row.getCell(2).getNumericCellValue() * getMultiplier()));
                    data.setContainerHeight((int) (row.getCell(3).getNumericCellValue() * getMultiplier()));
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

    public void run() {

        ContainerData data = null;

        try {

            statusText.setText("Now calculations...\n");
            // Creating a Workbook from an Excel file (.xls or .xlsx)
            if (inputFile == null) {
                System.err.println("No File Selected!");
                return;
            }

            try (Workbook workbook = WorkbookFactory.create(inputFile)) {
                Sheet sheet = workbook.getSheetAt(0);
                data = readSheet(sheet);
            }

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
                stringBuilder.append("Input File: ").append(inputFile.getAbsolutePath()).append("\n");
                stringBuilder.append(String.format("Container Depth = %.2f\n", (float) match.getDepth() / getMultiplier()));
                stringBuilder.append(String.format("Container Width = %.2f\n", (float) match.getWidth() / getMultiplier()));
                stringBuilder.append(String.format("Container Height = %.2f\n", (float) match.getHeight() / getMultiplier()));
                stringBuilder.append("Number of Levels = ").append(match.getLevels().size()).append("\n");
                stringBuilder.append(String.format("Used Space: Width = %.2f\n", (float) match.getUsedSpace().getWidth() / getMultiplier()));
                stringBuilder.append(String.format("Used Space: Depth/Length = %.2f\n", (float) match.getUsedSpace().getDepth() / getMultiplier()));
                stringBuilder.append(String.format("Used Space: Height = %.2f\n", (float) match.getUsedSpace().getHeight() / getMultiplier()));

                stringBuilder.append("Placements: @ (x, y, z)\n");
                for (int i = 0; i < match.getLevels().size(); i++) {
                    Level level = match.getLevels().get(i);
                    for (int j = 0; j < level.size(); j++) {
                        Placement placement = level.get(j);
                        stringBuilder.append(String.format("\tPlacement of Box '%s' is @ (%.2f, %.2f, %.2f)\n",
                                placement.getBox().getName(), (float) placement.getSpace().getX() / getMultiplier(),
                                (float) placement.getSpace().getY() / getMultiplier(), (float) placement.getSpace().getZ() / getMultiplier()));
                    }
                }

                String outputFile = Paths.get(outputFolderLabel.getText(), "results-" + inputFile.getName() + "-"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-M-yyyy hhmm")) + ".txt").toString();
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
                bufferedWriter.write(stringBuilder.toString());
                bufferedWriter.close();

                Desktop.getDesktop().open(new File(outputFolderLabel.getText()));
                statusText.setText(String.format("Success, results were written into file: %s", outputFile) + "\n");
            } else {
                statusText.setText("No Match, items cannot be packed into the container" + "\n");
            }
        } catch (Exception e) {
            statusText.setText(e.getMessage() + "\n");
        }
    }

    public void browse() {
        FileChooser fileChooser = new FileChooser();
        //fileChooser.getExtensionFilters().addAll(
        //        new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx")
        //);

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
