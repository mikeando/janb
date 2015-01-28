package janb.models;

import janb.Action;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class LocationModel implements IModel {
    private final String title;

    public LocationModel(String title) {
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public ObservableList<IModel> getEntries() {
        return null;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return null;
    }
}
