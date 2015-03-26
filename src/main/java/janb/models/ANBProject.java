package janb.models;

import janb.project.ProjectDB;

import java.util.List;

/**
* Created by michaelanderson on 24/03/2015.
*/
public interface ANBProject {
    boolean tryUpdate(ProjectDB.EntityField entity);
    boolean trySave(ProjectDB.EntityField entity);

    ProjectDB.ConstEntityField getEntityById(EntityID id);

    List<ProjectDB.ConstEntityField> getEntities();
}
