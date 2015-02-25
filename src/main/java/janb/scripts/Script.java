package janb.scripts;

import java.util.ArrayList;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public abstract class Script {

    private ScriptBinder binder;

    public BoundChoice getBoundChoice(String tag) {
        checkBinder();
        return binder.getBoundChoice(tag);
    }

    public ScriptUIDBuilder uidBuilder() {
        return new ScriptUIDBuilder(this);
    }

    public ScriptTextBuilder textBuilder() {
        return new ScriptTextBuilder(this);
    }

    public abstract void action();

    public abstract String getTitle();

    public void setBinder(ScriptBinder binder) {
        this.binder = binder;
    }

    public ScriptBinder getBinder() {
        return binder;
    }

    public void clearBinder() {
        binder=null;
    }

    public void checkBinder() {
        if(binder==null)
            throw new RuntimeException("Binder should not be null");
    }

    public void createUID(ArrayList<ScriptUIDBuilder.UIDElement> elements) {
        checkBinder();
        binder.setUID(elements);
    }

    public void addText(ArrayList<ScriptTextBuilder.TextElement> elements) {
        checkBinder();
        binder.addText(elements);
    }
}
