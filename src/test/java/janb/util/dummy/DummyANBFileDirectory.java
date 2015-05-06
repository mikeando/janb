package janb.util.dummy;

import janb.util.ANBFile;

import java.util.*;

/**
 * Created by michaelanderson on 31/03/2015.
 */
public class DummyANBFileDirectory extends DummyANBFileBase {

    private final Map<String, ANBFile> children;

    public DummyANBFileDirectory(DummyANBFileDirectory parent, List<String> absolute_path, boolean isWritable, Map<String, ANBFile> children) {
        super(parent, absolute_path, isWritable);
        this.children = children;
    }

    public DummyANBFileDirectory(DummyANBFileDirectory parent, List<String> absolute_path) {
        this(parent, absolute_path, true, new HashMap<>());
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public ANBFile child(String name) {
        return children.get(name);
    }

    @Override
    public DummyANBFileDirectory createSubdirectory(String s) {
        return addChildDirectory(s);
    }

    @Override
    public void createFile(String s, byte[] rawData) {
        final DummyANBFileNormal file = addChildFile(s);
        file.content = rawData;
    }

    @Override
    public List<ANBFile> getAllFiles() {
        return Collections.unmodifiableList(new ArrayList<>(children.values()));
    }

    public void addChild(String name, ANBFile child) {
        children.put(name,child);
    }

    public DummyANBFileDirectory addChildDirectory(String name) {
        List<String> newPath = new ArrayList<>(absolute_path);
        newPath.add(name);
        DummyANBFileDirectory newFile = new DummyANBFileDirectory(this, newPath);
        addChild(name, newFile);
        return newFile;
    }

    public DummyANBFileNormal addChildFile(String name) {
        List<String> newPath = new ArrayList<>(absolute_path);
        newPath.add(name);
        DummyANBFileNormal newFile = new DummyANBFileNormal(this, newPath, true);
        addChild(name, newFile);
        return newFile;
    }

    public ANBFile resolve(String... components) {
        ANBFile result = this;
        for (String component : components) {
            result = result.child(component);
            if(result==null)
                return null;
        }
        return result;
    }
}
