package janb.scripts;

import javafx.collections.ObservableList;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public class BoundChoice {
    public final String name;
    public final ObservableList<String> values;

    public BoundChoice(String name, ObservableList<String> values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public String toString() {
        return "BoundChoice{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }
}
