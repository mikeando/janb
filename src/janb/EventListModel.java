package janb;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class EventListModel implements IModel {

    private final ObservableList<IModel> entries;

    EventListModel() {
        ArrayList<IModel> entries = new ArrayList<>();
        entries.add( new EventModel("Some Event"));
        entries.add( new EventModel("Another Event"));
        this.entries = new ObservableListWrapper<>(entries);
    }

    @Override
    public String getTitle() {
        return "Events";
    }

    @Override
    public ObservableList<IModel> getEntries() {
        return entries;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return new ArrayList<>();
    }
}
