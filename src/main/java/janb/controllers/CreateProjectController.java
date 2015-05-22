package janb.controllers;

import janb.Main;
import janb.ui.ProjectCreatePage;
import janb.yaml.YamlConversionException;
import janb.yaml.YamlMap;
import janb.yaml.YamlString;
import janb.yaml.YamlUtils;
import javafx.stage.Stage;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
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

        private final Path path;
        private final YamlMap yaml;

        public ProjectMetadata(Path path) {
            this.path = path;
            this.yaml = getYamlMap(path);
        }


        //TODO: There's some duplication between this and the
        //      mxl loading code.
        private static YamlMap getYamlMap(Path path) {

            if(!path.toFile().exists())
                return null;

            try {
                InputStream is = new FileInputStream(path.toFile());

                Yaml yaml = new Yaml();
                Object yamlData = yaml.load(is);
                if (yamlData == null) {
                    System.err.printf("WARNING: Unable to parse YAML in file %s", path);
                }
                //TODO: This is not really the right exception type.

                try {
                    return YamlUtils.getRootAsMap(yamlData);
                } catch (YamlConversionException e) {
                    System.err.printf("WARNING: YAML in file %s does not have a map as root element", path);
                    return null;
                }
            } catch (FileNotFoundException e) {
                System.err.printf("WARNING: Unable find YAML config file %s", path);
                return null;
            }
        }


        public static ProjectMetadata loadFromProject(Path path) {
            return new ProjectMetadata(path.resolve("anb_meta.yml"));
        }

        public String getName() {
            if(yaml==null)
                return path.getParent().toFile().getName();
            final YamlString yamlString = yaml.getChild("name").asString();
            if(yamlString==null)
                return path.getParent().toFile().getName();
            return yamlString.getRawData();
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
            final ProjectMetadata projectMetadata = ProjectMetadata.loadFromProject(f.toPath());
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

        ProjectMetadata metadata = null;
        for(ProjectMetadata md : templateMetadata) {
            if(md!=null && md.getName().equals(templateName)) {
                metadata = md;
                break;
            }
        }
        if(metadata==null)
            return false;

        final Path template = metadata.path.getParent();

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

        //TODO: Update the new projects metadata. it should get its name recorded and get assigned a new
        //      id.

        main.loadProject(stage, targetDir);
        return true;
    }
}
