package janb.mxl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MxlTextLocationTest {

    @Test
    public void testUpdatesLocationOnRemoveMessageBefore() throws Exception {
        MxlTextLocation location = new MxlTextLocation(100);
        assertEquals(100, location.location());
        MxlText text = new MxlText("dummy");
        location.onEvent(new MxlTextRemovedEvent(text, 50, 25, textRepeat("a", 25)));
        assertEquals(75, location.location());
    }

    @Test
    public void testUpdatesLocationOnRemoveMessageAfter() throws Exception {
        MxlTextLocation location = new MxlTextLocation(100);
        assertEquals(100, location.location());
        MxlText text = new MxlText("dummy");
        location.onEvent(new MxlTextRemovedEvent(text, 150, 25, textRepeat("a", 25)));
        assertEquals(100, location.location());
    }

    @Test
    public void testUpdatesLocationOnRemoveMessageSpanning() throws Exception {
        MxlTextLocation location = new MxlTextLocation(100);
        assertEquals(100, location.location());
        MxlText text = new MxlText("dummy");
        location.onEvent(new MxlTextRemovedEvent(text, 80, 25, textRepeat("a", 25)));
        assertEquals(80, location.location());
    }


    private String textRepeat(String a, int count) {
        String result = "";
        for(int i=0; i<count; ++i) {
            result+=a;
        }
        return result;
    }
}