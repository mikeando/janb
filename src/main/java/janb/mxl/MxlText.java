package janb.mxl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 9/02/2015.
 */
public class MxlText {

    // TODO UGH. how to deal with this nicely - immutability of strings may be making this expensive.
    private String data;


    public String getData() {
        checkInternals();
        return data;
    }

    public static interface MxlTextListener {
        void onEvent(MxlTextEvent event);
    }

    public MxlTextLocation replaceFirstOccurrence(String value) throws MxlConstructionException {
        checkInternals();

        final int startPos = data.indexOf(value);
        if(startPos<0)
            throw new MxlConstructionException("Key '"+value+"' not found in content");

        removeRange(startPos, value.length());

        return getLocation(startPos);
    }

    public MxlTextLocation replaceFirstOccurrenceAfter(MxlTextLocation location, String value) throws MxlConstructionException {
        checkInternals();

        final int startPos = data.indexOf(value, location.location());
        if(startPos<0)
            throw new MxlConstructionException("Key '"+value+"' not found in content after "+location);

        removeRange(startPos, value.length());

        return getLocation(startPos);
    }

    public MxlTextLocation getLocation(int startPos) {
        checkInternals();

        final MxlTextLocation textLocation = new MxlTextLocation(startPos);
        this.addListener(textLocation);
        return textLocation;
    }

    public void addListener(MxlTextListener listener) {
        checkInternals();
        listeners.add(listener);
    }

    public void removeRange(int startPos, int length) {
        checkInternals();
        //TODO: Handle case where range isn't a subset.
        String beforeStart = data.substring(0,startPos);
        String removed = data.substring(startPos, startPos+length);
        String afterStart = data.substring(startPos+length, data.length());
        data = beforeStart + afterStart;

        //TODO: Should capture exceptions here in some way, so that a failing listener doesn't
        // break all the other listeners.
        for(MxlTextListener listener:listeners) {
            listener.onEvent(new MxlTextRemovedEvent(this,startPos,length, removed));
        }
    }

    List<MxlTextListener> listeners = new ArrayList<>();
    public MxlText(String data) {
        this.data = data;
        checkInternals();
    }

    private void checkInternals() {
        if(data==null)
            throw new RuntimeException("data is unexpectedly null");
    }
}
