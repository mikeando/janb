package janb.util;

import java.io.IOException;
import java.util.List;

/**
 * Created by michaelanderson on 3/03/2015.
 */
public interface ANBFile {

    List<String> relative_path(ANBFile root);
    public List<ANBFile> getAllFiles();

    public boolean isDirectory();
    boolean isWritable();

    ANBFile child(String name);

    String pathAsString();

    String getName();

    byte[] readContents() throws IOException;

    /**
     * @Note use of this is racy. It could return true but then have a read fail immediately after if
     * the file has been removed in the interim. It is usually better to just try to read/write the
     * file with the right settings and handle failure.
     */
    boolean exists();

    boolean hasExtension(String s);
    ANBFile withoutExtension(String s);
    ANBFile withExtension(String s);

    ANBFile createSubdirectory(String s);

    void createFile(String s, byte[] rawData);
}
