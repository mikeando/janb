package janb.project;

import janb.models.Entity;
import janb.models.EntityID;
import janb.models.EntitySource;
import janb.mxl.IMxlFile;
import janb.mxl.MxlAnnotation;
import janb.mxl.MxlFile;
import janb.util.ANBFile;
import janb.util.dummy.DummyANBFileDirectory;
import janb.util.dummy.DummyANBFileNormal;
import org.hamcrest.core.Is;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ANBProjectTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private void setupDefaultFS(DummyANBFileDirectory root) {
        DummyANBFileDirectory entitiesDir = root.addChildDirectory("entities");

        DummyANBFileDirectory characterDir = entitiesDir.addChildDirectory("character");
        DummyANBFileDirectory donkeysDir = characterDir.addChildDirectory("donkeys");

        DummyANBFileDirectory locationDir = entitiesDir.addChildDirectory("location");

        DummyANBFileDirectory an_entity = characterDir.addChildDirectory("an_entity");
        an_entity.addChildFile("_type");
        DummyANBFileDirectory another_entity = characterDir.addChildDirectory("another_entity");
        another_entity.addChildFile("_type");

    }

/**
 *
 *  * Different types of Entity are determined by their "type" file.
 *
 *  * for a text type the file contains simply "text"
 *    the content is stored in the text.md file in the directory
 *    the meta-data is in the text.md.mxl file. There should be no
 *    other files. (if there are they are ignored)
 *
 *  * for a collection entity - the "type" file does not exist or
 *     contains "collection" or "collection:protottype_id".
 *     the rest of the content of the dir should be sub-entity directories.
 *     Any other files are ignored
 *
 *  * for an entity reference - type file contains "ref:reference_id"
 *    There should be not other content in the directory.
 *    If there is it is ignored.
 *
 *  The default package structure is thus like:
 *
 *
 *  + package
 *     |- files
 *     |  |- chapter1
 *     |  |  |- type = "text"
 *     |  |  |- text.md = "This is chapter 1..."
 *     |  |  |- text.md.mxl = "..."
 *     |  |- chapter2
 *     |     |- type = "text"
 *     |     |- text.md = "This is chapter 1..."
 *     |     |- text.md.mxl = "..."
 *     |- entities
 *     |  |- character
 *     |     |- ed
 *     |       |- type = "collection:prototypes.character"
 *     |       |- full_name
 *     |       |   |- type = "text"
 *     |       |   |- text.md = "Ed Foozle"
 *     |       |   |- text.md.mxl = "...."
 *     |       |- job
 *     |       |  |- type = "text"
 *     |       |  |- text.md = "Baker"
 *     |       |  |- text.md.mxl = "..."
 *     |- prototypes
 *        |- character
 *            |- type = "collection"
 *            |- full_name
 *            |   |- type = "text"
 *            |   |- text.md = "No Name"
 *            |   |- text.md.mxl = "..."
 *            |- job (...)
 *            |- age (...)
 */
    private void setupDefaultPackage(DummyANBFileDirectory root) {

        DummyANBFileDirectory filesDir = root.addChildDirectory("files");

        {
            DummyANBFileDirectory chapter1 = filesDir.addChildDirectory("chapter1");

            DummyANBFileNormal type = chapter1.addChildFile("_type");
            type.content="text".getBytes();
            DummyANBFileNormal text = chapter1.addChildFile("text.md");
            text.content="It was a <<1>>Dark and Stormy<</1>> night.".getBytes();
            DummyANBFileNormal text_mxl = chapter1.addChildFile("text.md.mxl");
            text_mxl.content = ("annotations :\n" +
                    "  - { start : \"<<1>>\", end : \"<</1>>\", note: \"Can't get more corney than this.\" }\n"
                    ).getBytes(StandardCharsets.UTF_8);
        }

        {
            DummyANBFileDirectory chapter2 = filesDir.addChildDirectory("chapter1");

            DummyANBFileNormal type = chapter2.addChildFile("_type");
            type.content="text".getBytes();
            DummyANBFileNormal text = chapter2.addChildFile("text.md");
            text.content="<<1>>Roger<</1>> danced hapily...".getBytes();
            DummyANBFileNormal text_mxl = chapter2.addChildFile("text.md.mxl");
            text_mxl.content = ("annotations :\n" +
                    "  - { start : \"<<1>>\", end : \"<</1>>\", note: \"Roger is awesome.\" }\n"
                ).getBytes(StandardCharsets.UTF_8);
        }

        DummyANBFileDirectory entitiesDir = root.addChildDirectory("entities");
        DummyANBFileDirectory characters = entitiesDir.addChildDirectory("character");

        {
            DummyANBFileDirectory ed = characters.addChildDirectory("ed");
            {
                DummyANBFileNormal type = ed.addChildFile("_type");
                type.content = "collection:prototypes.character".getBytes();
            }

            {
                final DummyANBFileDirectory full_name = ed.addChildDirectory("full_name");
                DummyANBFileNormal type = full_name.addChildFile("_type");
                type.content = "text".getBytes();
                DummyANBFileNormal text = full_name.addChildFile("text.md");
                text.content = "Ed Foozle".getBytes(StandardCharsets.UTF_8);
            }

            {
                final DummyANBFileDirectory job = ed.addChildDirectory("job");
                DummyANBFileNormal type = job.addChildFile("_type");
                type.content = "text".getBytes();
                DummyANBFileNormal text = job.addChildFile("text.md");
                text.content = "Baker".getBytes(StandardCharsets.UTF_8);
            }
        }

        DummyANBFileDirectory prototypesDir = root.addChildDirectory("prototypes");
        DummyANBFileDirectory characterPrototype = prototypesDir.addChildDirectory("character");
        {
            DummyANBFileNormal type = characterPrototype.addChildFile("_type");
            type.content = "collection".getBytes();
        }
        {
            final DummyANBFileDirectory full_name = characterPrototype.addChildDirectory("full_name");
            full_name.addChildFile("_type").content="text".getBytes(StandardCharsets.UTF_8);
            full_name.addChildFile("text.md").content="No Name".getBytes(StandardCharsets.UTF_8);
            //TODO: Do we want a .mxl here?
        }
        {
            final DummyANBFileDirectory job = characterPrototype.addChildDirectory("job");
            job.addChildFile("_type").content="text".getBytes(StandardCharsets.UTF_8);
            job.addChildFile("text.md").content="No Job".getBytes(StandardCharsets.UTF_8);
        }
        {
            final DummyANBFileDirectory job = characterPrototype.addChildDirectory("age");
            job.addChildFile("_type").content="text".getBytes(StandardCharsets.UTF_8);
            job.addChildFile("text.md").content="No Age".getBytes(StandardCharsets.UTF_8);
        }

    }

    @Test
    public void testTryUpdateCreatesFile() throws Exception {
        DummyANBFileDirectory sourcePath = new DummyANBFileDirectory(null, asList("root"));
        setupDefaultPackage(sourcePath);

        SimpleANBProject project = new SimpleANBProject(sourcePath);
        ProjectDB.DBField field = new ProjectDB.ConstCollectionField(
                EntityID.fromComponents("character","ed"),
                new HashMap<>(),
                null
                );
        project.tryUpdate(field);
    }

    @Test
    public void testTryUpdateUpdatesFileContents() throws Exception {
        fail("NYI");
    }

    @Test
    public void testTryUpdate_failsOnNull() throws Exception {
        ANBFile sourcePath = emptyProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        try {
            project.tryUpdate(null);
            fail("Should have thrown");
        } catch ( NullPointerException e) {
            // Should end up in here...
        }
    }

    @Test
    public void testTrySaveCreatesFile() throws Exception {
        DummyANBFileDirectory sourcePath = emptyProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);
        ProjectDB.DBField field = new ProjectDB.ConstCollectionField(
                EntityID.fromComponents("test","foo","donkey"),
                new HashMap<>(),
                null
        );
        project.trySave(field);

        assertThat( sourcePath.resolve("entities", "test", "foo", "donkey"), is(notNullValue()));
    }

    @Test
    public void testTrySaveSetsFileContents() throws Exception {
        DummyANBFileDirectory sourcePath = emptyProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);
        ProjectDB.DBField field = new ProjectDB.ConstCollectionField(
                EntityID.fromComponents("test","foo","donkey"),
                new HashMap<>(),
                null
        );
        project.trySave(field);

        fail("Need to check the file contents ... maybe with a more complex entity?");
    }

    private ANBFile simpleProject() {
        DummyANBFileDirectory sourcePath = new DummyANBFileDirectory( null, asList("project_path"));
        DummyANBFileDirectory filesPath = sourcePath.addChildDirectory("files");
        DummyANBFileNormal fileA = filesPath.addChildFile("a.txt");
        DummyANBFileDirectory fileB = filesPath.addChildDirectory("b");
        DummyANBFileNormal fileBB = fileB.addChildFile("b.txt");

        DummyANBFileDirectory entitiesPath = sourcePath.addChildDirectory("entities");
        final DummyANBFileDirectory locationDir = entitiesPath.addChildDirectory("location");
        final DummyANBFileDirectory perthEntity = locationDir.addChildDirectory("perth");
        perthEntity.addChildFile("_type");
        final DummyANBFileDirectory monkeyLandEntity = locationDir.addChildDirectory("monkey_land");
        monkeyLandEntity.addChildFile("_type");

        return sourcePath;
    }

    DummyANBFileDirectory emptyProject() {
        DummyANBFileDirectory sourcePath = new DummyANBFileDirectory(null, asList("project_path"));
        DummyANBFileDirectory filesPath = sourcePath.addChildDirectory("files");
        return sourcePath;
    }

    DummyANBFileDirectory simpleProjectWithMxlFiles() {
        DummyANBFileDirectory sourcePath = new DummyANBFileDirectory(null, asList("project_path"));

        final DummyANBFileDirectory filesDirectory = sourcePath.addChildDirectory("files");
        DummyANBFileNormal fileA = filesDirectory.addChildFile("a.md");
        fileA.content = "⟪A⟪This⟫A⟫ is a ⟪B⟪note⟫B⟫ - its ⟪C⟪very interesting⟫C⟫.".getBytes(StandardCharsets.UTF_8);
        DummyANBFileNormal fileAmxl = filesDirectory.addChildFile("a.md.mxl");
        fileAmxl.content = ("annotations :\n" +
                "  - { start : \"⟪A⟪\", end : \"⟫A⟫\", note: \"A note\" }\n" +
                "  - { start : \"⟪B⟪\", end : \"⟫B⟫\", note: \"Another note\" }\n" +
                "  - { start : \"⟪C⟪\", end : \"⟫C⟫\", note: \"A final note\" }").getBytes(StandardCharsets.UTF_8);

        return sourcePath;
    }

    @Test
    public void testTrySave_failsOnNull() throws Exception {

        ANBFile emptyProject = emptyProject();
        SimpleANBProject project = new SimpleANBProject(emptyProject);

        try {
            project.trySave(null);
            fail("Should have thrown");
        } catch ( NullPointerException e) {
            // Should end up in here...
        }
    }

    @Test
    public void testGetEntityById_no_such_entity() throws Exception {
        ANBFile sourcePath = emptyProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);


        final ProjectDB.ConstDBField dbField = project.getEntityById(EntityID.fromComponents("no.such.entity"));
        assertThat(dbField,is(nullValue()));
    }

    @Test
    public void testGetEntityById_npe() throws Exception {
        ANBFile sourcePath = emptyProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        try {

        final ProjectDB.ConstDBField dbField = project.getEntityById(null);
            fail("Should have thrown");
        } catch ( NullPointerException e) {
            // Should end up in here...
        }
    }

    @Test
    public void testGetFiles_empty() throws Exception {
        ANBFile sourcePath = emptyProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        final List<IMxlFile> files = project.getFiles();
        assertThat(files, is(notNullValue()));
        assertThat(files.size(), is(0));
    }

    @Test
    public void testGetFiles_non_empty() throws Exception {
        ANBFile sourcePath = simpleProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        final List<IMxlFile> files = project.getFiles();
        assertThat(files, is(notNullValue()));
        assertThat(files.size(), is(2));
    }

    @Test
    public void testGetFiles_mxl() throws Exception {
        ANBFile sourcePath = simpleProjectWithMxlFiles();
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        final List<IMxlFile> files = project.getFiles();
        assertThat(files, is(notNullValue()));
        assertThat(files.size(), is(1));

        final MxlFile mxlFile = (MxlFile) files.get(0);
        final List<MxlAnnotation> annotations = mxlFile.getAnnotations();
        assertThat(annotations.size(),is(3));
    }

    @Test
    public void testSaveFile() throws Exception {
        DummyANBFileDirectory sourcePath = simpleProjectWithMxlFiles();
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        IMxlFile file = context.mock(IMxlFile.class, "a_file.txt");

        context.checking(new Expectations() {{
            oneOf(file).getRawData();
            will(returnValue("Some content"));
        }});

        project.trySave(file, EntityID.fromComponents("files","chapters", "chapter1", "notes", "a_file.txt"));

        //Check containing directory was created.
        final ANBFile containingDir = sourcePath.resolve("files", "chapters", "chapter1", "notes");
        assertThat(containingDir, is(notNullValue()));

        //Now check the file was created.
        final ANBFile actualFile = sourcePath.resolve("files", "chapters", "chapter1", "notes", "a_file.txt");
        assertThat(actualFile, is(notNullValue()));
        assertThat(actualFile, is(instanceOf(DummyANBFileNormal.class)));
        DummyANBFileNormal fileAsDummy = (DummyANBFileNormal)actualFile;
        assertThat(new String(fileAsDummy.content, StandardCharsets.UTF_8), is("Some content"));
    }

    @Test
    public void testGetEntities_empty() throws Exception {
        ANBFile sourcePath = emptyProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        final List<ProjectDB.ConstDBField> entities = project.getEntities();
        assertThat(entities, is(notNullValue()));
        assertThat(entities.size(), is(0));
    }

    @Test
    public void testGetEntities_nonempty() throws Exception {
        ANBFile sourcePath = simpleProject();
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        final List<ProjectDB.ConstDBField> entities = project.getEntities();
        assertThat(entities, is(notNullValue()));
        assertThat(entities.size(), is(2));
    }

    @Test
    public void testGetEntityByName_onlyReturnsFiles() throws Exception {
        DummyANBFileDirectory root = new DummyANBFileDirectory(null ,asList("root"));
        setupDefaultFS(root);
        SimpleANBProject project = new SimpleANBProject(root);

        EntitySource entitySource = new EntitySource();
        entitySource.addProject(project);

        final Entity aCharacter = entitySource.getEntityByName("an_entity");
        assertThat(aCharacter, Is.is(notNullValue()));

        final Entity character = entitySource.getEntityByName("character");
        assertThat(character, Is.is(nullValue()));
    }

    @Test public void testCanGetEntityTypes()  throws Exception {

        DummyANBFileDirectory rootDirectory = new DummyANBFileDirectory(null, asList("root"));
        setupDefaultFS(rootDirectory);

        // Run
        SimpleANBProject project = new SimpleANBProject(rootDirectory);

        // Run
        final List<ProjectDB.ConstDBField> entityTypes = project.getEntityTypes();

        // Check
        assertThat(entityTypes, Is.is(notNullValue()));
        assertThat(entityTypes.size(), Is.is(4));

        assertThat(entityTypes.get(0).getLocation().components(), Is.is(equalTo(asList())));
        assertThat(entityTypes.get(1).getLocation().components(), Is.is(equalTo(asList("character"))));
        assertThat(entityTypes.get(2).getLocation().components(), Is.is(equalTo(asList("character", "donkeys"))));
        assertThat(entityTypes.get(3).getLocation().components(), Is.is(equalTo(asList("location"))));

    }

    @Test public void testDefaultPackage() throws Exception {
        DummyANBFileDirectory rootDirectory = new DummyANBFileDirectory(null, asList("root"));
        setupDefaultPackage(rootDirectory);
        SimpleANBProject project = new SimpleANBProject(rootDirectory);

        final List<ProjectDB.ConstDBField> entities = project.getEntities();

        List<EntityID> entityIds = entities.stream()
                .filter(f->(f!=null))
                .map(ProjectDB.DBField::getLocation)
                .collect(Collectors.toList());

        assertThat(entityIds, is(asList(EntityID.fromComponents("character","ed"))));
        assertThat(entities.size(), is(1));

        ProjectDB.ConstDBField ed = entities.stream()
                .filter( f -> f.getLocation().equals(EntityID.fromComponents("character","ed")))
                .findFirst()
                .get();

        assertThat(ed, notNullValue());

        //Check that ed exists and has his jobs and full_name fields.
        assertThat(ed, instanceOf(ProjectDB.ConstCollectionField.class));
        ProjectDB.ConstCollectionField edx = (ProjectDB.ConstCollectionField)ed;
        assertThat(edx.getFields().keySet(), hasItem("job"));
        assertThat(edx.getFields().keySet(), hasItem("full_name"));

        //Check the full name and job fields contain sane values
        final ProjectDB.ConstDBField full_name = edx.getField("full_name");
        assertThat(full_name, instanceOf(ProjectDB.ConstTextField.class));
        ProjectDB.ConstTextField full_namex = (ProjectDB.ConstTextField) full_name;
        assertThat(full_namex.getText(), is("Ed Foozle"));

        final ProjectDB.ConstDBField job = edx.getField("job");
        assertThat(job, instanceOf(ProjectDB.ConstTextField.class));
        ProjectDB.ConstTextField jobx = (ProjectDB.ConstTextField) job;
        assertThat(jobx.getText(), is("Baker"));

        // Check that ed is inheriting from his prototype
        final List<ProjectDB.ConstDBField> prototypes = project.getPrototypes();
        assertThat(prototypes.size(), is(1));
        ProjectDB.ConstDBField character_prototype_raw = prototypes.stream()
                .filter( f -> f.getLocation().equals(EntityID.fromComponents("character")))
                .findFirst()
                .get();
        assertThat(character_prototype_raw, instanceOf(ProjectDB.ConstCollectionField.class));
        ProjectDB.ConstCollectionField character_prototype = (ProjectDB.ConstCollectionField)character_prototype_raw;

        assertThat(edx.getPrototype(), is(sameInstance(character_prototype)));

        // TODO: Check that ed is getting values from his prototype

        //TODO: Check the files are loaded correctly

        //TODO: Check that the prototype exists.


    }

    @Test
    public void testGetEntitiesById() throws Exception {
        DummyANBFileDirectory rootDirectory = new DummyANBFileDirectory(null, asList("root"));
        setupDefaultPackage(rootDirectory);
        SimpleANBProject project = new SimpleANBProject(rootDirectory);

        final ProjectDB.ConstDBField entity = project.getEntityById(EntityID.fromComponents("character", "ed"));
        assertThat(entity, is(notNullValue()));

    }
}