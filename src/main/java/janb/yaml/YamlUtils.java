package janb.yaml;

import java.util.*;

/**
 * Created by michaelanderson on 3/02/2015.
 */
public final class YamlUtils {
    private YamlUtils() {}

    public static YamlMap getRootAsMap(Object yamlData) throws YamlConversionException {
        if (yamlData instanceof YamlMap)
            return new YamlMap(((YamlMap) yamlData).getRawMap(), new YamlPath());
        if (yamlData instanceof Map)
            return new YamlMap((Map) yamlData, new YamlPath());
        throw new YamlConversionException("Root object is not a map");
    }

    public static YamlString objectToStringOrNull(Object value, YamlPath path) {
        if (value instanceof YamlString)
            return (YamlString) value;
        if(value instanceof String)
            return new YamlString((String)value, path);
        return null;
    }

    public static YamlList objectToListOrNull(Object value, YamlPath path) {
        if(value==null)
            return null;
        if (value instanceof YamlList)
            return new YamlList(((YamlList) value).getRawList(), path);
        if (value instanceof List)
            return new YamlList((List) value, path);
        return null;
    }

    public static YamlMap objectToMapOrNull(Object value, YamlPath path) {
        if(value==null)
            return null;
        if (value instanceof YamlMap)
            return new YamlMap(((YamlMap) value).getRawMap(), path);
        if (value instanceof Map)
            return new YamlMap((Map) value, path);
        return null;
    }

    public static YamlList objectToList(Object yamlList, YamlPath path) throws YamlConversionException {
        if(yamlList==null) {
            throw new YamlConversionException(String.format("No child found at %s", path));
        }
        YamlList result = objectToListOrNull(yamlList, path);

        if(result!=null)
            return result;

        throw new YamlConversionException(String.format("Object at %s is a %s, not a list", path, yamlList.getClass()));
    }

    private static YamlMap objectToMap(Object yamlList, YamlPath path) throws YamlConversionException {
        if(yamlList==null) {
            throw new YamlConversionException(String.format("No child found at %s", path));
        }
        YamlMap result = objectToMapOrNull(yamlList,path);
        if(result!=null)
            return result;

        throw new YamlConversionException(String.format("Object at %s is a %s, not a map", path, yamlList.getClass()));
    }


}
