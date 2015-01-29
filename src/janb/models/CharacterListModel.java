package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class CharacterListModel extends AbstractModel {

    private List<CharacterModel> characters;

    CharacterListModel() {
        characters = new ArrayList<>();
        characters.add( new CharacterModel("Don"));
        characters.add( new CharacterModel("Jane"));
    }

    @Override
    public String getTitle() {
        return "Characters";
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return new ArrayList<>();
    }

    @Override
    public List<IModel> getChildModels() {
        return Collections.unmodifiableList(characters);
    }
}
