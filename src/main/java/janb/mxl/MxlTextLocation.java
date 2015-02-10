package janb.mxl;

/**
 * Created by michaelanderson on 9/02/2015.
 */
public class MxlTextLocation implements MxlText.MxlTextListener {

    private int location;

    public MxlTextLocation(int location) {
        this.location = location;
    }

    public int location() {
        return location;
    }


    @Override
    public void onEvent(MxlTextEvent event) {
        if(event instanceof MxlTextRemovedEvent) {
            MxlTextRemovedEvent removedEvent = (MxlTextRemovedEvent)event;
            if (removedEvent.start >= location)
                return;
            int end = removedEvent.start + removedEvent.length;
            if (end <= location) {
                location -= removedEvent.length;
                return;
            }
            location = removedEvent.start;
            return;
        }
        throw new RuntimeException(String.format("Unknown event %s", event));
    }
}
