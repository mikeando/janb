package janb.mxl;

import janb.yaml.*;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 4/02/2015.
 */
public class MxlMetadataFile {
    private File source;
    private List<MxlUnboundAnnotation> annotations = new ArrayList<>();

    public MxlMetadataFile(File source) throws MxlConstructionException {
        this.source = source;

        try(FileInputStream is = new FileInputStream(source)) {

            //TODO: This should be shifted into YamlUtils.
            Yaml yaml = new Yaml();
            //TODO: This is not a safe way to load YAML aparently....
            Object yamlData = yaml.load(is);
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
                            buildAnnotations(value, source);
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

            } catch (YamlConversionException e) {
                throw new MxlConstructionException(
                        String.format("Error loading YAML in .mxl file '%s'", source),
                        e);
            }
        } catch (IOException e){
            throw new MxlConstructionException(
                    String.format("Unable to load metadata file '%s'", source),
                    e
            );
        }
    }

    private void buildAnnotations(YamlList value, File source) throws MxlConstructionException {
        YamlList annotations = value;
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
        return;
    }

    public MxlMetadataFile() {
    }

    public void addUnboundAnnotation(MxlUnboundAnnotation annotation) {
        annotations.add(annotation);
    }

    public List<MxlUnboundAnnotation> getUnboundAnnotations() {
        return annotations;
    }
}
