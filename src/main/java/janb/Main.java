package janb;

import janb.controllers.Controller;
import janb.models.EntitySource;
import janb.models.Model;
import janb.util.ANBFile;
import janb.util.ANBFileSystem;
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
        SimpleANBFileSystem fs;

        public SimpleANBFile(Path p, SimpleANBFileSystem fs) {
            this.p=p;
            this.fs=fs;
        }

        @Override
        public List<String> relative_path(ANBFile root) {
            Path rootPath = ((SimpleANBFile)root).p;
            final Path relativePath = rootPath.relativize(p);
            //TODO: This is a pretty bad way to do this
            return Arrays.asList(relativePath.toString().split("/"));
        }

        @Override
        public ANBFileSystem getFS() {
            return fs;
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
            return new SimpleANBFile(p.resolve(name),fs);
        }

        @Override
        public String pathAsString() {
            return p.toString();
        }
    }

    public static class SimpleANBFileSystem implements ANBFileSystem {

        @Override
        public List<ANBFile> getAllFiles(ANBFile file) {
            SimpleANBFile sanbfile = (SimpleANBFile)file;
            File[] files = sanbfile.p.toFile().listFiles();
            List<ANBFile> result = new ArrayList<>();
            for(File f:files) {
                result.add(new SimpleANBFile(f.toPath(),this));
            }
            return result;
        }

        @Override
        public ANBFile getFileForString(String s) {
            return new SimpleANBFile(Paths.get(s), this);
        }

        @Override
        public byte[] readFileContents(ANBFile file) throws IOException {
            SimpleANBFile sanbfile = (SimpleANBFile)file;
            return Files.readAllBytes(sanbfile.p);
        }

        @Override
        public void writeFileContents(ANBFile file, byte[] data) throws IOException {
            SimpleANBFile sanbfile = (SimpleANBFile)file;
            Files.write(sanbfile.p,data);
        }

        @Override
        public ANBFile makePaths(ANBFile directory, List<String> components) throws IOException {
            SimpleANBFile sanbdirectory = (SimpleANBFile)directory;

            //TODO: Gotta be a nicer way to do this;
            Path p = sanbdirectory.p;
            for(String s:components) {
                p = p.resolve(s);
            }
            Files.createDirectories(p);
            return new SimpleANBFile(p,this);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        final URL rootFxmlUrl = getClass().getResource("../../../resources/main/sample.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(rootFxmlUrl);
        Parent root = fxmlLoader.load();

        Controller sampleController = fxmlLoader.getController();
        ANBFileSystem fs = new SimpleANBFileSystem();
        final File sourceLocation = new File("/Users/michaelanderson/JANBData/entities");


        EntitySource entitySource = new EntitySource(fs);
        entitySource.addRoot(sourceLocation.getAbsolutePath());


        Model model = new Model(entitySource);

        //TODO: We shouldn't use this now.. just use the entitySource instead.
        model.loadFromPath(sourceLocation, sampleController.getViewModel());

        sampleController.model = model;

        sampleController.createTreeView();

        primaryStage.setScene(new Scene(root, 300, 550));
        primaryStage.setTitle("FXML Welcome");
        primaryStage.show();
    }
}
