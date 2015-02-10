package janb.mxl;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MxlTextTest {

    @Test
    public void testRemoveRange() {
        MxlText text = new MxlText("123456789");
        text.removeRange(1,3);
        assertEquals("156789",text.getData());
    }

    @Test
    public void testRemoveRangeNotifiesListeners() throws Exception {
        MxlText text = new MxlText("123456789");

        ArrayList<MxlTextEvent> events = new ArrayList<>();

        MxlText.MxlTextListener listener = events::add;

        text.addListener(listener);

        text.removeRange(1,3);
        assertEquals(1, events.size());
        assertThat( events.get(0), instanceOf(MxlTextRemovedEvent.class) );
        MxlTextRemovedEvent removedEvent = (MxlTextRemovedEvent) events.get(0);
        assertThat(removedEvent.source, sameInstance(text));
        assertEquals(1, removedEvent.start);
        assertEquals(3, removedEvent.length);
        assertEquals("234", removedEvent.textRemoved);
    }
}