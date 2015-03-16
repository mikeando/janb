package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class EventListModel extends EntityListModel<EventModel> {

    EventListModel(IEntitySource entitySource) {
        super(entitySource, new IEntityToModelConverter() {
            @Override
            public EventModel toModel(IEntityDB.ICharacterBlock entity) {
                return new EventModel(entity.id().shortName());
            }
        }, "events");

        entries.add( new EventModel("X Some Event"));
        entries.add( new EventModel("X Another Event"));
    }

    @Override
    public String getTitle() {
        return "Events";
    }


    @Override
    public List<Pair<String, Action>> getContextActions() {
        return new ArrayList<>();
    }


}
