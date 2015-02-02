package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class LocationModel extends AbstractValueModel {
    private final String title;

    public LocationModel(String title) {
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return null;
    }

}
