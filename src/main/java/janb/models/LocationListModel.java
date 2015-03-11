package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class LocationListModel extends AbstractModel {

    private final List<LocationModel> entries;

    LocationListModel() {
        entries = new ArrayList<>();
        entries.add(new LocationModel("Some Location"));
        entries.add(new LocationModel("Another Location"));
    }

    @Override
    public String getTitle() {
        return "Locations";
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        ArrayList<Pair<String,Action>> actions = new ArrayList<>();
        actions.add( new Pair<>("Add Location", (controller) -> this.addNew()));
        return actions;
    }

    @Override
    public List<IModel> getChildModels() {
        return Collections.unmodifiableList(entries);
    }

    private void addNew() {
        final LocationModel locationModel = new LocationModel("New Location");
        entries.add(locationModel);
        publishEvent( ModelEvent.addEvent(this, locationModel, entries.size()-1));
    }

}
