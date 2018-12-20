package Model;

import View.View;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.load(getClass().getResource("/View/SearchEngine.fxml").openStream());
        Parent root = loader.getRoot();
        View MainWindow = (View)loader.getController();
        Model model = new Model();

        ViewModel view_model = new ViewModel(model,MainWindow);
        MainWindow.setViewModel(view_model);

        primaryStage.setTitle("Search Engine");

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
