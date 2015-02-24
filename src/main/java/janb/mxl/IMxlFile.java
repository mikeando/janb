package janb.mxl;

import java.util.List;

/**
 * Created by michaelanderson on 11/02/2015.
 */
public interface IMxlFile {
    String getBaseName();

    List<MxlAnnotation> getAnnotations();

    MxlText getText();

    String getRawData();
}
