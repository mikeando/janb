package janb.models;

import janb.project.ProjectDB;

/**
 * Created by michaelanderson on 26/03/2015.
 */
public class DefaultEntityMapper implements EntityMapper {
    @Override
    public Entity mapToEntity(ProjectDB.DBField e1) {
        return new Entity() {

            EntityType entityType = null;

            @Override
            public EntityID id() {
                return e1.getLocation();
            }

            @Override
            public EntityType getType() {
                return entityType;
            }

            @Override
            public void setType(EntityType entityType) {
                this.entityType = entityType;
            }
        };
    }

    @Override
    public EntityType mapToEntityType(ProjectDB.DBField entityType) {
        return new SimpleEntityType(entityType.getLocation());
    }
}
