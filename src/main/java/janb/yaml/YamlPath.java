package janb.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* Created by michaelanderson on 13/02/2015.
*/
public class YamlPath {
    final private List<String> path;
    YamlPath(List<String> path) {
        this.path = Collections.unmodifiableList(new ArrayList<>(path));
    }
    YamlPath() {
        path = Collections.emptyList();
    }

    YamlPath child(String name) {
        ArrayList<String> childPath = new ArrayList<>(path);
        childPath.add(name);
        return new YamlPath(childPath);
    }

    public YamlPath child(int i) {
        return child(Integer.toString(i));
    }

    @Override
    public String toString() {
        return String.format("%s",path);
    }
}
