package janb.models;

import janb.Action;
import janb.mxl.MxlConstructionException;
import janb.mxl.MxlMetadataFile;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class FileListModel extends AbstractModel {
    private final List<FileModel> entries;

    FileListModel() {
        entries = new ArrayList<>();
        entries.add( new FileModel("Some File"));
        entries.add( new FileModel("Another File"));
    }

    @Override
    public String getTitle() {
        return "Files";
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        ArrayList<Pair<String,Action>> actions = new ArrayList<>();
        actions.add( new Pair<>("Add File", this::addNew));
        return actions;
    }

    @Override
    public List<IModel> getChildModels() {
        return Collections.unmodifiableList(entries);
    }

    private void addNew() {
        entries.add( new FileModel("New File"));
    }

    public void loadFromPath(File path) {
        System.err.printf("Loading root %s\n", path);
        final File[] fileList = path.listFiles();
        if(fileList==null) {
            System.err.printf("Unable to load root path %s, maybe it doesn't exist?", path);
            return;
        }
        for( File f : fileList) {
            System.err.printf("Should be loading resource from %s\n", f);
            if(f.getPath().endsWith(".mxl")) {
                System.err.printf("Got me a .mxl file :%s\n", f);
                MxlMetadataFile metadata = FileListModel.parseMXLFile(f);
                System.err.printf("Metadata = %s\n", metadata);
            } else {
                System.err.printf("Not a .mxl file - ignoring %s\n", f);
            }
        }
    }



    public static MxlMetadataFile parseMXLFile(File f) {
        System.err.printf("Trying to open MXL file %s\n", f);
        try {
            return new MxlMetadataFile(f);
        } catch (MxlConstructionException c) {
            System.err.printf("Error getting metadata\n%s", c);
            c.printStackTrace();
            throw new RuntimeException("Error getting metadata", c);
        }
    }
}
