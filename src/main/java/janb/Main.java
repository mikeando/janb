package janb;

import janb.controllers.Controller;
import janb.models.EntitySource;
import janb.models.Model;
import janb.project.SimpleANBProject;
import janb.util.ANBFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static class SimpleANBFile implements ANBFile {

        Path p;

        public SimpleANBFile(Path p) {
            this.p=p;
        }

        @Override
        public List<String> relative_path(ANBFile root) {
            Path rootPath = ((SimpleANBFile)root).p;
            final Path relativePath = rootPath.relativize(p);
            //TODO: This is a pretty bad way to do this
            return Arrays.asList(relativePath.toString().split("/"));
        }

        @Override
        public List<ANBFile> getAllFiles() {

            final File file = p.toFile();
            List<ANBFile> result = new ArrayList<>();

            if(!file.exists())
                return result;

            File[] files = file.listFiles();
            if(files==null)
                return result;

            for(File f:files) {
                result.add(new SimpleANBFile(f.toPath()));
            }
            return result;
        }

        @Override
        public boolean isDirectory() {
            return p.toFile().isDirectory();
        }

        @Override
        public boolean isWritable() {
            return p.toFile().canWrite();
        }

        @Override
        public ANBFile child(String name) {
            return new SimpleANBFile(p.resolve(name));
        }

        @Override
        public String pathAsString() {
            return p.toString();
        }

        @Override
        public String getName() {
            if(p==null || p.getNameCount()==0)
                return "";
            return p.getName(p.getNameCount()-1).toString();
        }

        @Override
        public byte[] readContents() throws IOException {
            return Files.readAllBytes(p);
        }

        @Override
        public boolean exists() {
            return p.toFile().exists();
        }

        @Override
        public boolean hasExtension(String s) {
            return p.toString().endsWith(s);
        }

        @Override
        public ANBFile withoutExtension(String s) {
            if(!hasExtension(s)) {
                throw new RuntimeException("File does not have extension '"+s+"'");
            }
            final String pathString = p.toString();
            pathString.substring(0, pathString.length()-s.length());
            return new SimpleANBFile(Paths.get(pathString));
        }

        @Override
        public ANBFile withExtension(String s) {
            String pathString=p.toString()+"."+s;
            return new SimpleANBFile(Paths.get(pathString));
        }

        @Override
        public ANBFile createSubdirectory(String s) {
            throw new RuntimeException("NYI");
        }

        @Override
        public void createFile(String s, byte[] rawData) {
            throw new RuntimeException("NYI");
        }

        @Override
        public String toString() {
            return "SimpleANBFile{" +
                    "p=" + p +
                    '}';
        }
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
