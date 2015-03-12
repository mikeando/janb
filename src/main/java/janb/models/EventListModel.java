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
    IEntityToModelConverter converter = new IEntityToModelConverter() {

        @Override
        public EventModel toModel(IEntityDB.ICharacterBlock entity) {
            return new EventModel(entity.id().asString());
        }
    };

    EventListModel(IEntitySource entitySource) {
        entries = new ArrayList<>();
        entries.add( new EventModel("X Some Event"));
        entries.add( new EventModel("X Another Event"));

        IEntityDB.EntityID eventTypeID = IEntityDB.EntityID.fromComponents("events");
        final IEntitySource.EntityType eventType = entitySource.getEntityTypeByID(eventTypeID);
        if(eventType==null) {
            System.err.printf("WARNING: No events of type 'entity' loaded.");
            return;
        }

        final List<IEntityDB.ICharacterBlock> eventEntities = entitySource.getEntitiesOfType(eventType);
        for(IEntityDB.ICharacterBlock event:eventEntities) {
            EventModel eventModel = converter.toModel(event);
            if(eventModel!=null)
                entries.add(eventModel);
        }
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
