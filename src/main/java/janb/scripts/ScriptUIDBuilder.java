package janb.scripts;

import java.util.ArrayList;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public class ScriptUIDBuilder {


    public static class UIDElement {

        public static UIDElement fromString(String key) {
            return new UIDElement();
        }

        public static UIDElement fromChoice(BoundChoice choice) {
            return new UIDElement();
        }
    }

    ArrayList<UIDElement> elements = new ArrayList<>();

    public ScriptUIDBuilder(Script script) {
    }

    public ScriptUIDBuilder add(String key) {
        elements.add(UIDElement.fromString(key));
        return this;
    }


    public ScriptUIDBuilder add(BoundChoice choice) {
        elements.add(UIDElement.fromChoice(choice));
        return this;
    }
}
