package janb.controllers;

import janb.Main;
import janb.ui.ProjectCreatePage;
import janb.ui.StartPage;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.nio.file.Paths;

/**
 * Created by michaelanderson on 11/05/2015.
 */
public class StartController implements StartPage.IStartPageDelegate {

    //TODO: The passing of main is a little ugly - should pass something else?
    private final Main main;
    private final Stage stage;

    public StartController(Main main, Stage stage) {
        this.main = main;
        this.stage = stage;
    }

    public void newProject(ActionEvent actionEvent) throws Exception {
        ProjectCreatePage.build(main.getCreateProjectDelegate(), stage);
    }

    public void openProject(ActionEvent actionEvent) throws Exception {
        System.err.printf("StartController.newProject()\n");
        //TODO: Should use a better value than this!
        main.loadProject(stage, Paths.get("/Users/michaelanderson/JANBData"));
    }


}
