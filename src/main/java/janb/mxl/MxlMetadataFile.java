package janb.mxl;

import janb.yaml.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 4/02/2015.
 */
public class MxlMetadataFile implements IMxlMetadataFile {

    private MxlMetadataFile() {

    }



    public static interface MxlMetadataSource {

    }

    public static class MxlMetadataSourceFromFile implements MxlMetadataSource {
        private File source;

        public MxlMetadataSourceFromFile(File source) {
            this.source = source;
        }
    }

    //TODO: Make these final.
    private MxlMetadataSource source;
    private List<MxlUnboundAnnotation> annotations = new ArrayList<>();

    public static IMxlMetadataFile fromFile(File source) throws MxlConstructionException, IOException {
        try(FileInputStream is = new FileInputStream(source)) {
            return MxlMetadataFile.fromInputStream(is, new MxlMetadataSourceFromFile(source));
        }
    }

    public static IMxlMetadataFile fromInputStream(InputStream is, MxlMetadataSource source) throws MxlConstructionException {
        //TODO: This should be shifted into YamlUtils.
        Yaml yaml = new Yaml();
        //TODO: This is not a safe way to load YAML aparently....
        Object yamlData = yaml.load(is);
        return fromYaml(yamlData,source);
    }

    public static IMxlMetadataFile fromYaml(Object yamlData, MxlMetadataSource source) throws MxlConstructionException {
        MxlMetadataFile result = new MxlMetadataFile();
        result.source = source;

        if(yamlData==null)
            throw new MxlConstructionException(String.format("Unable to load YAML in .MXL file %s\n", source));

        try {
            YamlMap rootElement = YamlUtils.getRootAsMap(yamlData);

            rootElement.onAllChildren( new YamlMapCallback() {
                @Override
                public void onMap(String key, YamlMap value) {

                    System.err.printf("key=%s is a map : %s\n", key, value);
                }

                @Override
                public void onList(String key, YamlList value) throws MxlConstructionException {
                    if(key.equals("annotations")) {
                        result.buildAnnotations(value);
                        return;
                    }
                    System.err.printf("key=%s is a list : %s\n", key, value);
                }

                @Override
                public void onString(String key, YamlString value) throws MxlConstructionException {
                    throw new MxlConstructionException(
                            String.format("Error loading YAML in .mxl file %s : Non map element found in root for key %s", source, key)
                    );
                }

            });

            return result;
        } catch (YamlConversionException e) {
            throw new MxlConstructionException(
                    String.format("Error loading YAML in .mxl file '%s'", source),
                    e);
        }
    }

    public static IMxlMetadataFile empty() {
        return new MxlMetadataFile();
    }

    private void buildAnnotations(YamlList annotations) throws MxlConstructionException {
        for (int i = 0; i < annotations.size(); ++i) {
            try {
                YamlMap rawAnnotation = annotations.getChild(i).asMap();
                System.err.printf("Annotation %d = %s\n", i, rawAnnotation.getRawMap());
                MxlUnboundAnnotation ubannotation = new MxlUnboundAnnotation(
                        rawAnnotation.getChild("start").asString().getRawData(),
                        rawAnnotation.getChild("end").asString().getRawData(),
                        rawAnnotation.getChild("note").getRawData() // Should just pass the YamlObject I guess - or convert it.
                );

                this.addUnboundAnnotation(ubannotation);
                System.err.printf("UnboundAnnotation = %s\n", ubannotation);
            } catch (YamlConversionException e) {
                throw new MxlConstructionException(String.format("Invalid annotation annotation %d in file %s", i, source), e);
            }
        }
    }

    @Override
    public void addUnboundAnnotation(MxlUnboundAnnotation annotation) {
        annotations.add(annotation);
    }

    @Override
    public List<MxlUnboundAnnotation> getUnboundAnnotations() {
        return annotations;
    }
}
