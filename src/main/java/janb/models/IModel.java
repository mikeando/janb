package janb.models;

import janb.Action;
import javafx.util.Pair;

import java.util.List;


public interface IModel {
    public abstract String getTitle();

    public void addListener( IModelEventListener listener );
    public void removeListener(IModelEventListener listener);

    //TODO: Probably need to be able to configure this more than a Pair will let us.
    public abstract List<Pair<String, Action>> getContextActions();

    List<IModel> getChildModels();
}
