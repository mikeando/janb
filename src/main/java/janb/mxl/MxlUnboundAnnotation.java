package janb.mxl;

/**
 * Created by michaelanderson on 4/02/2015.
 *
 * Represents an annotation in raw text.
 * It's location is determined by matching the start and end strings into the source text.
 * For now it carries raw data.
 */
public class MxlUnboundAnnotation {
    String start;
    String end;
    Object data;

    public MxlUnboundAnnotation(String start, String end, Object data) {
        this.start = start;
        this.end = end;
        this.data = data;
    }

    @Override
    public String toString() {
        return "MxlUnboundAnnotation{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", data=" + data +
                '}';
    }
}
