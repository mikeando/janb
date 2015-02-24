package janb.mxl.dummy;

import janb.mxl.IMxlFile;
import janb.mxl.MxlAnnotation;
import janb.mxl.MxlText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 12/02/2015.
 */
public class DummyMxlFile {

    private static class FileWithAnnotations implements IMxlFile {

        List<MxlAnnotation> annotations;
        MxlText text;
        String rawData;

        public FileWithAnnotations(List<MxlAnnotation> annotations) {
            this.annotations = annotations;
        }

        @Override
        public String getBaseName() {
            return "a file";
        }

        @Override
        public List<MxlAnnotation> getAnnotations() {
            return annotations;
        }

        @Override
        public MxlText getText() {
            return text;
        }

        @Override
        public String getRawData() {
            return rawData;
        }
    }


    public static IMxlFile fileWithAnnotations(int n) {
        List<MxlAnnotation> annotations = new ArrayList<>();
        for(int i=0; i<n; ++i) {
            annotations.add(DummyMxlAnnotation.dummyAnnotation("annotation "+i));
        }
        return new FileWithAnnotations(annotations);
    }



    public static IMxlFile fileWithNullAnnotations() {
        return new FileWithAnnotations(null);
    }
}
