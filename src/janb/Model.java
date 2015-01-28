package janb;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 24/12/2014.
 */
public class Model implements IModel {

    public final CharacterListModel characters = new CharacterListModel();
    public final FileListModel files = new FileListModel();
    public final LocationListModel locations = new LocationListModel();
    public final EventListModel events = new EventListModel();
    private final ObservableList<IModel> categories;


    Model() {
        ArrayList<IModel> tempCategories = new ArrayList<>();
        tempCategories.add(characters);
        tempCategories.add(files);
        tempCategories.add(locations);
        tempCategories.add(events);

        categories = new ObservableListWrapper<>(tempCategories);
    }

    public void dump() {
        System.err.printf("DUMPING MODEL\n");
    }

    public ObservableList<IModel> getCategories() {
        return categories;
    }

    @Override
    public String getTitle() {
        return "Root";
    }

    @Override
    public ObservableList<IModel> getEntries() {
        return categories;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return null;
    }
}
