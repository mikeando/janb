package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class CharacterListModel extends CategoryModel {

    private List<CharacterModel> characters = new ArrayList<>();

    CharacterListModel() {
        characters.add( new CharacterModel("Don"));
        characters.add( new CharacterModel("Jane"));
    }

    @Override
    public String getTitle() {
        return "Characters";
    }

    @Override
    public List<CharacterModel> getEntries() {
        return characters;
    }
}
