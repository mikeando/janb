package janb.project;

import janb.models.ANBProject;
import janb.models.EntityID;
import janb.util.ANBFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by michaelanderson on 24/03/2015.
*/
public class SimpleANBProject implements ANBProject {

    public SimpleANBProject(ANBFile sourcePath) {

    }

    //TODO: Shift this into the project?
    private ProjectDB.ConstCollectionField loadCollectionEntity(ANBFile path, EntityID entityID) throws IOException {
        ANBFile typeFile = path.child("_type");
        if(typeFile!=null && typeFile.isDirectory())
            throw new RuntimeException("_type must not be a directory");
        if(typeFile!=null) {
            //TODO: Handle the _type file
        }

        final List<ANBFile> files = path.getAllFiles();
        Map<String, ProjectDB.ConstDBField> fields = new HashMap<>();
        for(ANBFile file : files) {
            if(!file.isDirectory())
                continue;
            String fileName  = file.getName();
            ProjectDB.AbstractConstDBField child = loadEntity(file, entityID.child(fileName));
            if(child!=null) {
                fields.put(fileName, child);
            }
        }

        //TODO: Need to use the prototype too - tricky as we can't get it until we've loaded everything
        //      But the return value is const...
        return new ProjectDB.ConstCollectionField(entityID, fields, null);
    }

    //TODO: Throwing RuntimeExceptions from inside this is not appropriate.
    private ProjectDB.AbstractConstDBField loadEntity(ANBFile path, EntityID id) throws IOException {
        ANBFile typeFile = path.child("_type");
        if(typeFile!=null && typeFile.isDirectory())
            throw new RuntimeException("_type must not be a directory");
        if(typeFile==null)
            return loadCollectionEntity(path,id);
        final byte[] bytes = typeFile.readContents();
        if(bytes==null)
            throw new RuntimeException("_type must not have null data.");
        String type = new String(bytes, StandardCharsets.UTF_8);
        if(type.equals("text"))
            return loadTextEntity(path, id);
        if(type.startsWith("ref:"))
            return loadRefEntity(path,id);
        if(type.startsWith("collection"))
            return loadCollectionEntity(path,id);
        //TODO: Throw a more sensible error.
        throw new RuntimeException("Unknown type:"+type);
    }

    private ProjectDB.AbstractConstDBField loadRefEntity(ANBFile path, EntityID id) {
        return null;
    }

    private ProjectDB.ConstTextField loadTextEntity(ANBFile path, EntityID id) throws IOException {
        return null;
    }


    @Deprecated
    private void loadEntitiesForPath(ANBFile root, ANBFile file, ProjectDB.CollectionField entityType) {
        throw new RuntimeException("NYI");

//        final List<ANBFile> files = file.getAllFiles();
//        if(files==null)
//            throw new RuntimeException("ANBFileSystem returned null");
//
//        for(ANBFile f: files) {
//            if (f.isDirectory()) {
//                final List<String> path = f.relative_path(root);
//                IEntityDB.EntityID id = new IEntityDB.EntityID(path);
//
//                Entity.ConstCollectionField type = getOrCreateEntityType(getPrototypeForEntityId(id));
//                type.addPath(f);
//
//                loadEntitiesForPath(root, f, type);
//            } else {
//                parseEntity(root, f, entityType);
//            }
//        }
    }

    @Override
    public boolean tryUpdate(ProjectDB.DBField entity) {
        if(entity==null)
            throw new NullPointerException("entity can not be null");
        throw new RuntimeException("NYI");
    }

    @Override
    public boolean trySave(ProjectDB.DBField entity) {
        if(entity==null)
            throw new NullPointerException("entity can not be null");
        throw new RuntimeException("NYI");
    }

    @Override
    public ProjectDB.ConstDBField getEntityById(EntityID id) {
        if(id==null)
            throw new NullPointerException("id can not be null");
        return null;
    }

    @Override
    public List<ProjectDB.ConstDBField> getEntities() {
        return Collections.EMPTY_LIST;
    }
}
