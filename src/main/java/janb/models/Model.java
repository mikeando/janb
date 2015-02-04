package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 24/12/2014.
 */
public class Model extends AbstractModel {

    public final CharacterListModel characters = new CharacterListModel();
    public final FileListModel files = new FileListModel();
    public final LocationListModel locations = new LocationListModel();
    public final EventListModel events = new EventListModel();
    List<IModel> extraCategories = new ArrayList<>();

    public Model() {
    }

    public void dump() {
        System.err.printf("DUMPING MODEL\n");
    }

    @Override
    public String getTitle() {
        return "Root";
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return null;
    }

    @Override
    public List<IModel> getChildModels() {
        ArrayList<IModel> models = new ArrayList<>();
        models.add(characters);
        models.add(files);
        models.add(locations);
        models.add(events);
        models.addAll(extraCategories);
        return Collections.unmodifiableList(models);
    }

    public void addCategory(IModel model) {
        extraCategories.add(model);
        publishEvent(ModelEvent.addEvent(this, model, getChildModels().size() - 1));
    }

    public void loadFromPath(File file) {
        files.loadFromPath(file);
    }
}
