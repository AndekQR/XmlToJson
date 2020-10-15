import controller.MainController;
import view.MainView;

public class ViewManager {

    public void showMainPanel() {
        MainController controller = new MainController();
        new MainView(controller);
    }
}
