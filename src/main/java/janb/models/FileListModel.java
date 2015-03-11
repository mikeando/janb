package janb.models;

import janb.Action;
import janb.controllers.IController;
import janb.mxl.MxlConstructionException;
import janb.mxl.MxlFile;
import janb.mxl.MxlMetadataFile;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
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

    private void addNew(IController controller) {
        throw new RuntimeException("FileListModel.addNew() not yet implemented");
        //entries.add( new FileModel("New File"));
    }

    //TODO: This seems overkill - we should just put the list into a TreeSet or similar.
    private boolean fileListContains(File[] fileList, File f) {
        for(File ff:fileList) {
            if(ff.equals(f))
                return true;
        }
        return false;
    }

    public void loadFromPath(File path, IViewModel viewModel) {
        System.err.printf("Loading root %s\n", path);
        final File[] fileList = path.listFiles();
        if(fileList==null) {
            System.err.printf("Unable to load root path %s, maybe it doesn't exist?", path);
            return;
        }
        for( File f : fileList) {

            if(f.getPath().endsWith(".mxl"))
                continue;

            final File metadataFile = new File(f.toString() + ".mxl");
            if (fileListContains(fileList, metadataFile)) {
                System.err.printf("Found file with .mxl data : %s + %s\n", f, metadataFile);
                MxlMetadataFile metadata = FileListModel.parseMXLFile(metadataFile);
                System.err.printf("Metadata = %s\n", metadata);
                try {
                    MxlFile mxlFile = MxlFile.createAndBind(f, metadata);
                    entries.add(new FileModel(mxlFile, viewModel));
                    System.err.printf("MxlFile = %s\n", mxlFile);
                } catch (IOException | MxlConstructionException e) {
                    System.err.printf("ERROR unable to load file %s : %s\n", f, e.getMessage());
                    e.printStackTrace();
                }
                continue;
            }


            System.err.printf("No a .mxl file for %s - ignoring\n", f);

        }
    }



    public static MxlMetadataFile parseMXLFile(File f) {
        System.err.printf("Trying to open MXL file %s\n", f);
        try {
            return new MxlMetadataFile(f);
        } catch (MxlConstructionException c) {
            System.err.printf("Error getting metadata\n%s\n", c);
            c.printStackTrace();
            throw new RuntimeException("Error getting metadata", c);
        }
    }
}
