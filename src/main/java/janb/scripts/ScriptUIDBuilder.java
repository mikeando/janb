package janb.scripts;

import java.util.ArrayList;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public class ScriptUIDBuilder {


    private final Script script;

    public static class UIDElement {

        public UIDElement(String key, BoundChoice choice) {
            this.key = key;
            this.choice = choice;
        }

        public static UIDElement fromString(String key) {
            return new UIDElement(key, null);
        }

        public static UIDElement fromChoice(BoundChoice choice) {
            return new UIDElement(null, choice);
        }

        String key;
        BoundChoice choice;

        @Override
        public String toString() {
            return "UIDElement{" +
                    "key='" + key + '\'' +
                    ", choice=" + choice +
                    '}';
        }
    }

    ArrayList<UIDElement> elements = new ArrayList<>();

    public ScriptUIDBuilder(Script script) {
        this.script = script;
    }

    public ScriptUIDBuilder add(String key) {
        elements.add(UIDElement.fromString(key));
        return this;
    }

    public ScriptUIDBuilder add(BoundChoice choice) {
        elements.add(UIDElement.fromChoice(choice));
        return this;
    }

    public void done() {
        script.createUID(elements);
    }
}
