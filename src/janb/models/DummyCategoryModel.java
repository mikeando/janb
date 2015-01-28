package janb.models;

import com.sun.javafx.collections.ObservableListWrapper;
import janb.Action;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
* Created by michaelanderson on 13/01/2015.
*/
public class DummyCategoryModel implements IModel {


    ObservableList<IModel> entries;

    {
        final ArrayList<IModel> entries = new ArrayList<>();
        entries.add(new IModel() {
            @Override
            public String getTitle() {
                return "entry by dump..";
            }

            @Override
            public ObservableList<IModel> getEntries() {
                return null;
            }

            @Override
            public List<Pair<String, Action>> getContextActions() {
                return null;
            }
        });
        this.entries = new ObservableListWrapper<>(entries);
    }

    @Override
    public String getTitle() {
        return "Added by Dump";
    }

    @Override
    public ObservableList<IModel> getEntries() {
        return entries;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        ArrayList<Pair<String, Action>> actions = new ArrayList<>();
        actions.add(new Pair<>("Add Dummy", this::addNew));
        return actions;
    }

    private void addNew() {
    }
}
