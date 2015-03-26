package janb.models;

import java.util.List;

/**
 * Created by michaelanderson on 25/03/2015.
 */
public class EntityType {
    private final EntityID id;

    public EntityType(EntityID id) {
        this.id = id;
    }

    public EntityID id() {
        return id;
    }

    public Entity.ConstCollectionField asEntity() {
        throw new RuntimeException("NYI");
    }

    public void addSourceProject(ANBProject project) {
    }

    public List<ANBProject> getProjects() {
        return null;
    }
}
