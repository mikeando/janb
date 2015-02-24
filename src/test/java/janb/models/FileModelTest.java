package janb.models;

import janb.mxl.IMxlFile;
import janb.mxl.dummy.DummyMxlFile;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class FileModelTest {

    @Test
    public void testConstructorThrowsNPE() throws Exception {
        IMxlFile file = null;
        try {
            FileModel model = new FileModel(file, null);
            fail("Should have thrown");
        } catch( NullPointerException npe) {

        }
    }

    @Test
    public void testConstructor() {
        IMxlFile file = DummyMxlFile.fileWithNullAnnotations();
        FileModel model = new FileModel(file, null);
    }

    @Test
    public void testGetTitle() throws Exception {
        IMxlFile file = DummyMxlFile.fileWithNullAnnotations();
        FileModel model = new FileModel(file, null);
        assertEquals("a file", model.getTitle());
    }

    @Test
    public void testGetContextActions() throws Exception {
        fail("NYI");
    }

    @Test
    public void testGetChildModels() throws Exception {
        {
            IMxlFile file = DummyMxlFile.fileWithNullAnnotations();

            FileModel model = new FileModel(file, null);

            final List<IModel> childModels = model.getChildModels();
            assertThat(childModels, notNullValue());
            assertEquals(0, childModels.size());
        }
        {
            IMxlFile file = DummyMxlFile.fileWithAnnotations(2);

            FileModel model = new FileModel(file, null);

            final List<IModel> childModels = model.getChildModels();
            assertThat(childModels, notNullValue());
            assertEquals(2, childModels.size());
            assertThat(childModels.get(0), instanceOf(AnnotationModel.class));
        }
    }

    @Test public void testViewCode() throws Exception {
        fail("Not yet implemented");
    }
}