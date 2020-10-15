package view;

import controller.MainController;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainView {

    private static final int HEIGHT=768;
    private static final int WIDTH=1024;

    private final MainController mainController;
    private BorderPane container;

    public MainView(MainController mainController) {
        this.mainController = mainController;
        this.initialize();
    }

    private void initialize() {
        this.container = new BorderPane();
        this.container.setPrefSize(WIDTH, HEIGHT);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Label jsonLabel = new Label(this.mainController.getRootNode("example.xml").getJsonString());
        this.container.setCenter(l);
        this.container.setTop(jsonLabel);

        Scene scene=new Scene(this.container);
        Stage stage=new Stage(StageStyle.DECORATED);
        stage.setTitle("XmlToJson");
        stage.setScene(scene);

        stage.show();
    }
}
