package janb.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 25/03/2015.
 */
public class SimpleEntityType implements EntityType {
    private final EntityID id;
    private final List<ANBProject> inProjects = new ArrayList<>();

    public SimpleEntityType(EntityID id) {
        this.id = id;
    }

    public EntityID id() {
        return id;
    }

    public void addSourceProject(ANBProject project) {
        inProjects.add(project);
    }

    public List<ANBProject> getProjects() {
        return Collections.unmodifiableList(inProjects);
    }
}
