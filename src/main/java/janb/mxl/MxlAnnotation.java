package janb.mxl;

/**
 * Created by michaelanderson on 6/02/2015.
 */
public class MxlAnnotation {
    private MxlTextLocation start;
    private MxlTextLocation end;
    private Object data;

    public MxlAnnotation(MxlTextLocation start, MxlTextLocation end, Object data) {
        this.start = start;
        this.end = end;
        this.data = data;
    }

    public MxlTextLocation getStart() {
        return start;
    }

    public MxlTextLocation getEnd() {
        return end;
    }

    public Object getData() {
        return data;
    }
}
