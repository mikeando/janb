package janb;

import janb.controllers.Controller;
import janb.models.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{




        final URL rootFxmlUrl = getClass().getResource("../../../resources/main/sample.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(rootFxmlUrl);
        Parent root = fxmlLoader.load();

        Controller sampleController = fxmlLoader.getController();

        Model model = new Model();
        model.loadFromPath(new File("/Users/michaelanderson/JANBData"), sampleController.getViewModel());

        sampleController.model = model;

        sampleController.createTreeView();

        primaryStage.setScene(new Scene(root, 300, 550));
        primaryStage.setTitle("FXML Welcome");
        primaryStage.show();
    }
}
