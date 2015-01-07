package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();

        Model model = new Model();

        Controller sampleController = (Controller) fxmlLoader.getController();
        sampleController.model = model;

        sampleController.createTreeView();

        primaryStage.setScene(new Scene(root, 300, 550));
        primaryStage.setTitle("FXML Welcome");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
