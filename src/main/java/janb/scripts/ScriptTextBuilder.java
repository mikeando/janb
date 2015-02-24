package janb.scripts;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public class ScriptTextBuilder {
    private Script script;

    public ScriptTextBuilder(Script script) {

        this.script = script;
    }

    public ScriptTextBuilder add(String text) {
        return this;
    }

    public ScriptTextBuilder add(BoundChoice choice) {
        return this;
    }
}
