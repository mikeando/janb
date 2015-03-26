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
    protected final List<IModel> childTypes = new ArrayList<>();

    protected final IEntitySource entitySource;
    protected final EntityType entityType;
    protected final IEntityToModelConverter<T> converter;

    EntityID getIDOrWarn(IEntitySource entitySource, String... components) {
        EntityID eventTypeID = EntityID.fromComponents(components);
        final Entity eventType = entitySource.getEntityById(eventTypeID);
        if (eventType == null) {
            System.err.printf("WARNING: No events of type 'entity' loaded.");
            return null;
        }
        return eventTypeID;
    }
    public EntityListModel(IEntitySource entitySource, IEntityToModelConverter<T> entityToModelConverter, String... components) {
        this(entitySource, EntityID.fromComponents(components), entityToModelConverter);
    }

    public EntityListModel(IEntitySource entitySource, EntityID entityTypeID, IEntityToModelConverter<T> entityToModelConverter) {
        this(entitySource, entitySource.getEntityTypeByID(entityTypeID), entityToModelConverter);
    }

    public EntityListModel(IEntitySource entitySource, EntityType entityType, IEntityToModelConverter<T> entityToModelConverter) {
        this.entitySource = entitySource;
        this.entityType = entityType;
        converter = entityToModelConverter;

        if(entityType==null) {
            System.err.printf("WARNING: No events of type '%s' loaded.\n", entityType);
            return;
        }
        //TODO: Need to register for changes to the entity source of the model
        entitySource.addListener(new EntitySourceListener() {
            @Override
            public void onAddEntity(Entity entity) {
                if(EntityID.isDirectChild(entityType.id(), entity.id())) {
                    onSourceAddChildEntity(entity);
                }
            }

            @Override
            public void onAddEntityType(EntityType type) {
                if(EntityID.isDirectChild(entityType.id(), type.id())) {
                    onSourceAddChildEntityType(type);
                }
            }
        });

        entitySource.getAllEntitiesOfType(entityType)
                  .stream()
                  .map( converter::toModel)
                  .filter(v -> (v != null))
                  .forEach(v -> {
                      entries.add(v);
                  });

        //TODO: This
        //TODO: Add sub types.
        //TODO: For now we assume subtypes also have model type of T.
        //      but really this should be going through a factory of some kind.
        //TODO: Doesn't getSubtypesOf belong in the entity type itself?
        entitySource.getChildTypesOfType(entityType)
                .stream()
                .filter(v -> (v != null))
                .map( v->getChildModel(v) )
                .forEach( childTypes::add );

    }

    EntityListModel<T> getChildModel(EntityType type) {
        return new EntityListModel<T>(entitySource, type, converter) {
            @Override
            public String getTitle() {
                return type.id().shortName();
            }

            @Override
            public List<Pair<String, Action>> getContextActions() {
                return null;
            }
        };
    }

    protected void onSourceAddChildEntityType(EntityType type) {
        EntityListModel<T> childModel = getChildModel(type);
        if(childModel!=null) {
            childTypes.add(childModel);
            int end = entries.size() + childTypes.size();
            publishEvent(new ModelEvent.AddEvent(this, childModel, end));
        }
    }

    private void onSourceAddChildEntity(Entity entity) {
        T childModel = converter.toModel(entity);
        if(childModel!=null) {
            entries.add(childModel);
            int end = entries.size();
            publishEvent(new ModelEvent.AddEvent(this, childModel, end));
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
