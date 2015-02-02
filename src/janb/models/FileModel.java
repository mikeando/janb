package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class FileModel extends AbstractValueModel {
    private String title;

    public FileModel(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        return null;
    }
}
