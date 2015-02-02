package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class EventListModel extends AbstractModel {

    private final List<EventModel> entries;

    EventListModel() {
        entries = new ArrayList<>();
        entries.add( new EventModel("Some Event"));
        entries.add( new EventModel("Another Event"));
    }

    @Override
    public String getTitle() {
        return "Events";
    }


    @Override
    public List<Pair<String, Action>> getContextActions() {
        return new ArrayList<>();
    }

    @Override
    public List<IModel> getChildModels() {
        return Collections.unmodifiableList(entries);
    }
}
