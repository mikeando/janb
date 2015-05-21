package janb;

import janb.controllers.Controller;
import janb.controllers.CreateProjectController;
import janb.controllers.StartController;
import janb.models.EntitySource;
import janb.models.Model;
import janb.project.SimpleANBProject;
import janb.ui.ProjectCreatePage;
import janb.ui.StartPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        System.err.printf("Main.start: Creating delegate...\n");
        StartPage.IStartPageDelegate delegate = new StartController(this, primaryStage);
        System.err.printf("Main.start: Building start page...\n");

        StartPage.build(delegate, primaryStage);
        System.err.printf("Main.start: Done in start\n");
    }

    public void loadProject(Stage primaryStage, Path targetDir) throws Exception {
        final URL rootFxmlUrl = getClass().getResource("../../../resources/main/sample.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(rootFxmlUrl);
        Parent root = fxmlLoader.load();

        Controller sampleController = fxmlLoader.getController();

        SimpleANBFile rootFile = new SimpleANBFile(targetDir);

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


    public ProjectCreatePage.IProjectCreatePageDelegate getCreateProjectDelegate() {
        return new CreateProjectController(this, primaryStage);
    }
}
