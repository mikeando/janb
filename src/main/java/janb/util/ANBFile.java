package janb.util;

import java.io.IOException;
import java.util.List;

/**
 * Created by michaelanderson on 3/03/2015.
 */
public interface ANBFile {

    //TODO: Hoist this into the fileSystem?
    List<String> relative_path(ANBFile root);
    public List<ANBFile> getAllFiles();

    ANBFileSystem getFS();

    public boolean isDirectory();
    boolean isWritable();

    ANBFile child(String name);

    String pathAsString();

    String getName();

    byte[] readContents() throws IOException;
}
