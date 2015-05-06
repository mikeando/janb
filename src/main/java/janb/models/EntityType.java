package janb.models;

import java.util.List;

/**
 * Created by michaelanderson on 9/04/2015.
 */
public interface EntityType {
    EntityID id();
    void addSourceProject(ANBProject project);
    List<ANBProject> getProjects();
}
