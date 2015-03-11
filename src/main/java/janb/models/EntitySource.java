package janb.models;

import janb.util.ANBFile;
import janb.util.ANBFileSystem;

import java.util.*;

/**
 * Created by michaelanderson on 27/02/2015.
 */
public class EntitySource implements IEntitySource {
    private final ANBFileSystem fileSystem;

    private final List<EntityType> types = new ArrayList<>();
    private final Map<IEntityDB.EntityID, EntityType> typesMap = new HashMap<>();
    private final List<IEntityDB.ICharacterBlock> entities = new ArrayList<>();

    public EntitySource(ANBFileSystem fileSystem) {

        this.fileSystem = fileSystem;
    }

    @Override
    public List<EntityType> getEntityTypes() {
        return Collections.unmodifiableList(types);
    }

    @Override
    public EntityType getEntityTypeByShortName(String name) {
        for(EntityType entityType:types) {
            if(entityType.shortName().equals(name))
                return entityType;
        }
        return null;
    }

    //TODO: This belongs somewhere else... maybe EntityType itself?
    public ANBFile getWritableLocationForEntityType(EntityType entityType) {

        // Check if each of the entity types is writable
        for(ANBFile location:entityType.getSourceLocations()) {
            if(location.isDirectory() && location.isWritable())
                return location;
        }

        // It's not writable, fall back to the default location.
        ANBFile defaultEntityDir = getDefaultEntityDir();
        return fileSystem.makePaths(defaultEntityDir, entityType.components());
    }

    //TODO: Implement me.
    private ANBFile getDefaultEntityDir() {
        return null;
    }

    @Override
    public IEntityDB.ICharacterBlock createNewEntityOfType(EntityType entityType, String name) {
        //TODO: Check it doesn't already exist.
        //TODO: Perform some sanity tests on the name.
        //      e.g. only a-z lower case, 0-9, _
        return new CharacterBlock(entityType.id().child(name), getWritableLocationForEntityType(entityType).child(name), entityType);
    }

    @Override
    public IEntityDB.ICharacterBlock getEntityByName(String name) {
        for(IEntityDB.ICharacterBlock e:entities) {
            if(e.id().shortName().equals(name))
                return e;
        }
        return null;
    }

    @Override
    public List<IEntityDB.ICharacterBlock> getEntitiesOfType(EntityType type) {
        List<IEntityDB.ICharacterBlock> result = new ArrayList<>();
        for(IEntityDB.ICharacterBlock e:entities) {
            if(e.getType()==type) {
                result.add(e);
            }
        }
        return result;
    }

    @Override
    public IEntityDB getDB() {
        return null;
    }

    public void addRoot(String s) {
        ANBFile root = fileSystem.getFileForString(s);
        loadEntitiesForPath(root,root, getOrCreateEntityType(new IEntityDB.EntityID()));
    }

    private void loadEntitiesForPath(ANBFile root, ANBFile file, EntityType entityType) {
        final List<ANBFile> files = fileSystem.getAllFiles(file);
        if(files==null)
            throw new RuntimeException("ANBFileSystem returned null");

        for(ANBFile f: files) {
            if (f.isDirectory()) {
                final List<String> path = f.relative_path(root);
                IEntityDB.EntityID id = new IEntityDB.EntityID(path);

                EntityType type = getOrCreateEntityType(id);
                type.addPath(f);

                loadEntitiesForPath(root, f, type);
            } else {
                parseEntity(root, f, entityType);
            }
        }
    }

    private EntityType getOrCreateEntityType(IEntityDB.EntityID id) {
        EntityType type = typesMap.get(id);
        if(type==null) {
            type = createEntityType(id);
        }
        return type;
    }

    private void parseEntity(ANBFile root, ANBFile f, EntityType entityType) {
        createEntity(root, f, entityType);
    }



    protected EntityType createEntityType(IEntityDB.EntityID id) {
        EntityType type = new EntityType(id);
        types.add(type);
        typesMap.put(id,type);
        return type;
    }

    protected void createEntity(ANBFile root, ANBFile f, EntityType entityType) {
        entities.add(new CharacterBlock(new IEntityDB.EntityID(f.relative_path(root)), f, entityType));
    }

    @Override
    public EntityType getEntityTypeByID(IEntityDB.EntityID id) {
        for(EntityType entityType:types) {
            if(entityType.id().equals(id))
                return entityType;
        }
        return null;
    }

    @Override
    public IEntityDB.ICharacterBlock getEntityById(IEntityDB.EntityID id) {
        for(IEntityDB.ICharacterBlock entity:entities) {
            if(entity.id().equals(id))
                return entity;
        }
        return null;
    }
}
