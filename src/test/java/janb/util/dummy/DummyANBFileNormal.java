package janb.util.dummy;

import janb.util.ANBFile;

import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 31/03/2015.
 */
public class DummyANBFileNormal extends DummyANBFileBase {

    public DummyANBFileNormal(DummyANBFileDirectory parent, List<String> absolute_path, boolean isWritable) {
        super(parent, absolute_path, isWritable);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public ANBFile child(String name) {
        return null;
    }

    @Override
    public ANBFile createSubdirectory(String s) {
        throw new RuntimeException("Can not create subdirectory of a file!");
    }

    @Override
    public void createFile(String s, byte[] rawData) {
        throw new RuntimeException("Can not create subfile of a file!");
    }

    @Override
    public List<ANBFile> getAllFiles() {
        return Collections.EMPTY_LIST;
    }
}
