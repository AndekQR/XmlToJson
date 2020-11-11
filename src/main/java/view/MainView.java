package view;

import controller.MainController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainView {

    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;

    private static final int CONTROL_BAR_HEIGHT = 50;

    private final MainController mainController;

    private TextArea jsonViewer;
    private TextArea xmlViewer;
    private BorderPane container;
    private Stage stage;

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
            hBox.getChildren().addAll(xmlViewer, jsonViewer);
            Platform.runLater(() -> {
                this.container.setCenter(hBox);
            });
        });

        Platform.runLater(() -> {
            this.container.setBottom(this.controlPanel());
        });

        executorService.shutdown();
    }

    private Button getStyledButton(String name) {
        Button button = new Button(name);
        button.setPrefHeight(CONTROL_BAR_HEIGHT);

        return button;
    }

    private HBox controlPanel() {
        HBox hBox = new HBox();

        Button chooseFileButton = this.getStyledButton("Load file");
        Button parseButton = this.getStyledButton("Parse to JSON");
        Button saveJsonFile = this.getStyledButton("Save JSON");

        chooseFileButton.setOnAction((ActionEvent event) -> {
            File file = showFileChooser("*.xml");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                String json = this.mainController.getJson(file.getAbsolutePath(), true);
                String xml = this.mainController.getFormattedXml();
                this.jsonViewer.setText(json);
                this.xmlViewer.setText(xml);
            });
            executorService.shutdown();
        });

        parseButton.setOnAction((ActionEvent event) -> {
            String xmlText = this.xmlViewer.getText();
            if (!xmlText.isEmpty()) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();

                String json = this.mainController.convertXmlStringToJson(xmlText, true);
                this.jsonViewer.setText(json);

                executorService.shutdown();
            }
        });

        saveJsonFile.setOnAction((event) -> {
            File file = this.showFileChooser("*.txt", "*.json");
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(this.jsonViewer.getText());
                fileWriter.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        hBox.getChildren().addAll(chooseFileButton, parseButton, saveJsonFile);
        return hBox;
    }

    private File showFileChooser(String ...extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", extensions));
        File file = fileChooser.showOpenDialog(this.stage);
        return file;
    }

    private TextArea jsonViewComponent(String json) {

        TextArea jsonTextArea = new TextArea();
        jsonTextArea.setPrefWidth(WIDTH >> 1);
        jsonTextArea.setPrefHeight(HEIGHT - CONTROL_BAR_HEIGHT);
        jsonTextArea.setMaxHeight(HEIGHT - CONTROL_BAR_HEIGHT);
        jsonTextArea.setText(json);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(jsonTextArea);
        scrollPane.setFitToWidth(false);

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
