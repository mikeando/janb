package janb.util;

import java.util.List;

/**
 * Created by michaelanderson on 3/03/2015.
 */
public interface ANBFile {

    //TODO: Hoist this into the fileSystem?
    List<String> relative_path(ANBFile root);

    ANBFileSystem getFS();

    public boolean isDirectory();
    boolean isWritable();

    ANBFile child(String name);

    String pathAsString();
}
