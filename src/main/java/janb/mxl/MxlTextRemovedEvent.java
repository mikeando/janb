package janb.mxl;

/**
 * Created by michaelanderson on 10/02/2015.
 */
public class MxlTextRemovedEvent implements MxlTextEvent {
    public final MxlText source;
    public final int start;
    public final int length;
    public final String textRemoved;

    public MxlTextRemovedEvent(MxlText source, int start, int length, String textRemoved) {
        this.source = source;
        this.start = start;
        this.length = length;
        this.textRemoved = textRemoved;
    }
}
