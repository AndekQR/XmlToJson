package view;

import controller.MainController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainView {

    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;

    private static final int CONTROL_BAR_HEIGHT = 50;

    private final MainController mainController;

    private TextArea errorArea;
    private TextArea jsonViewer;
    private TextArea xmlViewer;
    private BorderPane container;
    private Stage stage;

    private Boolean onLineJson = false;

    public MainView(MainController mainController) {
        this.mainController = mainController;
        this.initialize();
    }

    private void initialize() {
        this.container = new BorderPane();
        this.container.setPrefSize(WIDTH, HEIGHT);

        this.initComponents();


        Scene scene = new Scene(this.container);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("XmlToJson");
        stage.setScene(scene);
        this.stage = stage;

        stage.show();
    }

    private void initComponents() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            HBox hBox = new HBox();
            hBox.setFillHeight(true);
            this.jsonViewer = this.jsonViewComponent("");
            this.xmlViewer = this.xmlViewComponent("");
            this.jsonViewer.setEditable(false);
            hBox.getChildren().addAll(makeLabeledTextArea("XML file", xmlViewer), makeLabeledTextArea("Converted to JSON", jsonViewer));
            Platform.runLater(() -> {
                this.container.setCenter(hBox);
            });
        });

        Platform.runLater(() -> {
            this.container.setBottom(this.controlPanel());
        });

        executorService.shutdown();
    }

    private VBox makeLabeledTextArea(String label, TextArea area) {
        VBox vBox = new VBox();
        Label label1 = new Label(label);
        label1.setPrefWidth(WIDTH >> 1);
        label1.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(label1, area);
        return vBox;
    }

    private Button getStyledButton(String name) {
        Button button = new Button(name);
        button.setPrefHeight(CONTROL_BAR_HEIGHT);

        return button;
    }

    private HBox controlPanel() {
        HBox leftHbox = new HBox();

        Button chooseFileButton = this.getStyledButton("Load file");
        Button parseButton = this.getStyledButton("Parse to JSON");
        Button saveJsonFile = this.getStyledButton("Save JSON");

        chooseFileButton.setOnAction((ActionEvent event) -> {
            File file = showFileChooser("*.xml");
            this.mainController.setChoosedFile(file);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                long startTime = System.currentTimeMillis();
                this.jsonViewer.clear();
                this.errorArea.clear();
                String json = "";
                try {
                    try {
                        if (this.onLineJson) {
                            json = this.mainController.getJson(file.getAbsolutePath(), false);
                        } else {
                            json = this.mainController.getJson(file.getAbsolutePath(), true);
                        }
                    } catch (StackOverflowError error) {
                        this.errorArea.setText("Stack overflow error");
                        return;
                    }
                    String xml = this.mainController.getXmlString();
                    this.xmlViewer.setText(xml);
                    this.errorArea.setText(file.getAbsolutePath());
                    mainController.validateXml(file);
                } catch (RuntimeException | SAXException e) {
                    this.errorArea.setText(e.getLocalizedMessage());
                    return;
                }
                this.jsonViewer.setText(json);
                long stopTime = System.currentTimeMillis();
                this.errorArea.appendText("\nExecution time: " + (stopTime - startTime) + " ms");
            });
            executorService.shutdown();
        });

        parseButton.setOnAction((ActionEvent event) -> {
            String xmlText = this.xmlViewer.getText();
            if (!xmlText.isEmpty()) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(() -> {
                    this.mainController.setChoosedFile(null);
                    long start = System.currentTimeMillis();
                    this.jsonViewer.clear();
                    this.errorArea.clear();
                    String json = "";
                    if (this.onLineJson) {
                        json = this.mainController.convertXmlStringToJson(xmlText, false);
                    } else {
                        json = this.mainController.convertXmlStringToJson(xmlText, true);
                    }
                    try {
                        String xml = this.mainController.getXmlString();
                        this.mainController.validateXml(xml);
                    } catch (SAXException e) {
                        this.errorArea.setText(e.getLocalizedMessage());
                        return;
                    }
                    this.jsonViewer.setText(json);
                    long stop = System.currentTimeMillis();
                    this.errorArea.setText("Execution time: " + (stop - start) + " ms");
                });

                executorService.shutdown();
            }
        });

        saveJsonFile.setOnAction((event) -> {
            File folder = this.showFolderChooser();
            File choosedFile = this.mainController.getChoosedFile();
            File fileToWrite;
            if (choosedFile != null) {
                fileToWrite = new File(folder.getAbsolutePath() + "/" + this.mainController.getNameWithoutExtension(choosedFile) + ".json");
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd|HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                fileToWrite = new File(folder.getAbsolutePath() + "/" + dtf.format(now) + ".json");
            }
            try {
                FileWriter fileWriter = new FileWriter(fileToWrite);
                fileWriter.write(this.jsonViewer.getText());
                fileWriter.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        this.errorArea = new TextArea();
        errorArea.setEditable(false);
        this.errorArea.setPrefHeight(CONTROL_BAR_HEIGHT);
        this.errorArea.setPrefWidth(WIDTH >> 1);

        CheckBox oneLineJsonCheckbox = new CheckBox("One line JSON");
        oneLineJsonCheckbox.setSelected(false);
        oneLineJsonCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.onLineJson = newValue;
        });
        oneLineJsonCheckbox.setPadding(new Insets(0, 0, 0, 20));

        leftHbox.getChildren().addAll(chooseFileButton, parseButton, saveJsonFile, oneLineJsonCheckbox);
        leftHbox.setAlignment(Pos.BASELINE_LEFT);
        leftHbox.setPrefWidth(WIDTH >> 1);
        HBox rightHBox = new HBox();
        rightHBox.getChildren().add(errorArea);
        rightHBox.setAlignment(Pos.BASELINE_RIGHT);

        HBox parentHb = new HBox();
        parentHb.getChildren().addAll(leftHbox, rightHBox);
        parentHb.setAlignment(Pos.CENTER);
        return parentHb;
    }

    private File showFileChooser(String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", extensions));
        File file = fileChooser.showOpenDialog(this.stage);
        return file;
    }

    private File showFolderChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        return directoryChooser.showDialog(this.stage);
    }

    private TextArea jsonViewComponent(String json) {

        TextArea jsonTextArea = new TextArea();
        jsonTextArea.setPrefWidth(WIDTH >> 1);
        jsonTextArea.setPrefHeight(HEIGHT - CONTROL_BAR_HEIGHT);
        jsonTextArea.setMaxHeight(HEIGHT - CONTROL_BAR_HEIGHT);
        jsonTextArea.setText(json);


        return jsonTextArea;
    }

    private TextArea xmlViewComponent(String formattedXml) {

        TextArea textArea = new TextArea();
        textArea.setPrefWidth(WIDTH >> 1);
        textArea.setPrefHeight(HEIGHT - CONTROL_BAR_HEIGHT);
        textArea.setMaxHeight(HEIGHT - CONTROL_BAR_HEIGHT);
        textArea.setText(formattedXml);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(textArea);
        scrollPane.setFitToWidth(false);

        return textArea;

    }
}
