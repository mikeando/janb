package janb.models;

import com.sun.javafx.collections.ObservableListWrapper;
import janb.Action;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class FileListModel implements IModel {
    private final ObservableList<IModel> entries;

    FileListModel() {
        ArrayList<IModel> entries = new ArrayList<>();
        entries.add( new FileModel("Some File"));
        entries.add( new FileModel("Another File"));
        this.entries = new ObservableListWrapper<>(entries);
    }

    @Override
    public String getTitle() {
        return "Files";
    }

    @Override
    public ObservableList<IModel> getEntries() {
        return entries;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        ArrayList<Pair<String,Action>> actions = new ArrayList<>();
        actions.add( new Pair<>("Add File", this::addNew));
        return actions;
    }

    private void addNew() {
        entries.add( new FileModel("New File"));
    }
}
