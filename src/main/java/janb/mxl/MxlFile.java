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
public class MxlFile implements IMxlFile {
    private final List<MxlAnnotation> annotations = new ArrayList<>();
    private final String name;
    private String rawContent;
    private MxlText content;

    public MxlFile(String name, IMxlMetadataFile metadata, String rawContent) throws MxlConstructionException {
        this.name = name;
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

            MxlAnnotation annotation = new MxlAnnotation(startLocation, endLocation, x.data);
            annotations.add(annotation);
        }
    }

    public static MxlFile createAndBind(File f, IMxlMetadataFile metadata) throws IOException, MxlConstructionException {
        byte[] bytes = Files.readAllBytes(f.toPath());
        return createAndBind(f.getName(), bytes, metadata);
    }

    public static MxlFile createAndBind(String name, byte[] bytes, IMxlMetadataFile metadata) throws IOException, MxlConstructionException {
        String content = (bytes==null) ? "" : new String(bytes, Charset.forName("UTF-8"));
        return new MxlFile(name, metadata, content);
    }

    @Override
    public List<MxlAnnotation> getAnnotations() {
        return annotations;
    }

    @Override
    public MxlText getText() {
        return content;
    }

    @Override
    public String getRawData() {
        return rawContent;
    }

    @Override
    public String getBaseName() {
        return name;
    }
}
