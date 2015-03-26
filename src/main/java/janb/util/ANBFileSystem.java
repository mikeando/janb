package janb.util;

import java.io.IOException;
import java.util.List;

/**
 */
public interface ANBFileSystem {
    ANBFile getFileForString(String s);

    void writeFileContents(ANBFile file, byte[] data) throws IOException;

    ANBFile makePaths(ANBFile directory, List<String> components) throws IOException;
}
