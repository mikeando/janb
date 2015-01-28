package janb.models;

import janb.Action;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class CharacterModel implements IModel {
    private final String name;

    public CharacterModel(String name) {
        super();
        this.name = name;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public ObservableList<IModel> getEntries() {
        return null;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return null;
    }
}
