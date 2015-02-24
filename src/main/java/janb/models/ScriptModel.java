package janb.models;

import janb.Action;
import janb.controllers.IController;
import janb.scripts.Script;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public class ScriptModel extends AbstractValueModel{
    private Script script;

    public static ScriptModel fromScript(Script script) {
        return new ScriptModel(script);
    }

    private ScriptModel(Script script) {

        this.script = script;
    }

    @Override
    public String getTitle() {
        return script.getTitle();
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        List<Pair<String, Action>> actions = new ArrayList<>();
        actions.add(new Pair<>("test", new Action() {
            @Override
            public void act(IController controller) {
                controller.presentScript(ScriptModel.this);
                System.err.printf("SCRIPT TEST\n");
            }
        }));

        return actions;
    }

    public Script getScript() {
        return script;
    }
}
