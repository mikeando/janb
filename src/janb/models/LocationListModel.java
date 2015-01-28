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
public class LocationListModel implements IModel {

    private final ObservableList<IModel> entries;

    LocationListModel() {
        final ArrayList<IModel> entries = new ArrayList<>();
        entries.add(new LocationModel("Some Location"));
        entries.add(new LocationModel("Another Location"));
        this.entries = new ObservableListWrapper<>(entries);
    }

    @Override
    public String getTitle() {
        return "Locations";
    }

    @Override
    public ObservableList<IModel> getEntries() {
        return entries;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        ArrayList<Pair<String,Action>> actions = new ArrayList<>();
        actions.add( new Pair<>("Add Location", this::addNew));
        return actions;
    }

    private void addNew() {
        entries.add( new LocationModel("New Location"));
    }
}
