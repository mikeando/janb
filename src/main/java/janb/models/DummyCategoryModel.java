package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* Created by michaelanderson on 13/01/2015.
*/
public class DummyCategoryModel extends AbstractModel {


    List<DummyValueModel> entries = new ArrayList<>();

    public DummyCategoryModel() {
        entries.add(new DummyValueModel());
    }

    @Override
    public String getTitle() {
        return "Added by Dump";
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        ArrayList<Pair<String, Action>> actions = new ArrayList<>();
        actions.add(new Pair<>("Add Dummy", this::addNew));
        return actions;
    }

    @Override
    public List<IModel> getChildModels() {
        return Collections.unmodifiableList(entries);
    }

    private void addNew() {
        final DummyValueModel dummyValueModel = new DummyValueModel();
        entries.add(dummyValueModel);
        publishEvent(ModelEvent.addEvent(this,dummyValueModel, entries.size()-1));
    }

    private static class DummyValueModel extends AbstractValueModel {
        @Override
        public String getTitle() {
            return "entry by dump..";
        }

        @Override
        public List<Pair<String, Action>> getContextActions() {
            return null;
        }
    }
}
