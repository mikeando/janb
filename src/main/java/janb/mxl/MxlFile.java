package janb.mxl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 5/02/2015.
 */
public class MxlFile {
    private final List<MxlAnnotation> annotations = new ArrayList<>();
    private String rawContent;
    private MxlText content;

    public MxlFile(MxlMetadataFile metadata, String rawContent) throws MxlConstructionException {
        this.rawContent = rawContent;
        this.content = new MxlText(rawContent);

        for(MxlUnboundAnnotation x : metadata.getUnboundAnnotations()) {

            MxlTextLocation startLocation = content.replaceFirstOccurrence(x.start);
            MxlTextLocation endLocation = content.replaceFirstOccurrenceAfter(startLocation, x.end);


//
//            final int endPos = transformedContent.indexOf(x.end);
//            String beforeEnd = transformedContent.substring(0,endPos);
//            String afterEnd = transformedContent.substring(endPos+x.end.length(), transformedContent.length());
//            System.err.printf("BEFORE END = '%s'\n", beforeEnd);
//            System.err.printf("AFTER END = '%s'\n", afterEnd);
//            transformedContent = beforeEnd + afterEnd;

            MxlAnnotation annotation = new MxlAnnotation(startLocation, endLocation);
            annotations.add(annotation);
        }
    }

    public static MxlFile createAndBind(File f, MxlMetadataFile metadata) throws IOException, MxlConstructionException {
        byte[] bytes = Files.readAllBytes(f.toPath());
        String content = new String(bytes, Charset.forName("UTF-8"));
        return new MxlFile(metadata, content);
    }

    public String getData() {
        return content.getData();
    }

    public List<MxlAnnotation> getAnnotations() {
        return annotations;
    }
}
