package janb.models;

import janb.util.ANBFile;
import janb.util.ANBFileSystem;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by michaelanderson on 27/02/2015.
 */
public class EntitySourceTest {

    @Rule public JUnitRuleMockery context = new JUnitRuleMockery();

    private static class SimpleFileSystemDetails {
        private ANBFileSystem fileSystem;
        private Map<String, ANBFile> files = new HashMap<>();
        private Map<ANBFile, List<ANBFile>> childrenOfFile = new HashMap<>();

        private final JUnitRuleMockery context;
        private Map<ANBFile, ANBFile> parentOfFile = new HashMap<>();

        private SimpleFileSystemDetails(JUnitRuleMockery context) {
            this.context = context;
            fileSystem = context.mock(ANBFileSystem.class);
        }

        ANBFile file(String key) {
            assertThat(files, is(notNullValue()));
            final ANBFile file = files.get(key);
            assertThat(file, is(notNullValue()));
            return file;
        }

        ANBFile createDirectory(String name) {
            return createDirectory(name,name);
        }

        ANBFile createDirectory(String name, String mockName) {
            final ANBFile file = context.mock(ANBFile.class, mockName);
            files.put(name, file);
            final ArrayList<ANBFile> children = new ArrayList<>();
            childrenOfFile.put(file, children);

            context.checking( new Expectations(){{
                allowing(fileSystem).getAllFiles(file);
                will( returnValue(children));

                allowing(file).isDirectory();
                will(returnValue(true));

            }});

            return file;
        }

        public ANBFile createFile(String name) {
            return createFile(name,name);
        }

        ANBFile createFile(String name, String mockName) {
            final ANBFile file = context.mock(ANBFile.class, mockName);
            files.put(name, file);
            //final ArrayList<ANBFile> children = new ArrayList<>();
            //childrenOfFile.put(file, children);

            context.checking( new Expectations(){{
                //allowing(fileSystem).getAllFiles(file);
                //will( returnValue(children));

                allowing(file).isDirectory();
                will(returnValue(false));

            }});

            return file;
        }

        void addAsRootFile(String rootName, ANBFile file) {
            // Expectations
            context.checking( new Expectations(){{
                allowing(fileSystem).getFileForString(rootName);
                will( returnValue(file));
            }});
        }

        public void addChild(ANBFile parent, ANBFile child) {
            childrenOfFile.get(parent).add(child);
            parentOfFile.put(child,parent);
        }


    }


    private SimpleFileSystemDetails setupEmptyFileSystem(String rootName) {
        SimpleFileSystemDetails fs = new SimpleFileSystemDetails(context);
        ANBFile root = fs.createDirectory("root");
        fs.addAsRootFile(rootName, root);
        return fs;
    }

    private SimpleFileSystemDetails setupOneDeepFS(String rootName) {

        SimpleFileSystemDetails fs = new SimpleFileSystemDetails(context);

        // Setup
        ANBFile root = fs.createDirectory("root");
        fs.addAsRootFile(rootName,root);
        ANBFile aFile = fs.createDirectory("a_type");
        ANBFile bFile = fs.createDirectory("b_type");

        fs.addChild(root, aFile);
        fs.addChild(root, bFile);

        context.checking( new Expectations(){{
            allowing(aFile).relative_path(root);
            will(returnValue(asList("a_type")));
            allowing(bFile).relative_path(root);
            will(returnValue(asList("b_type")));
            }});

            return fs;
    }

    private SimpleFileSystemDetails setupDefaultFS(String rootName) {

        SimpleFileSystemDetails fs = new SimpleFileSystemDetails(context);

        // Setup
        ANBFile root = fs.createDirectory("root");
        fs.addAsRootFile(rootName,root);
        ANBFile characterDir = fs.createDirectory("character");
        ANBFile locationDir = fs.createDirectory("location");

        ANBFile an_entity = fs.createFile("an_entity");
        ANBFile another_entity = fs.createFile("another_entity");

        fs.addChild(root, characterDir);
        fs.addChild(root, locationDir);

        fs.addChild(characterDir,an_entity);
        fs.addChild(characterDir,another_entity);

        context.checking( new Expectations(){{
            allowing(characterDir).relative_path(root);
            will(returnValue(asList("character")));
            allowing(locationDir).relative_path(root);
            will(returnValue(asList("location")));

            allowing(an_entity).relative_path(root);
            will(returnValue(asList("character","an_entity")));
            allowing(another_entity).relative_path(root);
            will(returnValue(asList("character","another_entity")));
        }});

        return fs;
    }


    private SimpleFileSystemDetails setupFSWithDupeName(String rootName) {
        SimpleFileSystemDetails fs = new SimpleFileSystemDetails(context);
        ANBFile root = fs.createDirectory("root");
        fs.addAsRootFile(rootName,root);

        ANBFile aDir = fs.createDirectory("character");
        ANBFile bDir = fs.createDirectory("location");

        ANBFile aDupe = fs.createDirectory("duped_entity_type", "a_duped_entity_type");
        ANBFile bDupe = fs.createDirectory("duped_entity_type", "b_duped_entity_type");

        fs.addChild(root,aDir);
        fs.addChild(root,bDir);
        fs.addChild(aDir,aDupe);
        fs.addChild(bDir,bDupe);

        context.checking( new Expectations(){{
            allowing(root).relative_path(root);  will(returnValue(asList()));
            allowing(aDir).relative_path(root);  will(returnValue(asList("character")));
            allowing(bDir).relative_path(root);  will(returnValue(asList("location")));
            allowing(aDupe).relative_path(root);  will(returnValue(asList("character","duped_entity_type")));
            allowing(bDupe).relative_path(root);  will(returnValue(asList("location","duped_entity_type")));
        }});

        return fs;
    }


    private SimpleFileSystemDetails setupFSWithTwoRootsDupeName(String root1Name, String root2Name) {
        SimpleFileSystemDetails fs = new SimpleFileSystemDetails(context);
        ANBFile root1 = fs.createDirectory("root1");
        fs.addAsRootFile(root1Name,root1);
        ANBFile root2 = fs.createDirectory("root2");
        fs.addAsRootFile(root2Name,root2);

        ANBFile character1Dir = fs.createDirectory("character" ,"character in root1");
        ANBFile dupe1 = fs.createDirectory("duped_entity_type", "duped_entity_type_in_root1");

        ANBFile character2Dir = fs.createDirectory("character" ,"character in root2");
        ANBFile dupe2 = fs.createDirectory("duped_entity_type", "duped_entity_type_in_root2");

        fs.addChild(root1,character1Dir);
        fs.addChild(character1Dir,dupe1);

        fs.addChild(root2,character2Dir);
        fs.addChild(character2Dir,dupe2);

        context.checking( new Expectations(){{
            allowing(root1).relative_path(root1);  will(returnValue(asList()));
            allowing(character1Dir).relative_path(root1);  will(returnValue(asList("character")));
            allowing(dupe1).relative_path(root1);  will(returnValue(asList("character","duped_entity_type")));

            allowing(root2).relative_path(root2);  will(returnValue(asList()));
            allowing(character2Dir).relative_path(root2);  will(returnValue(asList("character")));
            allowing(dupe2).relative_path(root2);  will(returnValue(asList("character","duped_entity_type")));

            allowing(dupe1).pathAsString();
            will(returnValue(root1Name+"/character/duped_entity_type"));

            allowing(dupe2).pathAsString();
            will(returnValue(root2Name+"/character/duped_entity_type"));
        }});

        return fs;
    }


    @Test public void testCanGetEntityTypesWhenEmpty() {

        // Setup
        SimpleFileSystemDetails fs = setupEmptyFileSystem("/nowhere/dummyData");
        EntitySource entitySourceImpl = new EntitySource(fs.fileSystem);

        // Run
        entitySourceImpl.addRoot("/nowhere/dummyData");
        IEntitySource entitySource =  entitySourceImpl;
        final List<IEntitySource.EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(1));
    }

    @Test public void testCanGetEntityTypesOneLevelDeep() {

        // Setup
        SimpleFileSystemDetails fs = setupOneDeepFS("/nowhere/dummyData");
        EntitySource entitySourceImpl = new EntitySource(fs.fileSystem);

        // Run
        entitySourceImpl.addRoot("/nowhere/dummyData");
        IEntitySource entitySource =  entitySourceImpl;
        final List<IEntitySource.EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(3));

        assertThat(entityTypes.get(0).components(), is(equalTo(asList())));
        assertThat(entityTypes.get(1).components(), is(equalTo(asList("a_type"))));
        assertThat(entityTypes.get(2).components(), is(equalTo(asList("b_type"))));

    }

    @Test public void testCanGetEntityTypesTwoLevelsDeep() {

        // Setup
        ANBFileSystem fileSystem = context.mock(ANBFileSystem.class);
        ANBFile rootFile = context.mock(ANBFile.class, "rootFile");
        ANBFile fileA = context.mock(ANBFile.class, "a");
        ANBFile fileAB = context.mock(ANBFile.class, "a.b");

        EntitySource entitySourceImpl = new EntitySource(fileSystem);

        // Expectations
        context.checking( new Expectations(){{
            oneOf(fileSystem).getFileForString("/nowhere/dummyData");
            will( returnValue(rootFile));
            oneOf(fileSystem).getAllFiles(rootFile);
            will( returnValue(asList(fileA)));

            oneOf(fileSystem).getAllFiles(fileA);
            will( returnValue(asList(fileAB)));

            oneOf(fileSystem).getAllFiles(fileAB);
            will( returnValue(asList()));

            oneOf(fileA).isDirectory(); will(returnValue(true));
            allowing(fileA).relative_path(rootFile) ; will(returnValue(asList("a")));
            oneOf(fileAB).isDirectory(); will(returnValue(true));
            allowing(fileAB).relative_path(rootFile) ; will(returnValue(asList("a", "b")));
        }});

        // Run
        entitySourceImpl.addRoot("/nowhere/dummyData");
        IEntitySource entitySource =  entitySourceImpl;
        final List<IEntitySource.EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(3));

        assertThat(entityTypes.get(0).components(), is(equalTo(asList())));
        assertThat(entityTypes.get(1).components(), is(equalTo(asList("a"))));
        assertThat(entityTypes.get(2).components(), is(equalTo(asList("a", "b"))));

    }

    /**
     * Same entity type referfenced from two different roots.
     */
    @Test public void testThatRepeatedEntityTypesDoNotGetAddedMultipleTimes() {
        final SimpleFileSystemDetails defaultFS = setupFSWithTwoRootsDupeName("/nowhere/dummyData", "/donkey/food");
        EntitySource entitySource = new EntitySource(defaultFS.fileSystem);
        entitySource.addRoot("/nowhere/dummyData");
        entitySource.addRoot("/donkey/food");

        IEntityDB.EntityID id= IEntityDB.EntityID.fromComponents("character", "duped_entity_type");
        final IEntitySource.EntityType entityType = entitySource.getEntityTypeByID(id);
        assertThat(entityType, is(notNullValue()));
        assertThat(entityType.shortName(), is(equalTo("duped_entity_type")));
        assertThat(entityType.components(), is(equalTo(asList("character", "duped_entity_type"))));

        final List<ANBFile> sourceLocations = entityType.getSourceLocations();
        assertThat(sourceLocations,is(notNullValue()));
        List<String> paths = sourceLocations.stream()
                .map(sourceLocation -> sourceLocation.pathAsString())
                .collect(Collectors.toList());
        Collections.sort(paths);
        assertThat(paths, is(equalTo(asList(
                "/donkey/food/character/duped_entity_type",
                "/nowhere/dummyData/character/duped_entity_type"))));
    }



    // Later we're going to need to create new entities, which means we need to be able to
    // determine where they write to.
    @Test
    public void testThatEntityTypesHangOnToTheirOriginalFiles() throws Exception {
        ANBFile file = context.mock(ANBFile.class);

        IEntitySource.EntityType type = new IEntitySource.EntityType(IEntityDB.EntityID.fromComponents("hello", "world"));
        type.addPath(file);

        final List<ANBFile> sourceLocations = type.getSourceLocations();
        assertThat(sourceLocations, is(notNullValue()));
        assertThat(sourceLocations.size(), is(1));
        assertThat(sourceLocations.get(0), is(file));
    }

    @Test
    public void testGetEntityByID() throws Exception {
        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        EntitySource entitySource = new EntitySource(defaultFS.fileSystem);
        entitySource.addRoot("/nowhere/dummyData");

        IEntityDB.EntityID idA = (new IEntityDB.EntityID()).child("character").child("an_entity");
        IEntityDB.EntityID idB = (new IEntityDB.EntityID()).child("character").child("another_entity");
        IEntityDB.EntityID idC = (new IEntityDB.EntityID()).child("character").child("no_such_entity");

        final IEntityDB.ICharacterBlock entityA = entitySource.getEntityById(idA);
        final IEntityDB.ICharacterBlock entityB = entitySource.getEntityById(idB);
        final IEntityDB.ICharacterBlock entityC = entitySource.getEntityById(idC);

        assertThat(entityC, is(nullValue()));
        assertThat(entityA, is(notNullValue()));
        assertThat(entityB, is(notNullValue()));


    }

    @Test
    public void testGetEntityTypeByID() throws Exception {
        final SimpleFileSystemDetails defaultFS = setupFSWithDupeName("/nowhere/dummyData");
        EntitySource entitySource = new EntitySource(defaultFS.fileSystem);
        entitySource.addRoot("/nowhere/dummyData");

        IEntityDB.EntityID idA = (new IEntityDB.EntityID()).child("character").child("duped_entity_type");
        IEntityDB.EntityID idB = (new IEntityDB.EntityID()).child("location").child("duped_entity_type");


        final IEntitySource.EntityType dupedEntityA = entitySource.getEntityTypeByID(idA);
        final IEntitySource.EntityType dupedEntityB = entitySource.getEntityTypeByID(idB);

        assertThat(dupedEntityA, is(notNullValue()));
        assertThat(dupedEntityB, is(notNullValue()));

        assertThat(dupedEntityA.fullName(), is(equalTo("character.duped_entity_type")));
        assertThat(dupedEntityB.fullName(), is(equalTo("location.duped_entity_type")));
    }

    @Test
    public void testGetEntityTypeByName() {
        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        EntitySource entitySource = new EntitySource(defaultFS.fileSystem);
        entitySource.addRoot("/nowhere/dummyData");

        final IEntitySource.EntityType characterEntityType = entitySource.getEntityTypeByShortName("character");
        assertThat(characterEntityType, is(notNullValue()));

        assertThat(characterEntityType.fullName(),is(equalTo("character")));
        assertThat(characterEntityType.components(), is(equalTo(asList("character"))));
    }

    @Test
    public void testGetEntityTypeByName_noSuchEntityType() {
        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        EntitySource entitySource = new EntitySource(defaultFS.fileSystem);
        entitySource.addRoot("/nowhere/dummyData");

        final IEntitySource.EntityType noSuchEntity = entitySource.getEntityTypeByShortName("no_such_entity");
        assertThat(noSuchEntity, is(nullValue()));
    }

    @Test
    public void testGetEntityTypeByName_multipleEntitiesWithSameName() {
        final SimpleFileSystemDetails defaultFS = setupFSWithDupeName("/nowhere/dummyData");
        EntitySource entitySource = new EntitySource(defaultFS.fileSystem);
        entitySource.addRoot("/nowhere/dummyData");

        final IEntitySource.EntityType dupedEntity = entitySource.getEntityTypeByShortName("duped_entity_type");
        assertThat(dupedEntity, is(notNullValue()));
        assertThat(dupedEntity.shortName(), is(equalTo("duped_entity_type")));

        List<String> opt1 = asList("character", "duped_entity_type");
        List<String> opt2 = asList("location", "duped_entity_type");
        assertThat(dupedEntity.components(), anyOf(is(equalTo(opt1)), is(equalTo(opt2))));
    }

    @Test
    public void testGetEntityTypeByName_onlyReturnsDirectories() {
        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        EntitySource entitySource = new EntitySource(defaultFS.fileSystem);
        entitySource.addRoot("/nowhere/dummyData");

        final IEntitySource.EntityType noSuchEntity = entitySource.getEntityTypeByShortName("a_character");
        assertThat(noSuchEntity, is(nullValue()));
    }

    @Test
    public void testGetEntityByName_onlyReturnsFiles() {
        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        EntitySource entitySource = new EntitySource(defaultFS.fileSystem);
        entitySource.addRoot("/nowhere/dummyData");

        final IEntityDB.ICharacterBlock aCharacter = entitySource.getEntityByName("an_entity");
        assertThat(aCharacter, is(notNullValue()));

        final IEntityDB.ICharacterBlock character = entitySource.getEntityByName("character");
        assertThat(character, is(nullValue()));
    }



    @Test
    public void testCreateNewEntity() throws Exception {

        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");

        ANBFileSystem fileSystem = defaultFS.fileSystem;
        EntitySource entitySource = new EntitySource(fileSystem);
        entitySource.addRoot("/nowhere/dummyData");

        //TODO: Each of these bits needs its own tests.
        final IEntitySource.EntityType characterEntityType = entitySource.getEntityTypeByShortName("character");
        assertThat(characterEntityType, is(notNullValue()));

        ANBFile some_character = defaultFS.createFile("some_character");
        context.checking(new Expectations() {{
            oneOf(defaultFS.file("character")).isWritable();
            will(returnValue(true));
            oneOf(defaultFS.file("character")).child("some_character");
            will(returnValue(some_character));
            oneOf(some_character).getFS();
            will(returnValue(fileSystem));
        }});

        IEntityDB.ICharacterBlock entity = entitySource.createNewEntityOfType(characterEntityType,"some_character");
        assertThat(entity, is(notNullValue()));

        context.checking(new Expectations() {{
            oneOf(fileSystem).writeFileContents(entity.getFile(), "This is a test".getBytes());
            }});

        entity.saveContents("This is a test".getBytes());
    }
}
