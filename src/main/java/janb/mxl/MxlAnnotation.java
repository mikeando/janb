package janb.mxl;

/**
 * Created by michaelanderson on 6/02/2015.
 */
public class MxlAnnotation {
    private MxlTextLocation start;
    private MxlTextLocation end;

    public MxlAnnotation(MxlTextLocation start, MxlTextLocation end) {
        this.start = start;
        this.end = end;
    }

    public MxlTextLocation getStart() {
        return start;
    }

    public MxlTextLocation getEnd() {
        return end;
    }
}
