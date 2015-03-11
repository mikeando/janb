package janb.util;

import java.util.List;

/**
 */
public interface ANBFileSystem {
    public List<ANBFile> getAllFiles(ANBFile file);
    ANBFile getFileForString(String s);

    byte[] readFileContents(ANBFile file);
    void writeFileContents(ANBFile file, byte[] data);

    ANBFile makePaths(ANBFile directory, List<String> components);
}
