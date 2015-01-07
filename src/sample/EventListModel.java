package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class EventListModel extends CategoryModel {

    private List<EventModel> entries = new ArrayList<>();

    EventListModel() {
        entries.add( new EventModel("Some Event"));
        entries.add( new EventModel("Another Event"));
    }

    @Override
    public String getTitle() {
        return "Events";
    }

    @Override
    public List<EventModel> getEntries() {
        return entries;
    }
}
