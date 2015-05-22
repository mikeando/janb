package janb.yaml;

import janb.mxl.MxlConstructionException;

import java.util.Map;
import java.util.Set;

/**
* Created by michaelanderson on 13/02/2015.
*/
public class YamlMap extends YamlValue<Map>{

    public YamlMap(Map rawMap, YamlPath path) {
        super(rawMap,path);
    }

    public Map getRawMap() {
        return data;
    }

    public YamlValue getChild(String childName) {
        return new YamlValue(data.get(childName), path.child(childName));
    }

    public void onAllChildren(YamlMapCallback yamlMapCallback) throws MxlConstructionException {
        //TODO: Aargh this is soo ugly!
        for( Map.Entry kv : (Set<Map.Entry>) data.entrySet() ) {
            String key = (String) kv.getKey();
            Object value = kv.getValue();

            YamlString asString = YamlUtils.objectToStringOrNull(value,path.child(key));
            if(asString!=null) {
                yamlMapCallback.onString(key,asString);
                continue;
            }
            YamlList asList = YamlUtils.objectToListOrNull(value, path.child(key));
            if(asList!=null) {
                yamlMapCallback.onList(key, asList);
                continue;
            }
            YamlMap asMap = YamlUtils.objectToMapOrNull(value, path.child(key));
            if(asMap!=null) {
                yamlMapCallback.onMap(key, asMap);
            }
            throw new RuntimeException(String.format("Unhandled node type at %s\n", path.child(key)));
        }
    }
}
