package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
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

    public static void parseMXLFile(File f) {
        //Later we want this file to be YAML format I think, but for now we'll just use a stupid text format.
        try {
            FileInputStream is = new FileInputStream(f);

        } catch (IOException e){
            System.err.printf("ERROR: Unable to load metadata file %s\n", f);
        }
    }
}
