package janb.models;

import janb.project.ProjectDB;
import janb.util.ANBFile;
import janb.util.ANBFileSystem;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by michaelanderson on 27/02/2015.
 */
public class EntitySourceTest {

    @Rule public JUnitRuleMockery context = new JUnitRuleMockery();

    public static abstract class DummyANBFileBase implements ANBFile {
        protected final ANBFileSystem fs;
        protected final List<String> absolute_path;
        protected final boolean isWritable;

        // Hide this away a little?
        public byte[] content = null;

        public DummyANBFileBase(ANBFileSystem fs, List<String> absolute_path, boolean isWritable) {
            this.fs = fs;
            this.absolute_path = absolute_path;
            this.isWritable = isWritable;
        }

        @Override
        public List<String> relative_path(ANBFile root) {
            final DummyANBFileBase rootAsDummy = (DummyANBFileBase) root;
            if(rootAsDummy.absolute_path.size() > absolute_path.size())
                throw new RuntimeException("Not a child path!");
            ArrayList<String> result = new ArrayList<>();
            for(int i=0; i<absolute_path.size(); ++i) {
                if(i<rootAsDummy.absolute_path.size()) {
                    if(rootAsDummy.absolute_path.get(i).equals(absolute_path.get(i)))
                        continue;
                    throw new RuntimeException("Not a child path!");
                } else {
                    result.add(absolute_path.get(i));
                }
            }

            return result;
        }

        @Override
        public ANBFileSystem getFS() {
            return fs;
        }

        @Override
        public boolean isWritable() {
            return isWritable;
        }

        @Override
        public String pathAsString() {
            return String.join("/",absolute_path);
        }

        @Override
        public String getName() {
            return absolute_path.get(absolute_path.size()-1);
        }

        @Override
        public byte[] readContents() throws IOException {
            return content;
        }
    }

    public static class DummyANBFileDirectory extends DummyANBFileBase {

        private final Map<String,ANBFile> children;

        public DummyANBFileDirectory(ANBFileSystem fs, List<String> absolute_path, boolean isWritable, Map<String, ANBFile> children) {
            super(fs, absolute_path, isWritable);
            this.children = children;
        }

        public DummyANBFileDirectory(ANBFileSystem fs, List<String> absolute_path) {
            this(fs, absolute_path, true, new HashMap<>());
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        @Override
        public ANBFile child(String name) {
            return children.get(name);
        }



        @Override
        public List<ANBFile> getAllFiles() {
            return Collections.unmodifiableList(new ArrayList<>(children.values()));
        }
    }

    public static class DummyANBFileNormal extends DummyANBFileBase {

        public DummyANBFileNormal(ANBFileSystem fs, List<String> absolute_path, boolean isWritable) {
            super(fs, absolute_path, isWritable);
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public ANBFile child(String name) {
            return null;
        }

        @Override
        public List<ANBFile> getAllFiles() {
            return Collections.EMPTY_LIST;
        }
    }

    private static class DummyFileSystemDetails {
        private ANBFileSystem fileSystem;

        private final JUnitRuleMockery context;

        private DummyFileSystemDetails(JUnitRuleMockery context) {
            this.context = context;
            fileSystem = context.mock(ANBFileSystem.class);
        }
    }

    @Deprecated
    private static class SimpleFileSystemDetails {
        private ANBFileSystem fileSystem;
        private Map<String, ANBFile> files = new HashMap<>();
        private Map<ANBFile, List<ANBFile>> childrenOfFile = new HashMap<>();

        private final JUnitRuleMockery context;

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
                allowing(file).getAllFiles();
                will( returnValue(children));

                allowing(file).isDirectory();
                will(returnValue(true));

                allowing(file).child("_type");
                will(returnValue(null));

                allowing(file).getName();
                will(returnValue(name));

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
        }


    }

    @Deprecated
    private SimpleFileSystemDetails setupEmptyFileSystem(String rootName) {
        SimpleFileSystemDetails fs = new SimpleFileSystemDetails(context);
        ANBFile root = fs.createDirectory("root");
        fs.addAsRootFile(rootName, root);
        return fs;
    }

    @Deprecated
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

    private DummyFileSystemDetails setupOneDeepFS_newScheme(String rootName) {

        DummyFileSystemDetails fs = new DummyFileSystemDetails(context);

        DummyANBFileDirectory root = new DummyANBFileDirectory(fs.fileSystem, asList("root") );

        DummyANBFileDirectory a = new DummyANBFileDirectory(fs.fileSystem, asList("root","a"));
        root.children.put("a", a);

        DummyANBFileDirectory b = new DummyANBFileDirectory(fs.fileSystem, asList("root","b"));
        root.children.put("b",b);

        DummyANBFileNormal a_type = new DummyANBFileNormal(fs.fileSystem, asList("root","a","_type"), true);
        a_type.content = "collection".getBytes(StandardCharsets.UTF_8);
        b.children.put("_type",a_type);

        DummyANBFileNormal b_type = new DummyANBFileNormal(fs.fileSystem, asList("root", "b", "_type"), true);
        b.children.put("_type",b_type);
        b_type.content = "collection".getBytes(StandardCharsets.UTF_8);



        context.checking( new Expectations(){{
            allowing(fs.fileSystem).getFileForString(rootName);
            will(returnValue(root));
        }});

        return fs;
    }

    private DummyFileSystemDetails setupTwoDeepFS_newScheme(String s) {
        DummyFileSystemDetails fs = new DummyFileSystemDetails(context);
        DummyANBFileDirectory root = new DummyANBFileDirectory(fs.fileSystem, asList("root") );

        context.checking( new Expectations(){{
            allowing(fs.fileSystem).getFileForString(s);
            will(returnValue(root));
        }});

        DummyANBFileDirectory a = new DummyANBFileDirectory(fs.fileSystem, asList("root","a"));
        root.children.put("a", a);

        DummyANBFileDirectory b = new DummyANBFileDirectory(fs.fileSystem, asList("root","a", "b"));
        a.children.put("b", b);


        return fs;
    }

    private DummyFileSystemDetails setupEmptyFS_newScheme(String rootName) {

        DummyFileSystemDetails fs = new DummyFileSystemDetails(context);

        DummyANBFileDirectory root = new DummyANBFileDirectory(fs.fileSystem, asList("root") );

        context.checking( new Expectations(){{
            allowing(fs.fileSystem).getFileForString(rootName);
            will(returnValue(root));
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

    public static class DummyProject implements ANBProject {

        public DummyProject(ANBFileSystem fileSystem, String path) {

        }

        @Override
        public boolean tryUpdate(ProjectDB.DBField entity) {
            return false;
        }

        @Override
        public boolean trySave(ProjectDB.DBField entity) {
            return false;
        }

        @Override
        public ProjectDB.ConstDBField getEntityById(EntityID id) {
            return null;
        }

        @Override
        public List<ProjectDB.ConstDBField> getEntities() {
            return Collections.EMPTY_LIST;

        }
    }


    @Test public void testCanGetEntityTypesWhenEmpty() {

        // Setup
        SimpleFileSystemDetails fs = setupEmptyFileSystem("/nowhere/dummyData");
        DummyProject project = new DummyProject(fs.fileSystem, "/nowhere/dummyData");
        EntitySource entitySourceImpl = new EntitySource();

        // Run
        entitySourceImpl.addProject(project);
        IEntitySource entitySource =  entitySourceImpl;
        final List<EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(1));
        assertThat(entityTypes.get(0).id(), is(equalTo(EntityID.fromComponents())));
    }

    /**
     * In the new scheme an EntityType is just an Entity of type CollectionEntity
     * in particular any collection tagged with type = collection:!category
     *
     * @TODO: This now seems identical to the test above.
     */
    @Test public void testCanGetEntityCategoriesWhenEmpty_newScheme() {

        // Setup
        DummyFileSystemDetails fs = setupEmptyFS_newScheme("/nowhere/dummyData");
        DummyProject project = new DummyProject(fs.fileSystem, "/nowhere/dummyData");

        EntitySource entitySourceImpl = new EntitySource();

        // Run
        entitySourceImpl.addProject(project);
        IEntitySource entitySource =  entitySourceImpl;

        final List<EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(1));

        final EntityType root = entityTypes.get(0);
        assertThat(root.id(), is(equalTo(EntityID.fromComponents())));
    }

    @Test public void testCanGetEntityTypesOneLevelDeep() {

        // Setup
        SimpleFileSystemDetails fs = setupOneDeepFS("/nowhere/dummyData");
        DummyProject project = new DummyProject(fs.fileSystem, "/nowhere/dummyData");
        EntitySource entitySourceImpl = new EntitySource();

        // Run
        entitySourceImpl.addProject(project);
        IEntitySource entitySource =  entitySourceImpl;
        final List<EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(3));

        assertThat(entityTypes.get(0).id().components(), is(equalTo(asList())));
        assertThat(entityTypes.get(1).id().components(), is(equalTo(asList("a_type"))));
        assertThat(entityTypes.get(2).id().components(), is(equalTo(asList("b_type"))));

    }

    @Test public void testEntitySourceAddsEntitiesFromProjects() {
        ANBProject projectA = context.mock(ANBProject.class,"projectA");
        ANBProject projectB = context.mock(ANBProject.class,"projectB");
        EntityMapper mapper = context.mock(EntityMapper.class);


        ProjectDB.ConstDBField e1 = context.mock(ProjectDB.ConstDBField.class,"e1");
        ProjectDB.ConstDBField e2 = context.mock(ProjectDB.ConstDBField.class,"e2");
        ArrayList<ProjectDB.ConstDBField> entitiesInA = new ArrayList<>();
        entitiesInA.add(e1);
        ArrayList<ProjectDB.ConstDBField> entitiesInB = new ArrayList<>();
        entitiesInB.add(e2);

        Entity ee1 = context.mock(Entity.class,"ee1");
        Entity ee2 = context.mock(Entity.class,"ee2");

        EntitySource source = new EntitySource();
        source.setEntityMapper(mapper);

        context.checking( new Expectations(){{
                oneOf(projectA).getEntities();
                will(returnValue(entitiesInA));

                oneOf(projectB).getEntities();
                will(returnValue(entitiesInB));

                oneOf(mapper).mapToEntity(e1);
                will(returnValue(ee1));

                oneOf(mapper).mapToEntity(e2);
                will(returnValue(ee2));
            }});

        source.addProject(projectA);
        source.addProject(projectB);

        context.assertIsSatisfied();

        assertThat(new HashSet<>(source.getAllEntitiesOfType(null)), is(equalTo(new HashSet<>(asList(ee1, ee2)))));
    }

    @Test public void testEntitySourceGetsDefaultMapper() {
        EntitySource source = new EntitySource();
        assertThat(source.getEntityMapper(), is(instanceOf(DefaultEntityMapper.class)));
    }

    @Test public void testDefaultEntityMapperWorks() {
        fail("Not yet implemented");
    }

    @Test public void testCanGetEntityCategoriesOneLevelDeep_newScheme() {

        // Setup
        DummyFileSystemDetails fs = setupOneDeepFS_newScheme("/nowhere/dummyData");
        DummyProject project = new DummyProject(fs.fileSystem, "/nowhere/dummyData");
        EntitySource entitySourceImpl = new EntitySource();

        // Run
        entitySourceImpl.addProject(project);
        IEntitySource entitySource =  entitySourceImpl;
        final List<EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(3));

        final EntityType root = entityTypes.get(0);
        final EntityType entity1 = entityTypes.get(1);
        final EntityType entity2 = entityTypes.get(2);

        assertThat(root.id(), is(equalTo(EntityID.fromComponents())));
        assertThat(entity1.id(), is(equalTo(EntityID.fromComponents("a"))));
        assertThat(entity2.id(), is(equalTo(EntityID.fromComponents("b"))));

        assertThat(root, is(instanceOf(ProjectDB.ConstCollectionField.class)));
        assertThat(entity1, is(instanceOf(ProjectDB.ConstCollectionField.class)));
        assertThat(entity2, is(instanceOf(ProjectDB.ConstCollectionField.class)));
    }

    @Test public void testCanGetEntityTypesTwoLevelsDeep() {

        // Setup
        ANBFileSystem fileSystem = context.mock(ANBFileSystem.class);
        ANBFile rootFile = context.mock(ANBFile.class, "rootFile");
        ANBFile fileA = context.mock(ANBFile.class, "a");
        ANBFile fileAB = context.mock(ANBFile.class, "a.b");

        // Expectations
        context.checking( new Expectations(){{
            oneOf(fileSystem).getFileForString("/nowhere/dummyData");
            will( returnValue(rootFile));
            allowing(rootFile).getAllFiles();
            will( returnValue(asList(fileA)));
            allowing(rootFile).isDirectory();
            will(returnValue(true));
            allowing(rootFile).child("_type");
            will(returnValue(null));


            allowing(fileA).getAllFiles();
            will( returnValue(asList(fileAB)));

            allowing(fileAB).getAllFiles();
            will( returnValue(asList()));

            allowing(fileA).isDirectory(); will(returnValue(true));
            allowing(fileA).relative_path(rootFile) ; will(returnValue(asList("a")));
            allowing(fileA).getName(); will(returnValue("a"));
            allowing(fileA).child("_type"); will(returnValue(null));

            allowing(fileAB).isDirectory(); will(returnValue(true));
            allowing(fileAB).relative_path(rootFile) ; will(returnValue(asList("a", "b")));
            allowing(fileAB).getName(); will(returnValue("b"));
            allowing(fileAB).child("_type"); will(returnValue(null));
        }});



        // Run
        DummyProject project = new DummyProject(fileSystem, "/nowhere/dummyData");
        EntitySource entitySourceImpl = new EntitySource();
        entitySourceImpl.addProject(project);



        // Run
        IEntitySource entitySource =  entitySourceImpl;
        final List<EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(3));

        assertThat(entityTypes.get(0).id().components(), is(equalTo(asList())));
        assertThat(entityTypes.get(1).id().components(), is(equalTo(asList("a"))));
        assertThat(entityTypes.get(2).id().components(), is(equalTo(asList("a", "b"))));
    }

    @Test public void testCanGetEntityTypesTwoLevelsDeep_newScheme() {

        // Setup
        DummyFileSystemDetails fs = setupTwoDeepFS_newScheme("/nowhere/dummyData");

        DummyProject project = new DummyProject(fs.fileSystem, "/nowhere/dummyData");
        EntitySource entitySourceImpl = new EntitySource();

        // Run
        entitySourceImpl.addProject(project);
        IEntitySource entitySource =  entitySourceImpl;
        final List<EntityType> entityTypes = entitySource.getEntityTypes();

        // Check
        assertThat(entityTypes, is(notNullValue()));
        assertThat(entityTypes.size(), is(3));

        assertThat(entityTypes.get(0).id(), is(EntityID.fromComponents()));
        assertThat(entityTypes.get(1).id(), is(EntityID.fromComponents("a")));
        assertThat(entityTypes.get(2).id(), is(equalTo(EntityID.fromComponents("a", "b"))));

    }



    /**
     * Same entity type referfenced from two different roots.
     */
    @Test public void testThatRepeatedEntityTypesDoNotGetAddedMultipleTimes() {
        final SimpleFileSystemDetails defaultFS = setupFSWithTwoRootsDupeName("/nowhere/dummyData", "/donkey/food");
        DummyProject projectA = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        DummyProject projectB = new DummyProject(defaultFS.fileSystem, "/donkey/food");


        EntitySource entitySource = new EntitySource();
        entitySource.addProject(projectA);
        entitySource.addProject(projectB);

        EntityID id= EntityID.fromComponents("character", "duped_entity_type");
        final EntityType entityType = entitySource.getEntityTypeByID(id);
        assertThat(entityType, is(notNullValue()));
        assertThat(entityType.id().shortName(), is(equalTo("duped_entity_type")));
        assertThat(entityType.id().components(), is(equalTo(asList("character", "duped_entity_type"))));

        fail("NYI - removed stuff from here that hasn't been replaced - not sure what should be here now...");
    }



    // Later we're going to need to create new entities, which means we need to be able to
    // determine where they write to.
    @Test
    public void testThatEntityTypesHangOnToTheirOriginalFiles() throws Exception {
        ANBProject project = context.mock(ANBProject.class);

        EntityType type = new EntityType(EntityID.fromComponents("hello", "world"));
        type.addSourceProject(project);

        final List<ANBProject> sourceLocations = type.getProjects();
        assertThat(sourceLocations, is(notNullValue()));
        assertThat(sourceLocations.size(), is(1));
        assertThat(sourceLocations.get(0), is(project));
    }

    ANBProject getDefaultProject() {
//        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
//        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
//        return project;

        ANBProject project = context.mock((ANBProject.class));
        final List<ProjectDB.ConstDBField> entities = new ArrayList<>();
        ProjectDB.ConstDBField e1 = context.mock(ProjectDB.ConstDBField.class,"character.an_entity");
        ProjectDB.ConstDBField e2 = context.mock(ProjectDB.ConstDBField.class,"character.another_entity");
        entities.add(e1);
        entities.add(e2);

        context.checking(new Expectations() {{
            allowing(e1).getLocation(); will(returnValue(EntityID.fromComponents("character","an_entity")));
            allowing(e2).getLocation(); will(returnValue(EntityID.fromComponents("character","another_entity")));
            allowing(project).getEntities();
            will(returnValue(entities));
        }});

        return project;
    }


    @Test
    public void testGetEntityByID() throws Exception {
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(getDefaultProject());

        EntityID idA = (new EntityID()).child("character").child("an_entity");
        EntityID idB = (new EntityID()).child("character").child("another_entity");
        EntityID idC = (new EntityID()).child("character").child("no_such_entity");

        final Entity entityA = entitySource.getEntityById(idA);
        final Entity entityB = entitySource.getEntityById(idB);
        final Entity entityC = entitySource.getEntityById(idC);

        context.assertIsSatisfied();

        assertThat(entityC, is(nullValue()));
        assertThat(entityA, is(notNullValue()));
        assertThat(entityB, is(notNullValue()));
    }

    @Test
    public void testGetEntityTypeByID() throws Exception {
        final SimpleFileSystemDetails defaultFS = setupFSWithDupeName("/nowhere/dummyData");
        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);

        EntityID idA = (new EntityID()).child("character").child("duped_entity_type");
        EntityID idB = (new EntityID()).child("location").child("duped_entity_type");


        final EntityType dupedEntityA = entitySource.getEntityTypeByID(idA);
        final EntityType dupedEntityB = entitySource.getEntityTypeByID(idB);

        assertThat(dupedEntityA, is(notNullValue()));
        assertThat(dupedEntityB, is(notNullValue()));

        assertThat(dupedEntityA.id().asString(), is(equalTo("character.duped_entity_type")));
        assertThat(dupedEntityB.id().asString(), is(equalTo("location.duped_entity_type")));
    }

    @Test
    public void testGetEntityTypeByName() {
        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        final EntityType characterEntityType = entitySource.getEntityTypeByShortName("character");
        assertThat(characterEntityType, is(notNullValue()));

        assertThat(characterEntityType.id().asString(),is(equalTo("character")));
        assertThat(characterEntityType.id().components(), is(equalTo(asList("character"))));
    }

    @Test
    public void testGetEntityTypeByName_noSuchEntityType() {
        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        final EntityType noSuchEntity = entitySource.getEntityTypeByShortName("no_such_entity");
        assertThat(noSuchEntity, is(nullValue()));
    }

    @Test
    public void testGetEntityTypeByName_multipleEntitiesWithSameName() {
final SimpleFileSystemDetails defaultFS = setupFSWithDupeName("/nowhere/dummyData");
        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        final EntityType dupedEntity = entitySource.getEntityTypeByShortName("duped_entity_type");
        assertThat(dupedEntity, is(notNullValue()));
        assertThat(dupedEntity.id().shortName(), is(equalTo("duped_entity_type")));

        List<String> opt1 = asList("character", "duped_entity_type");
        List<String> opt2 = asList("location", "duped_entity_type");
        assertThat(dupedEntity.id().components(), anyOf(is(equalTo(opt1)), is(equalTo(opt2))));
    }

    @Test
    public void testGetEntityTypeByName_onlyReturnsDirectories() {
        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        final EntityType noSuchEntity = entitySource.getEntityTypeByShortName("a_character");
        assertThat(noSuchEntity, is(nullValue()));
    }

    @Test
    public void testGetEntityByName_onlyReturnsFiles() {
         final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        final Entity aCharacter = entitySource.getEntityByName("an_entity");
        assertThat(aCharacter, is(notNullValue()));

        final Entity character = entitySource.getEntityByName("character");
        assertThat(character, is(nullValue()));
    }



    @Test
    public void testCreateNewEntity() throws Exception {

        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");
        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        //TODO: Each of these bits needs its own tests.
        final EntityType characterEntityType = entitySource.getEntityTypeByShortName("character");
        assertThat(characterEntityType, is(notNullValue()));

        ANBFile some_character = defaultFS.createFile("some_character");
        context.checking(new Expectations() {{
            oneOf(defaultFS.file("character")).isWritable();
            will(returnValue(true));
            oneOf(defaultFS.file("character")).child("some_character");
            will(returnValue(some_character));
            oneOf(some_character).getFS();
            will(returnValue(defaultFS.fileSystem));
        }});

        fail("NYI - only partially implemented");
//        Entity entity = entitySource.createNewEntityOfType(characterEntityType,"some_character");
//        assertThat(entity, is(notNullValue()));
//
//        context.checking(new Expectations() {{
//            oneOf(defaultFS.fileSystem).writeFileContents(entity.getFile(), "This is a test".getBytes());
//            }});
//
//        entity.saveContents("This is a test".getBytes());
    }
    
    @Test
    public void testThatCreatingAnEntityGeneratesAnEvent() throws Exception {

        final SimpleFileSystemDetails defaultFS = setupDefaultFS("/nowhere/dummyData");

        DummyProject project = new DummyProject(defaultFS.fileSystem, "/nowhere/dummyData");
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);

        EntitySourceListener listener = context.mock(EntitySourceListener.class);
        entitySource.addListener(listener);

        //TODO: Each of these bits needs its own tests.
        final EntityType characterEntityType = entitySource.getEntityTypeByShortName("character");
        assertThat(characterEntityType, is(notNullValue()));

        ANBFile some_character = defaultFS.createFile("some_character");
        context.checking(new Expectations() {{
            allowing(defaultFS.file("character")).isWritable();
            will(returnValue(true));
            allowing(defaultFS.file("character")).child("some_character");
            will(returnValue(some_character));
            allowing(some_character).getFS();
            will(returnValue(defaultFS.fileSystem));
        }});

        context.checking(new Expectations() {{
            oneOf(listener).onAddEntity(with(aNonNull(Entity.class)));
        }});

//        Entity entity = entitySource.createNewEntityOfType(characterEntityType,"some_character");
//        assertThat(entity, is(notNullValue()));

        fail("NYI - only partially implemented");


    }

    @Test
    public void testAddingAnEntityTypeGeneratesAnEvent() throws Exception {
        fail("NYI");
    }
}
