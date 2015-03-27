package janb.project;

import janb.models.EntityID;
import janb.util.ANBFile;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ANBProjectTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Test
    public void testTryUpdate() throws Exception {
        ANBFile sourcePath = context.mock(ANBFile.class);
        SimpleANBProject project = new SimpleANBProject(sourcePath);
        fail("NYI");
        //project.tryUpdate(field);
    }

    @Test
    public void testTryUpdate_failsOnNull() throws Exception {
        ANBFile sourcePath = context.mock(ANBFile.class);
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        try {
            project.tryUpdate(null);
            fail("Should have thrown");
        } catch ( NullPointerException e) {
            // Should end up in here...
        }
    }

    @Test
    public void testTrySave() throws Exception {
        fail("NYI");
    }

    @Test
    public void testTrySave_failsOnNull() throws Exception {
        ANBFile sourcePath = context.mock(ANBFile.class);
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        try {
            project.trySave(null);
            fail("Should have thrown");
        } catch ( NullPointerException e) {
            // Should end up in here...
        }
    }

    @Test
    public void testGetEntityById_no_such_entity() throws Exception {
        ANBFile sourcePath = context.mock(ANBFile.class);
        SimpleANBProject project = new SimpleANBProject(sourcePath);


        final ProjectDB.ConstDBField dbField = project.getEntityById(EntityID.fromComponents("no.such.entity"));
        assertThat(dbField,is(nullValue()));
    }

    @Test
    public void testGetEntityById_npe() throws Exception {
        ANBFile sourcePath = context.mock(ANBFile.class);
        SimpleANBProject project = new SimpleANBProject(sourcePath);

        try {

        final ProjectDB.ConstDBField dbField = project.getEntityById(null);
            fail("Should have thrown");
        } catch ( NullPointerException e) {
            // Should end up in here...
        }
    }

    @Test
    public void testGetEntities() throws Exception {
        fail("NYI");
    }

    @Test
    public void testGetFiles() throws Exception {
        fail("NYI");
    }
}