package janb.mxl;

import java.util.List;

/**
 * Created by michaelanderson on 30/03/2015.
 */
public interface IMxlMetadataFile {
    void addUnboundAnnotation(MxlUnboundAnnotation annotation);

    List<MxlUnboundAnnotation> getUnboundAnnotations();
}
