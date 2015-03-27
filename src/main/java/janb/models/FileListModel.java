package janb.models;

import janb.Action;
import janb.controllers.IController;
import janb.mxl.MxlConstructionException;
import janb.mxl.MxlFile;
import janb.mxl.MxlMetadataFile;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @todo make this work through the entity source.
 */
public class FileListModel extends AbstractModel {
    private final List<FileModel> entries;
    private final IViewModel viewModel;
    private final IEntitySource entitySource;

    public FileListModel(IViewModel viewModel, IEntitySource entitySource) {
        this.viewModel = viewModel;
        this.entitySource = entitySource;
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

    private void loadFiles() {
        System.err.printf("Loading files from entitySource = %s\n", entitySource);

        for( MxlFile f : entitySource.getFiles() ) {
            entries.add(new FileModel(f, viewModel));
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
