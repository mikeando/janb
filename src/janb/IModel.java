package janb;

import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;

/**
 * A Marker interface.
 */
public interface IModel {
    public abstract String getTitle();

    //TODO: Remove this - and allow us to listen to something instead.
    //      since the ObservableList provides _write_ access to the list,
    //      which is not appropriate - since we'd prefer a more
    //      typesafe option than List<IModel> as the underlying data type.
    public abstract ObservableList<IModel> getEntries();

    //TODO: Probably need to be able to configure this more than a Pair will let us.
    public abstract List<Pair<String, Action>> getContextActions();
}
