package janb.ui;

import janb.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by michaelanderson on 13/05/2015.
 */
public class ProjectCreatePage {


    @FXML
    public TextField name;

    @FXML
    public Label directoryName;

    @FXML
    ListView<String> projectTemplates;

    private Stage stage;
    public IProjectCreatePageDelegate delegate;

    File directory;

    public void chooseDirectory(ActionEvent actionEvent) {
        DirectoryChooser projectDirectoryChooser = new DirectoryChooser();
        projectDirectoryChooser.setTitle("Choose project directory");


        if(directory!=null) {
            projectDirectoryChooser.setInitialDirectory(directory);
        }
        File f = projectDirectoryChooser.showDialog(stage);

        if(f!=null) {
            directoryName.setText(f.getAbsolutePath());
            directory = f;
        }

        System.err.printf("ProjectCreatePage.newProject() file = %s\n", f);
        //TODO: Forward this to the delegate.
    }

    public void createProject(ActionEvent actionEvent) throws Exception {
        System.err.printf("ProjectCreatePage.createProject()\n");
        System.err.printf("NAME = '%s'\n", name.getText());
        System.err.printf("FILE = '%s'\n", directory);
        final ObservableList<String> templates = projectTemplates.getSelectionModel().getSelectedItems();
        System.err.printf("TEMPLATE = '%s'\n", templates);

        if(templates.size()!=1)
            return;
        if(directory==null)
            return;

        if( delegate.createProject(directory, name.getText(), templates.get(0)) ) {
            System.err.printf("ProjectCreatePage closing window");
        }
    }

    public  interface IProjectCreatePageDelegate {
        List<String> getTemplateNames();
        boolean createProject(File directory, String name, String template) throws Exception;
    }


    //TODO: This is almost identical to StartPage.build - and should be refactored
    public static void build(IProjectCreatePageDelegate delegate, Stage stage) throws IOException {

        final URL rootFxmlUrl = Main.class.getResource("../../../resources/main/project_create_page.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(rootFxmlUrl);
        Parent root = fxmlLoader.load();

        ProjectCreatePage page = fxmlLoader.getController();
        page.delegate = delegate;
        page.stage = stage;

        ObservableList<String> templateNames = FXCollections.observableArrayList(delegate.getTemplateNames());

        page.projectTemplates.setItems(templateNames);

        File projectDir = new File(System.getProperty("user.home"), "JANBProjects");
        if(!projectDir.exists()) {
            projectDir = new File(System.getProperty("user.home"));
        }

        page.directory = projectDir;
        page.directoryName.setText(projectDir.getAbsolutePath());


        stage.setScene(new Scene(root, 600, 300));
        stage.setTitle("Create Project");
        stage.show();
    }

}
