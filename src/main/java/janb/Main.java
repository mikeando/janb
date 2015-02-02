package janb;

import janb.controllers.Controller;
import janb.models.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        Yaml yaml = new Yaml();
        String document =
                "  a: 1\n"+
                        "  b:\n" +
                        "    c: 3\n" +
                        "    d: 4";

        //TODO: This is not a safe way to load yaml.
        final Object load = yaml.load(document);
        System.err.printf("Result %s %s\n", load, load.getClass());


        System.err.printf("This is a message\n");
        final URLClassLoader classLoader = (URLClassLoader)this.getClass().getClassLoader();
        System.err.printf("URLS are %s\n", classLoader.getURLs());
        System.err.printf("Class loader = %s (%s)\n", classLoader, classLoader.getClass());
        final URL resource = getClass().getResource("../../../resources/main/sample.fxml");
        System.err.printf("Resource url = %s\n", resource);
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Parent root = fxmlLoader.load();

        Model model = new Model();
        model.loadFromPath(new File("/Users/michaelanderson/JANBData"));

        Controller sampleController = fxmlLoader.getController();
        sampleController.model = model;

        sampleController.createTreeView();

        primaryStage.setScene(new Scene(root, 300, 550));
        primaryStage.setTitle("FXML Welcome");
        primaryStage.show();
    }
}
