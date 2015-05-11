package janb.controllers;

import janb.Main;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

/**
 * Created by michaelanderson on 11/05/2015.
 */
public class StartController {

    private Main main;
    private Stage stage;

    public void setMain(Main main, Stage stage) {
        this.main = main;
        this.stage = stage;
    }

    public void newProject(ActionEvent actionEvent) throws Exception {
        System.err.printf("StartController.newProject()\n");
    }

    public void openProject(ActionEvent actionEvent) throws Exception {
        System.err.printf("StartController.newProject()\n");
        main.loadProject(stage);
    }
}
