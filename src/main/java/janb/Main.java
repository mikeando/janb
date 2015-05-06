package janb;

import janb.controllers.Controller;
import janb.models.EntitySource;
import janb.models.Model;
import janb.project.SimpleANBProject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Paths;

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

        SimpleANBFile rootFile = new SimpleANBFile(Paths.get("/Users/michaelanderson/JANBData"));

        SimpleANBProject project = new SimpleANBProject(rootFile);

        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        Model model = new Model(entitySource,sampleController.getViewModel());

        sampleController.model = model;

        sampleController.createTreeView();

        primaryStage.setScene(new Scene(root, 300, 550));
        primaryStage.setTitle("FXML Welcome");
        primaryStage.show();
    }


}
