package janb.models;

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
    //TODO: Shift this into the project?
    private Entity.ConstCollectionField loadCollectionEntity(ANBFile path, EntityID entityID) throws IOException {
        ANBFile typeFile = path.child("_type");
        if(typeFile!=null && typeFile.isDirectory())
            throw new RuntimeException("_type must not be a directory");
        if(typeFile!=null) {
            //TODO: Handle the _type file
        }

        final List<ANBFile> files = path.getAllFiles();
        Map<String, Entity.ConstEntityField> fields = new HashMap<>();
        for(ANBFile file : files) {
            if(!file.isDirectory())
                continue;
            String fileName  = file.getName();
            Entity.AbstractConstEntityField child = loadEntity(file, entityID.child(fileName));
            if(child!=null) {
                fields.put(fileName, child);
            }
        }

        //TODO: Need to use the prototype too - tricky as we can't get it until we've loaded everything
        //      But the return value is const...
        return new Entity.ConstCollectionField(entityID, fields, null);
    }

    //TODO: Throwing RuntimeExceptions from inside this is not appropriate.
    private Entity.AbstractConstEntityField loadEntity(ANBFile path, EntityID id) throws IOException {
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

    private Entity.AbstractConstEntityField loadRefEntity(ANBFile path, EntityID id) {
        return null;
    }

    private Entity.ConstTextField loadTextEntity(ANBFile path, EntityID id) throws IOException {
        return null;
    }


    @Deprecated
    private void loadEntitiesForPath(ANBFile root, ANBFile file, Entity.CollectionField entityType) {
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
    public boolean tryUpdate(Entity.EntityField entity) {
        throw new RuntimeException("NYI");
    }

    @Override
    public boolean trySave(Entity.EntityField entity) {
        throw new RuntimeException("NYI");
    }

    @Override
    public Entity.ConstEntityField getEntityById(EntityID id) {
        throw new RuntimeException("NYI");
    }

    @Override
    public List<Entity.ConstEntityField> getEntities() {
        return Collections.EMPTY_LIST;
    }
}
