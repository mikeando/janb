package janb.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by michaelanderson on 3/02/2015.
 */
public final class YamlUtils {
    private YamlUtils() {}

    public static class YamlPath {
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

    public static YamlMap getRootAsMap(Object yamlData) throws ConversionException {
        if (yamlData instanceof YamlMap)
            return new YamlMap(((YamlMap) yamlData).getRawMap(), new YamlPath());
        if (yamlData instanceof Map)
            return new YamlMap((Map) yamlData, new YamlPath());
        throw new ConversionException("Root object is not a map");
    }

    private static YamlList objectToList(Object yamlList, YamlPath path) throws ConversionException {
        if(yamlList==null) {
            throw new ConversionException(String.format("No child found at %s", path));
        }
        if (yamlList instanceof YamlList)
            return new YamlList(((YamlList) yamlList).getRawList(), path);
        if (yamlList instanceof List)
            return new YamlList((List) yamlList, path);
        throw new ConversionException(String.format("Object at %s is a %s, not a list", path, yamlList.getClass()));
    }

    private static YamlMap objectToMap(Object yamlList, YamlPath path) throws ConversionException {
        if(yamlList==null) {
            throw new ConversionException(String.format("No child found at %s", path));
        }
        if (yamlList instanceof YamlMap)
            return new YamlMap(((YamlMap) yamlList).getRawMap(), path);
        if (yamlList instanceof Map)
            return new YamlMap((Map) yamlList, path);
        throw new ConversionException(String.format("Object at %s is a %s, not a map", path, yamlList.getClass()));
    }

    public static class YamlMap {
        private final Map rawMap;
        private final YamlPath path;

        public YamlMap(Map rawMap, YamlPath path) {
            this.rawMap = rawMap;
            this.path = path;
        }

        public Map getRawMap() {
            return rawMap;
        }

        public YamlList getChildList(String childName) throws ConversionException {
            return objectToList(rawMap.get(childName), path.child(childName));
        }

        public String getString(String key) throws ConversionException {
            final Object obj = rawMap.get(key);
            if(obj==null)
                return null;
            if(obj instanceof String)
                return (String)obj;
            throw new ConversionException(String.format("Object at %s.%s is a %s, not a String", path, key, obj.getClass()));
        }

        public Object getObject(String key) {
            return rawMap.get(key);
        }
    }

    public static class ConversionException extends Exception {
        public ConversionException(String s) {
            super(s);
        }
    }

    public static class YamlList {
        private final List rawList;
        private final YamlPath path;

        public YamlList(List rawList, YamlPath path) {
            this.rawList = rawList;
            this.path = path;
        }

        public List getRawList() {
            return rawList;
        }

        public int size() {
            return rawList.size();
        }

        public YamlMap getChildMap(int i) throws ConversionException {
            return objectToMap(rawList.get(i), path.child(i));
        }
    }
}
