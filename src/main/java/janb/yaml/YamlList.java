package janb.yaml;

import java.util.List;

/**
* Created by michaelanderson on 13/02/2015.
*/
public class YamlList extends YamlValue<List> {

    public YamlList(List rawList, YamlPath path) {
        super(rawList, path);
    }

    public List getRawList() {
        return data;
    }

    public int size() {
        return data.size();
    }

    public YamlValue getChild(int i) throws YamlConversionException {
        return new YamlValue(data.get(i), path.child(i));
    }
}
