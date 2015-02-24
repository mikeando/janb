package janb.models;

import janb.Action;
import janb.mxl.MxlAnnotation;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * Created by michaelanderson on 11/02/2015.
 */
public class AnnotationModel extends AbstractValueModel {
    private MxlAnnotation annotation;

    public AnnotationModel(MxlAnnotation annotation) {

        this.annotation = annotation;
    }

    @Override
    public String getTitle() {
        Object data = annotation.getData();
        if( data instanceof String) {
            return (String)data;
        }
        if( data instanceof HashMap) {
            Object title = ((HashMap)data).get("title");
            if(title==null) {
                title = ((HashMap)data).get("name");
            }
            if(title instanceof String) {
                return (String)title;
            }
            return "<<untitled annotation>>";
        }
        return "<<untitled annotation>>";
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return null;
    }
}
