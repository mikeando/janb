package janb.controllers;

import janb.models.IModel;

import java.util.List;

/**
* Created by michaelanderson on 20/01/2015.
*/
public interface TreeControllerFactory {
    ITreeController controllerForModel(IModel m);

    List<ITreeController> getChildControllersForModel(IModel model);
}
