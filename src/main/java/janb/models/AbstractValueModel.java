package janb.models;

import java.util.List;

/**
 * Created by michaelanderson on 29/01/2015.
 */
public abstract class AbstractValueModel extends AbstractModel {
    @Override
    public List<IModel> getChildModels() {
        return null;
    }
}
