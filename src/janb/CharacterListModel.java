package janb;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class CharacterListModel implements IModel {

    private ObservableList<IModel> characters;

    CharacterListModel() {
        ArrayList<IModel> entries = new ArrayList<>();
        entries.add( new CharacterModel("Don"));
        entries.add( new CharacterModel("Jane"));
        characters = new ObservableListWrapper<>(entries);
    }

    @Override
    public String getTitle() {
        return "Characters";
    }

    @Override
    public ObservableList<IModel> getEntries() {
        return characters;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return new ArrayList<>();
    }
}
