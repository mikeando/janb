package janb.yaml;

import janb.mxl.MxlConstructionException;

/**
 * Created by michaelanderson on 12/02/2015.
 */
public interface YamlMapCallback {
    void onMap(String key, YamlMap value);

    void onList(String key, YamlList value) throws MxlConstructionException;

    void onString(String key, YamlString value) throws MxlConstructionException;
}
