package janb.models;

import janb.mxl.IMxlFile;

import java.util.List;

/**
 * Provides a little more context than the lower level IEntityDB
 *
 * @todo Changed the things that should return Entity to do so.
 * @todo Removed the deprecated crufteroonie
 */
public interface IEntitySource {

    List<Entity> getEntitiesOfType(EntityType type);

    void addListener(EntitySourceListener entitySourceListener);

    Entity getEntityById(EntityID id);

    List<EntityType> getEntityTypes();

    EntityType getEntityTypeByID(EntityID id);

    List<Entity> getAllEntitiesOfType(EntityType entityType);
    List<EntityType> getChildTypesOfType(EntityType entityType);

    void saveEntity(Entity entity);

    //TODO: Is this needed?
    EntityType getEntityTypeByShortName(String name);

    //TODO: Is this needed
    Entity getEntityByName(String name);

    //TODO: This should return IMxlFile
    List<IMxlFile> getFiles();

    void createNewEntityType(EntityType et);

    Entity createNewEntityOfType(EntityType characterEntityType, String name);
}
