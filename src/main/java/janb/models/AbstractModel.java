package janb.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 29/01/2015.
 */
public abstract class AbstractModel implements IModel {

    protected List<IModelEventListener> listeners = new ArrayList<>();

    @Override
    public void addListener(IModelEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IModelEventListener listener) {
        listeners.remove(listener);
    }

    protected void publishEvent(ModelEvent modelEvent) {
        for(IModelEventListener listener : listeners) {
            listener.onEvent(modelEvent);
        }
    }
}
