package sample;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by michaelanderson on 24/12/2014.
 */
public class Model {

    private final ObservableList<CategoryModel> categories;

    public final CharacterListModel characters = new CharacterListModel();
    public final FileListModel files = new FileListModel();
    public final LocationListModel locations = new LocationListModel();
    public final EventListModel events = new EventListModel();


    Model() {
        ArrayList<CategoryModel> tempCategories = new ArrayList<>();
        tempCategories.add(characters);
        tempCategories.add(files);
        tempCategories.add(locations);
        tempCategories.add(events);

        categories = new ObservableListWrapper<>(tempCategories);
    }

    public void dump() {
        System.err.printf("DUMPING MODEL\n");
    }

    public ObservableList<CategoryModel> getCategories() {
        return categories;
    }
}
