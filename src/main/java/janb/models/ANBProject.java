package janb.models;

import janb.mxl.IMxlFile;
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

    List<ProjectDB.ConstDBField> getPrototypes();

    List<MxlFile> getFiles();

    List<ProjectDB.ConstDBField> getEntityTypes();

    void trySave(IMxlFile file, EntityID entityID);
}
