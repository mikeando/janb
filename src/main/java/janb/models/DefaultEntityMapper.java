package janb.models;

import janb.project.ProjectDB;

/**
 * Created by michaelanderson on 26/03/2015.
 */
public class DefaultEntityMapper implements EntityMapper {
    @Override
    public Entity mapToEntity(ProjectDB.DBField e1) {
        return new Entity() {
            @Override
            public EntityID id() {
                return e1.getLocation();
            }

            @Override
            public EntityType getType() {
                return null;
            }
        };
    }
}
