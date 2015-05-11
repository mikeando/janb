package janb.project;

import janb.models.ANBProject;
import janb.models.EntityID;
import janb.mxl.*;
import janb.util.ANBFile;
import janb.util.ANBFileUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
* Created by michaelanderson on 24/03/2015.
*/
public class SimpleANBProject implements ANBProject {

    private List<MxlFile> files = new ArrayList<>();
    private List<ProjectDB.ConstDBField> entities = new ArrayList<>();
    private List<ProjectDB.ConstDBField> entityTypes = new ArrayList<>();
    private List<ProjectDB.ConstDBField> prototypes = new ArrayList<>();

    private ANBFileUtils fileutils = new ANBFileUtils();
    private final ANBFile sourcePath;

    public SimpleANBProject(ANBFile sourcePath) throws IOException, MxlConstructionException {
        this.sourcePath = sourcePath;
        if(sourcePath==null)
            throw new NullPointerException("sourcePath should not be null");
        final ANBFile filesDir = sourcePath.child("files");
        if(filesDir!=null) {
            loadFiles(filesDir, files);
        } else {
            System.err.printf("WARNING: files directory not found in "+sourcePath.pathAsString()+"\n" );
        }
        final ANBFile prototypesDir = sourcePath.child("prototypes");
        if(prototypesDir!=null) {
            loadPrototypesForPath(EntityID.fromComponents(), prototypesDir);
        } else {
            System.err.printf("WARNING: entities directory not found in " + sourcePath.pathAsString() + "\n");
        }

        final ANBFile entitiesDir = sourcePath.child("entities");
        if(entitiesDir!=null) {
            loadEntitiesForPath(EntityID.fromComponents(), entitiesDir);
        } else {
            System.err.printf("WARNING: entities directory not found in " + sourcePath.pathAsString() + "\n");
        }
    }

    private void loadEntitiesForPath(EntityID id, ANBFile entitiesDir) throws IOException {
        System.err.printf("Loading entities for "+entitiesDir.pathAsString()+"\n");

        //If it doesnt have a _type file then its a entity type
        ANBFile typeFile = entitiesDir.child("_type");
        if(typeFile!=null && typeFile.exists()) {
            System.err.printf("Loading collection %s\n", entitiesDir);
            final ProjectDB.ConstCollectionField entity = loadCollectionEntity(entitiesDir, id);
            entities.add(entity);
        } else {
            entityTypes.add(new ProjectDB.ConstCollectionField(id, new HashMap<>(), null));
            for (ANBFile file : entitiesDir.getAllFiles()) {
                if(file.isDirectory()) {
                    loadEntitiesForPath(id.child(file.getName()), file);
                } else {
                    System.err.printf("Found non-directory "+file+" in "+entitiesDir+"\n");
                }
            }
        }
    }

    private void loadPrototypesForPath(EntityID id, ANBFile entitiesDir) throws IOException {
        System.err.printf("Loading prototypes for "+entitiesDir.pathAsString()+"\n");

        //If it doesnt have a _type file then its a entity type
        ANBFile typeFile = entitiesDir.child("_type");
        if(typeFile!=null && typeFile.exists()) {
            System.err.printf("Loading collection %s\n", entitiesDir);
            final ProjectDB.ConstCollectionField entity = loadCollectionEntity(entitiesDir, id);
            prototypes.add(entity);
        } else {
            //TODO: Should we also be registering any new types here?
            //entityTypes.add(new ProjectDB.ConstCollectionField(id, new HashMap<>(), null));
            for (ANBFile file : entitiesDir.getAllFiles()) {
                if(file.isDirectory()) {
                    loadPrototypesForPath(id.child(file.getName()), file);
                } else {
                    System.err.printf("Found non-directory "+file+" in "+entitiesDir+"\n");
                }
            }
        }
    }

    public static <T> void collectionAddIfNonNull(Collection<T> list, T value) {
        if(value==null)
            return;
        list.add(value);
    }

    static void loadFiles(ANBFile filesDir, List<MxlFile> files) throws IOException, MxlConstructionException {

        if(filesDir==null)
            throw new NullPointerException("filesDir should not be null");

        System.err.printf("Loading files for "+filesDir.pathAsString()+"\n");

        Set<ANBFile> processedFiles = new HashSet<>();

        for (ANBFile file : filesDir.getAllFiles()) {

            if (!processedFiles.add(file))
                continue;

            if (file.isDirectory()) {
                loadFiles(file, files);
                continue;
            }

            ANBFile metaDataFile;
            ANBFile dataFile;

            System.err.printf("SimpleANBProject.loadFiles() - loading %s\n", file);

            //TODO: Not sure these belong in ANBFile, or some utils class.
            if (file.hasExtension(".mxl")) {
                metaDataFile = file;
                dataFile = file.withoutExtension(".mxl");
            } else {
                dataFile = file;
                metaDataFile = file.withExtension(".mxl");
            }

            System.err.printf("   - metaDataFile = %s\n", metaDataFile);
            System.err.printf("   - dataFile = %s\n", dataFile);

            //TODO: Not sure a RTE is the right thing to do here.
            if (dataFile == null || !dataFile.exists()) {
                throw new RuntimeException("File no longer exists");
            }

            collectionAddIfNonNull(processedFiles, metaDataFile);
            collectionAddIfNonNull(processedFiles, dataFile);


            if (metaDataFile!=null && metaDataFile.exists()) {
                System.err.printf("    - loading with metadata...\n");
                collectionAddIfNonNull(files, loadMxlFile(dataFile, metaDataFile));
            } else {
                System.err.printf("    - loading without metadata...\n");
                collectionAddIfNonNull(files, loadRawFile(dataFile));
            }
        }
    }

        static MxlFile loadMxlFile(ANBFile dataFile, ANBFile metadataFile) throws IOException, MxlConstructionException {
            if(dataFile==null)
                throw new NullPointerException("dataFile can not be null");
            if(metadataFile==null)
                throw new NullPointerException("metadataFile can not be null");
            IMxlMetadataFile metadata = parseMXLFile(metadataFile);
            return MxlFile.createAndBind(dataFile.getName(), dataFile.readContents(), metadata);
        }

        static MxlFile loadRawFile(ANBFile dataFile) throws IOException, MxlConstructionException {
            IMxlMetadataFile metadata = createMXLFile(dataFile.withExtension(".mxl"));
            return MxlFile.createAndBind(dataFile.getName(), dataFile.readContents(), metadata);
        }

    private static ProjectDB.ConstCollectionField loadCollectionEntity(ANBFile path, EntityID entityID) throws IOException {
        ANBFile typeFile = path.child("_type");
        if(typeFile!=null && typeFile.isDirectory())
            throw new RuntimeException("_type must not be a directory");
        if(typeFile!=null && typeFile.exists()) {
            final byte[] contents = typeFile.readContents();
            System.err.printf("Loading collection @ %s - _type = %s\n",
                    path.pathAsString(),
                    (contents==null)?"null":new String(contents, StandardCharsets.UTF_8));
            //TODO: Handle the _type file, and resolve any prototypes.
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
            } else {
                System.err.printf("Tried to load entity "+fileName+" but loadEntity returned null\n");
            }
        }

        //TODO: Need to use the prototype too - tricky as we can't get it until we've loaded everything
        //      But the return value is const...
        return new ProjectDB.ConstCollectionField(entityID, fields, null);
    }

    //TODO: Throwing RuntimeExceptions from inside this is not appropriate.
    private static ProjectDB.AbstractConstDBField loadEntity(ANBFile path, EntityID id) throws IOException {
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

    private static ProjectDB.AbstractConstDBField loadRefEntity(ANBFile path, EntityID id) {
        return null;
    }

    private static ProjectDB.ConstTextField loadTextEntity(ANBFile path, EntityID id) throws IOException {

        final ANBFile textFile = path.child("text.md");

        //TODO: Should handle this better?
        if(textFile==null || !textFile.exists()) {
            System.err.printf("Unable to load text entity " + path.pathAsString() + " as it has no text.md entry");
            return null;
        }

        final byte[] contents = textFile.readContents();
        if(contents==null)
            throw new IOException("ANBFile.readContents() returned null for file "+textFile);
        return new ProjectDB.ConstTextField(id, new String(contents, StandardCharsets.UTF_8));
    }



    public static class EntityCreationVisitor implements ProjectDB.EntityVisitor {

        @Override
        public boolean onCollection(ProjectDB.CollectionField cf) {
            System.err.printf("SAVING %s\n", cf);
            final Map<String, ? extends ProjectDB.DBField> fields = cf.getFields();
            for (Map.Entry<String, ? extends ProjectDB.DBField> entry : fields.entrySet()) {
                System.err.printf("Trying to save entry %s\n", entry);
            }
            return true;
        }

        @Override
        public void onText(ProjectDB.TextField tf) {
            throw new RuntimeException("Saving Text entity not yet supported");

        }

        @Override
        public void onReference(ProjectDB.ReferenceField rf) {
            throw new RuntimeException("Saving Reference entity not yet supported");

        }
    }

    @Override
    public boolean tryUpdate(ProjectDB.DBField entity) {
        if(entity==null)
            throw new NullPointerException("entity can not be null");
        EntityID id = entity.getLocation();
        //Do we have an entity at this location
        ProjectDB.ConstDBField currentEntity = getEntityById(id);
        if(currentEntity==null) {
            throw new RuntimeException("Entity does not exist");
        }

        ANBFile entityDir = fileutils.findOrCreateDirectoryForID(sourcePath, id.prepend("entities"));
        if(entityDir==null)
            throw new RuntimeException("Unable to get or create entity parent directory");

        ProjectDB.EntityVisitor creator = new EntityCreationVisitor();

        entity.visit(creator);
        return true;
    }

    @Override
    public boolean trySave(ProjectDB.DBField entity) {
        if(entity==null)
            throw new NullPointerException("entity can not be null");
        EntityID id = entity.getLocation();
        //Do we have an entity at this location
        ProjectDB.ConstDBField currentEntity = getEntityById(id);
        if(currentEntity!=null) {
            throw new RuntimeException("Entity already exists");
        }


        ANBFile entityDir = fileutils.findOrCreateDirectoryForID(sourcePath, id.prepend("entities"));
        if(entityDir==null)
            throw new RuntimeException("Unable to get or create entity parent directory");

        ProjectDB.EntityVisitor creator = new EntityCreationVisitor();

        entity.visit(creator);
        return true;
    }

    @Override
    public void trySave(IMxlFile file, EntityID entityID) {
        ANBFile entityDir = fileutils.findOrCreateDirectoryForID(sourcePath, entityID.parent());
        entityDir.createFile(entityID.shortName(), file.getRawData().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ProjectDB.ConstDBField createNewEntityOfType(EntityID id, String name) {
        //TODO: Really needs to be hooked into the tree.
        //      And saved to disk?
        return new ProjectDB.ConstCollectionField(id.child(name), new HashMap<>(), null);
    }

    @Override
    public ProjectDB.ConstDBField getEntityById(EntityID id) {
        if(id==null)
            throw new NullPointerException("id can not be null");
        for(ProjectDB.ConstDBField x:entities) {
            if(Objects.equals(x.getLocation(), id))
                return x;
        }
        return null;
    }

    @Override
    public List<ProjectDB.ConstDBField> getEntities() {
        return entities;
    }

    @Override
    public List<ProjectDB.ConstDBField> getPrototypes() {
        return prototypes;
    }


    @Override
    public List<IMxlFile> getFiles() {
        return Collections.unmodifiableList(files);
    }

    @Override
    public List<ProjectDB.ConstDBField> getEntityTypes() {
        return entityTypes;
    }

    public static IMxlMetadataFile parseMXLFile(ANBFile f) throws IOException, MxlConstructionException {
        if(f==null)
            throw new NullPointerException("f should not be null");
        final byte[] contents = f.readContents();
        if(contents==null)
            return MxlMetadataFile.empty();
        try(InputStream is = new ByteArrayInputStream(contents)) {
            MxlMetadataFile.MxlMetadataSource metadataSource = new MxlMetadataFile.MxlMetadataSource() {
            };
            return MxlMetadataFile.fromInputStream(is, metadataSource);
        }
    }

    public static IMxlMetadataFile createMXLFile(ANBFile f) {
        return MxlMetadataFile.empty();
    }
}
