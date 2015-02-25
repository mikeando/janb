package janb.scripts;

import java.util.ArrayList;

/**
 * Created by michaelanderson on 25/02/2015.
 */
public interface ScriptBinder {
    BoundChoice getBoundChoice(String tag);

    void setUID(ArrayList<ScriptUIDBuilder.UIDElement> elements);

    void addText(ArrayList<ScriptTextBuilder.TextElement> elements);
}
