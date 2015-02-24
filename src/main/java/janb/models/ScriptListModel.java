package janb.models;

import janb.Action;
import janb.scripts.Death;
import janb.scripts.Script;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class ScriptListModel extends AbstractModel {

    private final List<ScriptModel> entries;

    ScriptListModel() {
        entries = new ArrayList<>();
        entries.add(ScriptModel.fromScript(new Death()));
    }

    @Override
    public String getTitle() {
        return "Scripts";
    }


    @Override
    public List<Pair<String, Action>> getContextActions() {
        return new ArrayList<>();
    }

    @Override
    public List<IModel> getChildModels() {
        return Collections.unmodifiableList(entries);
    }
}
