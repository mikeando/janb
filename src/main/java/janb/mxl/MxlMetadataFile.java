package janb.mxl;

import janb.yaml.YamlUtils;
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


        //Later we want this file to be YAML format I think, but for now we'll just use a stupid text format.
        try(FileInputStream is = new FileInputStream(source)) {

            //TODO: This should be shifted into YamlUtils.
            Yaml yaml = new Yaml();
            //TODO: This is not a safe way to load YAML aparently....
            Object yamlData = yaml.load(is);
            if(yamlData==null)
                throw new MxlConstructionException(String.format("Unable to load YAML in .MXL file %s\n", source));



            try {
                YamlUtils.YamlMap rootElement = YamlUtils.getRootAsMap(yamlData);

                YamlUtils.YamlList annotations = rootElement.getChildList("annotations");
                for(int i=0; i<annotations.size(); ++i) {
                    YamlUtils.YamlMap rawAnnotation = annotations.getChildMap(i);
                    System.err.printf("Annotation %d = %s\n", i, rawAnnotation.getRawMap());
                    MxlUnboundAnnotation ubannotation = new MxlUnboundAnnotation(
                            rawAnnotation.getString("start"),
                            rawAnnotation.getString("end"),
                            rawAnnotation.getObject("note")
                    );

                    this.addUnboundAnnotation(ubannotation);
                    System.err.printf("UnboundAnnotation = %s\n", ubannotation);
                }
            } catch (YamlUtils.ConversionException e) {
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

    public MxlMetadataFile() {
    }

    public void addUnboundAnnotation(MxlUnboundAnnotation annotation) {
        annotations.add(annotation);
    }

    public List<MxlUnboundAnnotation> getUnboundAnnotations() {
        return annotations;
    }
}
