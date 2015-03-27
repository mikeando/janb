package janb.yaml;

/**
 * Created by michaelanderson on 13/02/2015.
 */
public class YamlValue<T> {
    T data;
    YamlPath path;

    public YamlValue(T value, YamlPath path) {
        data = value;
        this.path = path;
    }

    public YamlMap asMap() {
        if(this instanceof YamlMap)
            return (YamlMap)this;
        return YamlUtils.objectToMapOrNull(data,path);
    }
    public YamlList asList() {
        if(this instanceof YamlList)
            return (YamlList)this;
        return YamlUtils.objectToListOrNull(data,path);
    }
    public YamlString asString() {
        if(this instanceof YamlString)
            return (YamlString)this;
        return YamlUtils.objectToStringOrNull(data, path);
    }

    public T getRawData() {
        return data;
    }
}
