package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class EventListModel extends EntityListModel<EventModel> {

    //TODO: Need to hook this up to listen to the entitySource - otherwise add / remove etc wont be detected.
    EventListModel(IEntitySource entitySource) {
        super(entitySource, new IEntityToModelConverter() {
            @Override
            public EventModel toModel(Entity entity) {
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
        final ArrayList<Pair<String, Action>> actions = new ArrayList<>();
        actions.add( new Pair<>("create", (Action) controller -> {
            final EntityID id = entityType.id().child("donkey");
            //TODO: Use a real class for this
            final Entity entity = new Entity() {
                @Override
                public EntityID id() {
                    return id;
                }

                @Override
                public EntityType getType() {
                    return entityType;
                }
            };
            entitySource.saveEntity(entity);
        }));
        return actions;
    }


}
