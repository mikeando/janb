package janb.controllers;

import janb.Main;
import janb.ui.ProjectCreatePage;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michaelanderson on 13/05/2015.
 */
public class CreateProjectController implements ProjectCreatePage.IProjectCreatePageDelegate {
    private final Main main;
    private final Stage stage;

    //TODO: Move this to its own file...
    //TODO: Make this actually get more data...
    public static class ProjectMetadata {

        private final File file;

        public ProjectMetadata(File f) {

            file = f;
        }

        public static ProjectMetadata loadFromProject(File f) {
            return new ProjectMetadata(f);
        }

        public String getName() {
            return file.getName();
        }
    }

    private final ArrayList<ProjectMetadata> templateMetadata = new ArrayList<>();



    public CreateProjectController(Main main, Stage stage) {
        this.main = main;
        this.stage = stage;
        loadTemplateMetadata();
    }

    public Path getTemplateDirectory() {
        //TODO: Get this from somewhere more useful
        return Paths.get("/Users/michaelanderson/JANBTemplates");
    }

    public void loadTemplateMetadata() {
        Path templatesDirectory = getTemplateDirectory();
        templateMetadata.clear();
        for(File f : templatesDirectory.toFile().listFiles()) {
            final ProjectMetadata projectMetadata = ProjectMetadata.loadFromProject(f);
            templateMetadata.add(projectMetadata);
        }
    }

    @Override
    public List<String> getTemplateNames() {
        Path templatesDirectory = getTemplateDirectory();
        //TODO: Get the children.
        //Loop over the children
        //Get their metadata and add it to a list
        //return the list mapped to names.
        for(File f : templatesDirectory.toFile().listFiles()) {
            System.err.printf("template %s\n", f);
        }

        return templateMetadata.stream()
                .map( ProjectMetadata::getName )
                .collect(Collectors.toList());
    }

    @Override
    public boolean createProject(File directory, String name, String templateName) throws Exception {
        System.err.printf("Creating project ... %s %s %s\n", directory, name, templateName);

        final Path targetDir = directory.toPath().resolve(name);
        if(targetDir.toFile().exists())
            return false;

        Path templatesDirectory = getTemplateDirectory();

        //TODO: Get the name from the selected template
        final Path template = templatesDirectory.resolve("tutorial");

        // Recursive copy - extract me?
        SimpleFileVisitor<Path> copyVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                FileVisitResult result = super.preVisitDirectory(dir, attrs);
                final Path relDir = template.relativize(dir);
                final Path dirTarget = targetDir.resolve(relDir);
                System.err.printf("Creating directory %s\n", dirTarget);
                Files.copy(dir, dirTarget);
                return result;

            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                final FileVisitResult result = super.visitFile(file, attrs);
                final Path relFile = template.relativize(file);
                final Path fileTarget = targetDir.resolve(relFile);
                System.err.printf("Copying to %s\n", fileTarget);
                Files.copy(file, fileTarget);
                return result;
            }
        };
        Files.walkFileTree(template, copyVisitor);

        //TODO: Actually copy it and pass it to loadProject!
        main.loadProject(stage, targetDir);
        return true;
    }
}
