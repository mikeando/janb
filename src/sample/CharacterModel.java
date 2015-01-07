package sample;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class CharacterModel extends EntryModel {
    private final String name;

    public CharacterModel(String name) {
        super();
        this.name = name;
    }

    @Override
    public String getTitle() {
        return name;
    }
}
