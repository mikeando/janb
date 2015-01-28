package janb;

import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class EventModel implements IModel {
    private String title;

    public EventModel(String title) {
        this.title = title;
    }

    @Override
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
