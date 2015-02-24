package janb.scripts;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public abstract class Script {

    public BoundChoice getBoundChoice(String tag) {
        return new BoundChoice();
    }

    public ScriptUIDBuilder uidBuilder() {
        return new ScriptUIDBuilder(this);
    }

    public ScriptTextBuilder textBuilder() {
        return new ScriptTextBuilder(this);
    }

    abstract void action();

    public abstract String getTitle();
}
