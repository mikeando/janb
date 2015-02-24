package janb.mxl;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MxlFileTest {

    final URL fileURL = getClass().getResource("../../../../resources/test/mxl/sample.md");
    final URL metadataURL = getClass().getResource("../../../../resources/test/mxl/sample.md.mxl");


    public MxlFileTest() {
        super();
        assertNotNull(fileURL);
        assertNotNull(metadataURL);
    }

    @Test
    public void testLoadMetadata() throws MxlConstructionException {
        MxlMetadataFile metadata = new MxlMetadataFile(new File(metadataURL.getPath()));
        assertNotNull(metadata);
    }

    @Test
    public void testCreateAndBind() throws MxlConstructionException, IOException {
        MxlMetadataFile metadata = new MxlMetadataFile(new File(metadataURL.getPath()));
        assertNotNull(metadata);

        MxlFile file = MxlFile.createAndBind( new File(fileURL.getPath()),metadata);
        assertNotNull(file);
    }

    @Test
    public void testCreateAndBindSetsData() throws MxlConstructionException, IOException {

        MxlMetadataFile metadata = new MxlMetadataFile(new File(metadataURL.getPath()));
        assertNotNull(metadata);
        MxlFile file = MxlFile.createAndBind( new File(fileURL.getPath()),metadata);
        assertNotNull(file);

        String data = file.getText().getData();
        assertEquals("This is a note -\nits very interesting.",data);
    }

    @Test
    public void testCreateAndBinSetsAnnotations() throws MxlConstructionException, IOException {
        MxlMetadataFile metadata = new MxlMetadataFile(new File(metadataURL.getPath()));
        assertNotNull(metadata);
        MxlFile file = MxlFile.createAndBind( new File(fileURL.getPath()),metadata);
        assertNotNull(file);

        final List<MxlAnnotation> annotations = file.getAnnotations();
        assertNotNull(annotations);
        assertEquals(3, annotations.size());

        {
            MxlAnnotation annotation = annotations.get(0);
            assertEquals(0, annotation.getStart().location());
            assertEquals(4, annotation.getEnd().location());
        }

        {
            MxlAnnotation annotation = annotations.get(1);
            assertEquals(10, annotation.getStart().location());
            assertEquals(14, annotation.getEnd().location());
        }

        {
            MxlAnnotation annotation = annotations.get(2);
            assertEquals(21, annotation.getStart().location());
            assertEquals(37, annotation.getEnd().location());
        }
    }

    @Test
    public void testCreateAndBinSetsAnnotationsReversed() throws MxlConstructionException, IOException {
        MxlMetadataFile metadata = new MxlMetadataFile(new File(metadataURL.getPath()));
        assertNotNull(metadata);
        MxlMetadataFile metadataReversed = new MxlMetadataFile();

        {
            ArrayList<MxlUnboundAnnotation> annotations = new ArrayList<>(metadata.getUnboundAnnotations());
            Collections.reverse(annotations);
            annotations.forEach(metadataReversed::addUnboundAnnotation);
        }

        MxlFile file = MxlFile.createAndBind( new File(fileURL.getPath()),metadataReversed);
        assertNotNull(file);

        final List<MxlAnnotation> annotations = file.getAnnotations();
        assertNotNull(annotations);
        assertEquals(3, annotations.size());

        //Put them back in the order of the previous test
        Collections.reverse(annotations);

        // Now the tests as above.
        {
            MxlAnnotation annotation = annotations.get(0);
            assertEquals(0, annotation.getStart().location());
            assertEquals(4, annotation.getEnd().location());
        }

        {
            MxlAnnotation annotation = annotations.get(1);
            assertEquals(10, annotation.getStart().location());
            assertEquals(14, annotation.getEnd().location());
        }

        {
            MxlAnnotation annotation = annotations.get(2);
            assertEquals(21, annotation.getStart().location());
            assertEquals(37, annotation.getEnd().location());
        }
    }


}