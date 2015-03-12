package janb.util;

import java.io.IOException;
import java.util.List;

/**
 */
public interface ANBFileSystem {
    public List<ANBFile> getAllFiles(ANBFile file);
    ANBFile getFileForString(String s);

    byte[] readFileContents(ANBFile file) throws IOException;
    void writeFileContents(ANBFile file, byte[] data) throws IOException;

    ANBFile makePaths(ANBFile directory, List<String> components) throws IOException;
}
