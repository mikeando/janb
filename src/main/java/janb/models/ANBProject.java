package janb.models;

import janb.mxl.MxlFile;
import janb.project.ProjectDB;

import java.util.List;

/**
* Created by michaelanderson on 24/03/2015.
*/
public interface ANBProject {
    boolean tryUpdate(ProjectDB.DBField entity);
    boolean trySave(ProjectDB.DBField entity);

    ProjectDB.ConstDBField getEntityById(EntityID id);

    List<ProjectDB.ConstDBField> getEntities();

    List<MxlFile> getFiles();
}
