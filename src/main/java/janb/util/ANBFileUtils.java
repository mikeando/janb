package janb.util;

import janb.models.EntityID;

/**
 * Created by michaelanderson on 20/04/2015.
 */
public class ANBFileUtils {
    public ANBFile findOrCreateDirectoryForID(ANBFile sourcePath, EntityID id) {
        ANBFile result = sourcePath;
        for (String s : id.components()) {
            ANBFile f = result.child(s);
            if(f==null) {
                result = result.createSubdirectory(s);
            } else {
                result = f;
            }
        }
        return result;
    }
}
