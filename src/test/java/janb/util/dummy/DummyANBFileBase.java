package janb.util.dummy;

import janb.util.ANBFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 31/03/2015.
 */
public abstract class DummyANBFileBase implements ANBFile {
    final DummyANBFileDirectory parent;
    public final List<String> absolute_path;
    protected final boolean isWritable;

    // Hide this away a little?
    public byte[] content = null;

    public DummyANBFileBase(DummyANBFileDirectory parent, List<String> absolute_path, boolean isWritable) {
        this.parent = parent;
        this.absolute_path = absolute_path;
        this.isWritable = isWritable;
    }

    @Deprecated
    @Override
    public List<String> relative_path(ANBFile root) {
        final DummyANBFileBase rootAsDummy = (DummyANBFileBase) root;
        if (rootAsDummy.absolute_path.size() > absolute_path.size())
            throw new RuntimeException("Not a child path!");
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < absolute_path.size(); ++i) {
            if (i < rootAsDummy.absolute_path.size()) {
                if (rootAsDummy.absolute_path.get(i).equals(absolute_path.get(i)))
                    continue;
                throw new RuntimeException("Not a child path!");
            } else {
                result.add(absolute_path.get(i));
            }
        }

        return result;
    }


    @Override
    public boolean isWritable() {
        return isWritable;
    }

    @Override
    public String pathAsString() {
        return String.join("/", absolute_path);
    }

    @Override
    public String getName() {
        return absolute_path.get(absolute_path.size() - 1);
    }

    @Override
    public byte[] readContents() throws IOException {
        return content;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean hasExtension(String s) {
        return getName().endsWith(s);
    }


    @Override
    public ANBFile withoutExtension(String s) {
        final String name = getName();
        final String newName = name.substring(0, name.length() - s.length());
        return parent.child(newName);
    }

    @Override
    public ANBFile withExtension(String s) {
        final String name = getName();
        final String newName = name + s;
        return parent.child(newName);
    }

    @Override
    public String toString() {
        return "DummyANBFileBase{" +
                "absolute_path=" + absolute_path +
                '}';
    }


}
