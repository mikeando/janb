package janb.models;

import janb.mxl.IMxlFile;
import janb.mxl.MxlFile;
import janb.project.ProjectDB;
import janb.util.ANBFile;
import janb.util.dummy.DummyANBFileDirectory;
import janb.util.dummy.DummyANBFileNormal;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

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

    private ANBFile setupOneDeepFS_newScheme(String rootName) {

        DummyANBFileDirectory root = new DummyANBFileDirectory(null, asList("root") );

        DummyANBFileDirectory a = root.addChildDirectory("a");
        DummyANBFileDirectory b = root.addChildDirectory("b");

        DummyANBFileNormal a_type = a.addChildFile("_type");
        a_type.content = "collection".getBytes(StandardCharsets.UTF_8);

        DummyANBFileNormal b_type = b.addChildFile("_type");
        b_type.content = "collection".getBytes(StandardCharsets.UTF_8);

        return root;
    }

    private ANBFile setupTwoDeepFS_newScheme(String s) {
        DummyANBFileDirectory root = new DummyANBFileDirectory(null, asList("root") );
        DummyANBFileDirectory a = root.addChildDirectory("a");
        DummyANBFileDirectory b = a.addChildDirectory("b");
        return root;
    }

    private ANBFile setupEmptyFS_newScheme(String rootName) {
        DummyANBFileDirectory root = new DummyANBFileDirectory(null, asList("root") );
        return root;
    }



    private void setupFSWithDupeName(DummyANBFileDirectory root) {

        DummyANBFileDirectory characterDir = root.addChildDirectory("character");
        DummyANBFileDirectory locationDir = root.addChildDirectory("location");

        ANBFile aDupe = characterDir.addChildDirectory("duped_entity_type");
        ANBFile bDupe = locationDir.addChildDirectory("duped_entity_type");
    }

    public static class DummyProject implements ANBProject {

        private final Mockery context;

        public DummyProject(Mockery context) {
            this.context = context;
        }

        public boolean _tryUpdate = false;
        public boolean _trySave = false;

        List<ProjectDB.ConstDBField> entities = new ArrayList<>();
        List<MxlFile> files = new ArrayList<>();
        List<ProjectDB.ConstDBField> entityTypes = new ArrayList<>();
        List<ProjectDB.ConstDBField> prototypes = new ArrayList<>();


        @Override
        public boolean tryUpdate(ProjectDB.DBField entity) {
            return _tryUpdate;
        }

        @Override
        public boolean trySave(ProjectDB.DBField entity) {
            return _trySave;
        }

        @Override
        public ProjectDB.ConstDBField getEntityById(EntityID id) {
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
        public List<MxlFile> getFiles() {
            return files;
        }

        @Override
        public List<ProjectDB.ConstDBField> getEntityTypes() {
            return entityTypes;
        }

        @Override
        public void trySave(IMxlFile file, EntityID entityID) {
            throw new RuntimeException("DummyProject.trySave(IMxlFile, EntityID) not yet stubbed");
        }

        public ProjectDB.ConstDBField addMockEntity(String... path) {
            final EntityID entityID = EntityID.fromComponents(path);
            ProjectDB.ConstDBField e = context.mock(ProjectDB.ConstDBField.class, entityID.toString());
            context.checking(new Expectations() {{
                allowing(e).getLocation();
                will(returnValue(entityID));
            }});
            entities.add(e);
            return e;
        }

        public ProjectDB.ConstDBField addMockEntityType(String... path) {
            final EntityID entityID = EntityID.fromComponents(path);
            ProjectDB.ConstDBField e = context.mock(ProjectDB.ConstDBField.class, entityID.toString());
            context.checking(new Expectations() {{
                allowing(e).getLocation();
                will(returnValue(entityID));
            }});
            entityTypes.add(e);
            return e;
        }
    }


    @Test public void testCanGetEntityTypesWhenEmpty() {

        // Setup
        DummyProject project = new DummyProject(context);
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
        DummyProject project = new DummyProject(context);

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

        //Setup
        DummyProject project = new DummyProject(context);
        ProjectDB.ConstDBField e1 = project.addMockEntityType("a_type");
        ProjectDB.ConstDBField e2 = project.addMockEntityType("b_type");


        // Run
        EntitySource entitySourceImpl = new EntitySource();
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

    /**
     * Check that the EntitySource requests information from the
     * projects as they are added.
     *
     * @note Since we're directly testing the interaction of EntitySource
     * with ANBProject, it is more appropriate to use a mock here, than
     * a DummyProject like we do in most other tests.
     */
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
        ArrayList<ProjectDB.ConstDBField> entityTypesInA = new ArrayList<>();
        ProjectDB.ConstDBField et1 = context.mock(ProjectDB.ConstDBField.class,"et1");
        entityTypesInA.add(et1);
        ArrayList<ProjectDB.ConstDBField> entityTypesInB = new ArrayList<>();
        ProjectDB.ConstDBField et2 = context.mock(ProjectDB.ConstDBField.class,"et2");
        entityTypesInB.add(et2);

        Entity ee1 = context.mock(Entity.class,"ee1");
        Entity ee2 = context.mock(Entity.class,"ee2");

        EntityType eet1 = context.mock(EntityType.class, "eet1");


        EntitySource source = new EntitySource();
        source.setEntityMapper(mapper);

        context.checking(new Expectations() {{
            oneOf(projectA).getEntities();
            will(returnValue(entitiesInA));

            oneOf(projectB).getEntities();
            will(returnValue(entitiesInB));

            oneOf(projectA).getEntityTypes();
            will(returnValue(entityTypesInA));

            oneOf(projectB).getEntityTypes();
            will(returnValue(entityTypesInB));


            oneOf(mapper).mapToEntity(e1);
            will(returnValue(ee1));

            oneOf(mapper).mapToEntity(e2);
            will(returnValue(ee2));

            oneOf(mapper).mapToEntityType(et1);
            will(returnValue(eet1));

            oneOf(mapper).mapToEntityType(et2);
            will(returnValue(eet1));


        }});

        source.addProject(projectA);
        source.addProject(projectB);

        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            oneOf(ee1).getType();
            will(returnValue(eet1));
            oneOf(ee2).getType();
            will(returnValue(eet1));
        }});

        assertThat(new HashSet<>(source.getAllEntitiesOfType(eet1)), is(equalTo(new HashSet<>(asList(ee1, ee2)))));
    }

    @Test public void testEntitySourceGetsDefaultMapper() {
        EntitySource source = new EntitySource();
        assertThat(source.getEntityMapper(), is(instanceOf(DefaultEntityMapper.class)));
    }

    @Test public void testDefaultEntityMapperWorks() {
        fail("Not yet implemented");
    }

    /**
     * @TODO Should really push a version of this test down into the Projects
     */
    @Test public void testCanGetEntityCategoriesOneLevelDeep_newScheme() {

        // Setup
        DummyProject project = new DummyProject(context);
        final ProjectDB.ConstDBField type1 = context.mock(ProjectDB.ConstDBField.class, "type1");
        final ProjectDB.ConstDBField type2 = context.mock(ProjectDB.ConstDBField.class, "type2");
        // Expectations
        context.checking( new Expectations(){{
            allowing(type1).getLocation();
            will( returnValue(EntityID.fromComponents("a")));
            allowing(type2).getLocation();
            will( returnValue(EntityID.fromComponents("b")));
        }});

        project.entityTypes.add(type1);
        project.entityTypes.add(type2);

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

        assertThat(root, is(instanceOf(SimpleEntityType.class)));
        assertThat(entity1, is(instanceOf(SimpleEntityType.class)));
        assertThat(entity2, is(instanceOf(SimpleEntityType.class)));
    }

    @Test public void testCanGetEntityTypesTwoLevelsDeep() {

        //Setup
        DummyProject project = new DummyProject(context);
        ProjectDB.ConstDBField e1 = context.mock(ProjectDB.ConstDBField.class,"a");
        ProjectDB.ConstDBField e2 = context.mock(ProjectDB.ConstDBField.class,"a.b");
        project.entityTypes.add(e1);
        project.entityTypes.add(e2);

        // Expectations
        context.checking(new Expectations() {{
            allowing(e1).getLocation();
            will(returnValue(EntityID.fromComponents("a")));
            allowing(e2).getLocation();
            will(returnValue(EntityID.fromComponents("a","b")));
        }});

        // Run

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
        //ANBFile fs = setupTwoDeepFS_newScheme("/nowhere/dummyData");

        DummyProject project = new DummyProject(context);
        project.addMockEntityType("a");
        project.addMockEntityType("a","b");
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
        DummyProject projectA = new DummyProject(context);
        DummyProject projectB = new DummyProject(context);

        ProjectDB.ConstDBField type1 = context.mock(ProjectDB.ConstDBField.class, "type1");
        ProjectDB.ConstDBField type2 = context.mock(ProjectDB.ConstDBField.class, "type2");

        projectA.entityTypes.add(type1);
        projectB.entityTypes.add(type2);

        context.checking(new Expectations() {{
            allowing(type1).getLocation();
            will(returnValue(EntityID.fromComponents("character", "duped_entity_type")));
            allowing(type2).getLocation();
            will(returnValue(EntityID.fromComponents("character", "duped_entity_type")));
        }});


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

        SimpleEntityType type = new SimpleEntityType(EntityID.fromComponents("hello", "world"));
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

        DummyProject project = new DummyProject(context);
        ProjectDB.ConstDBField e1 = context.mock(ProjectDB.ConstDBField.class,"character.an_entity");
        ProjectDB.ConstDBField e2 = context.mock(ProjectDB.ConstDBField.class,"character.another_entity");
        project.entities.add(e1);
        project.entities.add(e2);

        context.checking(new Expectations() {{
            allowing(e1).getLocation();
            will(returnValue(EntityID.fromComponents("character", "an_entity")));
            allowing(e2).getLocation();
            will(returnValue(EntityID.fromComponents("character", "another_entity")));
        }});

        return project;
    }


    @Test
    public void testGetEntityByID() throws Exception {
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(getDefaultProject());

        EntityID idA = EntityID.fromComponents("character", "an_entity");
        EntityID idB = EntityID.fromComponents("character", "another_entity");
        EntityID idC = EntityID.fromComponents("character", "no_such_entity");

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

        DummyProject project = new DummyProject(context);

        ProjectDB.ConstDBField e1 = project.addMockEntityType("character", "duped_entity_type");
        ProjectDB.ConstDBField e2 = project.addMockEntityType("location", "duped_entity_type");

        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);

        EntityID idA = EntityID.fromComponents("character", "duped_entity_type");
        EntityID idB = EntityID.fromComponents("location", "duped_entity_type");

        final EntityType dupedEntityA = entitySource.getEntityTypeByID(idA);
        final EntityType dupedEntityB = entitySource.getEntityTypeByID(idB);

        assertThat(dupedEntityA, is(notNullValue()));
        assertThat(dupedEntityB, is(notNullValue()));

        assertThat(dupedEntityA.id().asString(), is(equalTo("character.duped_entity_type")));
        assertThat(dupedEntityB.id().asString(), is(equalTo("location.duped_entity_type")));
    }

    @Test
    public void testGetEntityTypeByName() {
        //DummyANBFileDirectory root = new DummyANBFileDirectory(asList("root"));
        //setupDefaultFS(root);
        DummyProject project = new DummyProject(context);
        final ProjectDB.ConstDBField dbField = context.mock(ProjectDB.ConstDBField.class);
        project.entityTypes.add(dbField);
        context.checking(new Expectations() {{
            allowing(dbField).getLocation();
            will(returnValue(EntityID.fromComponents("core","character")));
        }});
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);

        final EntityType characterEntityType = entitySource.getEntityTypeByShortName("character");
        assertThat(characterEntityType, is(notNullValue()));

        assertThat(characterEntityType.id().asString(),is(equalTo("core.character")));
        assertThat(characterEntityType.id().components(), is(equalTo(asList("core","character"))));
    }

    @Test
    public void testGetEntityTypeByName_noSuchEntityType() {
        //DummyANBFileDirectory root = new DummyANBFileDirectory(asList("root"));
        //setupDefaultFS(root);
        DummyProject project = new DummyProject(context);
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        final EntityType noSuchEntity = entitySource.getEntityTypeByShortName("no_such_entity");
        assertThat(noSuchEntity, is(nullValue()));
    }

    @Test
    public void testGetEntityTypeByName_multipleEntitiesWithSameName() {
        //DummyANBFileDirectory root = new DummyANBFileDirectory(asList("root"));
        //setupFSWithDupeName(root);
        DummyProject project = new DummyProject(context);
        final ProjectDB.ConstDBField dbFieldA = context.mock(ProjectDB.ConstDBField.class,"character.duped_entity_type");
        project.entityTypes.add(dbFieldA);
        context.checking(new Expectations() {{
            allowing(dbFieldA).getLocation();
            will(returnValue(EntityID.fromComponents("character","duped_entity_type")));
        }});
        final ProjectDB.ConstDBField dbFieldB = context.mock(ProjectDB.ConstDBField.class,"location.duped_entity_type");
        project.entityTypes.add(dbFieldB);
        context.checking(new Expectations() {{
            allowing(dbFieldB).getLocation();
            will(returnValue(EntityID.fromComponents("location","duped_entity_type")));
        }});
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
        //DummyANBFileDirectory root = new DummyANBFileDirectory(asList("root"));
        //setupDefaultFS(root);
        DummyProject project = new DummyProject(context);
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        final EntityType noSuchEntity = entitySource.getEntityTypeByShortName("a_character");
        assertThat(noSuchEntity, is(nullValue()));
    }





    @Test
    public void testCreateNewEntity() throws Exception {

        //DummyANBFileDirectory root = new DummyANBFileDirectory(asList("root"));
        //setupDefaultFS(root);
        DummyProject project = new DummyProject(context);
        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);


        //TODO: Each of these bits needs its own tests.
        final EntityType characterEntityType = entitySource.getEntityTypeByShortName("character");
        assertThat(characterEntityType, is(notNullValue()));

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

        DummyProject project = new DummyProject(context);

        ProjectDB.ConstDBField character_type = context.mock(ProjectDB.ConstDBField.class, "character_type");
        ProjectDB.ConstDBField an_entity = context.mock(ProjectDB.ConstDBField.class, "an_entity");
        ProjectDB.ConstDBField another_entity = context.mock(ProjectDB.ConstDBField.class, "another_entity");

        project.entities.add(an_entity);
        project.entities.add(another_entity);
        project.entityTypes.add(character_type);

        context.checking(new Expectations() {{
            allowing(character_type).getLocation();
            will(returnValue(EntityID.fromComponents("character")));
        }});

        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);

        EntitySourceListener listener = context.mock(EntitySourceListener.class);
        entitySource.addListener(listener);

        //TODO: Each of these bits needs its own tests.
        final EntityType characterEntityType = entitySource.getEntityTypeByShortName("character");
        assertThat(characterEntityType, is(notNullValue()));

        context.checking(new Expectations() {{
            oneOf(listener).onAddEntity(with(aNonNull(Entity.class)));
        }});

//        Entity entity = entitySource.createNewEntityOfType(characterEntityType,"some_character");
//        assertThat(entity, is(notNullValue()));

        fail("NYI - only partially implemented");


    }

    @Test
    public void testAddingAnEntityTypeGeneratesAnEvent() throws Exception {
        DummyProject project = new DummyProject(context);

        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);

        List<EntityType> addedEntityTypes = new ArrayList<>();

        entitySource.addListener(new EntitySourceListener() {
            @Override
            public void onAddEntity(Entity entity) {

            }

            @Override
            public void onAddEntityType(EntityType type) {
                addedEntityTypes.add(type);
            }
        });

        EntityType et = new EntityType() {

            EntityID id = EntityID.fromComponents("hello","world");

            @Override
            public EntityID id() {
                return id;
            }

            @Override
            public void addSourceProject(ANBProject project) {

            }

            @Override
            public List<ANBProject> getProjects() {
                return null;
            }
        };

        entitySource.createNewEntityType(et);

        assertThat(addedEntityTypes.size(), is(1));
        assertThat(addedEntityTypes.get(0), is(sameInstance(et)));

        //Now it should show up in the getInstanceTypes...
        assertThat(entitySource.getEntityTypes(), hasItem(et));

        assertThat(entitySource.getEntityTypeByID(EntityID.fromComponents("hello", "world")), is(sameInstance(et)));
    }

    @Test
    public void testEntitiesGetTheirTypeSet() throws Exception {
        DummyProject project = new DummyProject(context);

        ProjectDB.ConstDBField character_type = context.mock(ProjectDB.ConstDBField.class, "character_type");
        ProjectDB.ConstDBField an_entity = context.mock(ProjectDB.ConstDBField.class, "an_entity");

        context.checking(new Expectations() {{
            allowing(an_entity).getLocation();
            will(returnValue(EntityID.fromComponents("character", "donkey", "ed")));

            allowing(character_type).getLocation();
            will(returnValue(EntityID.fromComponents("character","donkey")));

        }});

        project.entities.add(an_entity);
        project.entityTypes.add(character_type);

        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);

        final EntityType entityType = entitySource.getEntityTypeByID(EntityID.fromComponents("character", "donkey"));
        final Entity entity = entitySource.getEntityById(EntityID.fromComponents("character","donkey","ed"));

        assertThat(entityType, is(notNullValue()));
        assertThat(entity, is(notNullValue()));
        assertThat(entity.getType(), is(sameInstance(entityType)));

        //Looks like this needs to be handled at the project level?
    }
}
