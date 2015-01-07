package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class LocationListModel extends CategoryModel {

    private List<LocationModel> entries = new ArrayList<>();

    LocationListModel() {
        entries.add( new LocationModel("Some Location"));
        entries.add( new LocationModel("Another Location"));
    }

    @Override
    public String getTitle() {
        return "Locations";
    }

    @Override
    public List<LocationModel> getEntries() {
        return entries;
    }
}
