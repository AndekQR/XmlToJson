import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/*
* Program nie wykrywa przestrzeni nazw w XML
* */
public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        ViewManager viewManager = new ViewManager();
        viewManager.showMainPanel();
    }

}