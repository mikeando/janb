package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 16/03/2015.
 */
public abstract class EntityListModel<T extends IModel> extends AbstractModel {

    protected final List<T> entries = new ArrayList<>();
    protected final List<IModel> childTypes = new ArrayList();

    protected final IEntityToModelConverter<T> converter;

    IEntityDB.EntityID getIDOrWarn(IEntitySource entitySource, String... components) {
        IEntityDB.EntityID eventTypeID = IEntityDB.EntityID.fromComponents(components);
        final IEntitySource.EntityType eventType = entitySource.getEntityTypeByID(eventTypeID);
        if (eventType == null) {
            System.err.printf("WARNING: No events of type 'entity' loaded.");
            return null;
        }
        return eventTypeID;
    }
    public EntityListModel(IEntitySource entitySource, IEntityToModelConverter<T> entityToModelConverter, String... components) {
        this(entitySource, IEntityDB.EntityID.fromComponents(components), entityToModelConverter);
    }

    public EntityListModel(IEntitySource entitySource, IEntityDB.EntityID entityTypeID, IEntityToModelConverter<T> entityToModelConverter) {
        this(entitySource, entitySource.getEntityTypeByID(entityTypeID), entityToModelConverter);
    }

    public EntityListModel(IEntitySource entitySource, IEntitySource.EntityType entityType, IEntityToModelConverter<T> entityToModelConverter) {
        converter = entityToModelConverter;

        if(entityType==null) {
            System.err.printf("WARNING: No events of type '%s' loaded.\n", entityType);
            return;
        }
        //TODO: Need to register for changes to the entity source of the model

        final List<IEntityDB.ICharacterBlock> eventEntities = entitySource.getEntitiesOfType(entityType);
        for(IEntityDB.ICharacterBlock event:eventEntities) {
            T model = converter.toModel(event);
            if(model!=null)
                entries.add(model);
        }

        //TODO: Add sub types.
        //TODO: For now we assume subtypes also have model type of T.
        //      but really this should be going through a factory of some kind.
        //TODO: Doesn't getSubtypesOf belong in the entity type itself?
        for(IEntitySource.EntityType type : entitySource.getSubtypesOf(entityType)) {
            childTypes.add(new EntityListModel<T>(entitySource, type, converter) {
                @Override
                public String getTitle() {
                    return type.shortName();
                }

                @Override
                public List<Pair<String, Action>> getContextActions() {
                    return null;
                }
            });
        }
    }

    @Override
    public List<IModel> getChildModels() {
        ArrayList<IModel> children = new ArrayList<>();
        children.addAll(entries);
        children.addAll(childTypes);
        return Collections.unmodifiableList(children);
    }
}
