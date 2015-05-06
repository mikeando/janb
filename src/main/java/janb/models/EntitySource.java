package janb.models;

import janb.mxl.MxlFile;
import janb.project.ProjectDB;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by michaelanderson on 27/02/2015.
 */
public class EntitySource implements IEntitySource {

    private final List<ANBProject> projects = new ArrayList<>();
    private final List<EntityType> types = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private List<EntitySourceListener> listeners = new ArrayList<>();
    private EntityMapper mapper = new DefaultEntityMapper();

    public EntitySource() {
        types.add(new SimpleEntityType(EntityID.fromComponents()));
    }

    @Override
    public List<EntityType> getEntityTypes() {
        return Collections.unmodifiableList(types);
    }

    @Override
    public EntityType getEntityTypeByShortName(String name) {
        for (EntityType entityType : types) {
            if (Objects.equals(entityType.id().shortName(), name))
                return entityType;
        }
        return null;
    }

    @Override
    public Entity getEntityByName(String name) {
        for(Entity e:entities) {
            if(e.id().shortName().equals(name))
                return e;
        }
        return null;
    }

    @Override
    public List<MxlFile> getFiles() {
        List<MxlFile> result = new ArrayList<>();
        for (ANBProject project : projects) {
            result.addAll(project.getFiles());
        }
        return result;
    }

    @Override
    public void createNewEntityType(EntityType et) {
        for (EntitySourceListener listener : listeners) {
            listener.onAddEntityType(et);
        }
        types.add(et);
    }

    @Override
    public List<Entity> getEntitiesOfType(EntityType type) {
        return entities.stream()
                .filter(e -> e.getType() == type)
                .collect(Collectors.toList());
    }


    //TODO: This should throw something saner.
    @Deprecated
    public void saveEntity(ProjectDB.DBField entity) {
        // Look for a writable version in each project.
        for(ANBProject p : projects) {
            if(p.tryUpdate(entity))
                return;
        }

        for(ANBProject p : projects) {
            if(p.trySave(entity))
                return;
        }
        throw new RuntimeException("No save location found");
    }

    public void addProject(ANBProject project) {
        projects.add(project);

        //TODO: Should ensure mapper is never null.
        if(mapper==null)
            throw new NullPointerException("mapper should not be null");


        //TODO: Convert this to a for loop to polpulate the types map.
        project.getEntityTypes().stream()
                .map(mapper::mapToEntityType)
                .forEach(types::add);

        project.getEntities().stream()
                .map(mapper::mapToEntity)
                .map(this::resolveEntityType)
                .forEach(entities::add);

        //TODO: Rehash any cached info.
        //TODO: Let the listeners know?
    }

    private Entity resolveEntityType(Entity entity) {
        EntityID typeID = entity.id().parent();
        final EntityType entityType = getEntityTypeByID(typeID);
        entity.setType(entityType);
        return entity;
    }

    @Override
    public void addListener(EntitySourceListener entitySourceListener) {
        listeners.add(entitySourceListener);
    }

    @Override
    public List<Entity> getAllEntitiesOfType(EntityType entityType) {
        return entities.stream()
                .filter( v -> v!=null )
                .filter(v -> Objects.equals(v.getType(), entityType))
                .collect(Collectors.toList());
    }

    @Override
    public List<EntityType> getChildTypesOfType(EntityType entityType) {
        return types.stream()
                .filter( v -> EntityID.isDirectChild(entityType.id(),v.id() ))
                .collect(Collectors.toList());
    }

    @Override
    public void saveEntity(Entity entity) {
        throw new RuntimeException("NYI");
    }

    @Override
    public EntityType getEntityTypeByID(EntityID id) {
        for (EntityType type : types) {
            if(type.id().equals(id))
                return type;
        }
        return null;
    }

    @Override
    public Entity getEntityById(EntityID id) {
        for(Entity entity:entities) {
            if(entity!=null && Objects.equals(entity.id(),id))
                return entity;
        }
        return null;
    }

    public void setEntityMapper(EntityMapper mapper) {
        if(mapper==null)
            throw new NullPointerException("mapper can not be null");
        this.mapper = mapper;
    }

    public EntityMapper getEntityMapper() {
        return mapper;
    }
}
