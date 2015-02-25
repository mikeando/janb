package janb.scripts;

import java.util.ArrayList;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public class ScriptTextBuilder {
    private Script script;
    private ArrayList<TextElement> elements = new ArrayList<>();

    public static class TextElement {
        public static TextElement fromString(String text) {
                return new TextElement(text, null);
            }
        public static TextElement fromChoice(BoundChoice choice) {
                return new TextElement(null, choice);
            }

        public String text;
        public BoundChoice choice;

        public TextElement(String text, BoundChoice choice) {
            this.text = text;
            this.choice = choice;
        }

        @Override
        public String toString() {
            return "TextElement{" +
                    "text='" + text + '\'' +
                    ", choice=" + choice +
                    '}';
        }
    }

    public ScriptTextBuilder(Script script) {

        this.script = script;
    }

    public ScriptTextBuilder add(String text) {
        elements.add(TextElement.fromString(text));
        return this;
    }

    public ScriptTextBuilder add(BoundChoice choice) {
        elements.add(TextElement.fromChoice(choice));
        return this;
    }

    public void done() {
        script.addText(elements);
    }
}
