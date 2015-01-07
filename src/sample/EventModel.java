package sample;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class EventModel extends EntryModel {
    private String title;

    public EventModel(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
