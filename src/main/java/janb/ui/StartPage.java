package janb.ui;

import janb.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Created by michaelanderson on 12/05/2015.
 */
public class StartPage {
    
    public  interface IStartPageDelegate {

        void newProject(ActionEvent actionEvent) throws Exception;

        void openProject(ActionEvent actionEvent) throws Exception;
    }
    
    public IStartPageDelegate delegate;
    
    public static void build(IStartPageDelegate delegate, Stage stage) throws IOException {
        final URL rootFxmlUrl = Main.class.getResource("../../../resources/main/start_page.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(rootFxmlUrl);
        Parent root = fxmlLoader.load();

        StartPage page = fxmlLoader.getController();
        page.delegate = delegate;

        stage.setScene(new Scene(root, 300, 550));
        stage.setTitle("Authors Notebook");
        stage.show();
    }

    public void newProject(ActionEvent actionEvent) throws Exception {
        delegate.newProject(actionEvent);
    }

    public void openProject(ActionEvent actionEvent) throws Exception {
        delegate.openProject(actionEvent);
    }
}
