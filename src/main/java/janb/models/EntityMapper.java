package janb.models;

import janb.project.ProjectDB;

/**
 * Created by michaelanderson on 26/03/2015.
 */
public interface EntityMapper {
    Entity mapToEntity(ProjectDB.DBField e1);
}
