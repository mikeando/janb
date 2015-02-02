package janb.controllers;

import janb.models.IModel;

import java.util.ArrayList;
import java.util.List;

/**
* Created by michaelanderson on 20/01/2015.
*/
public class CategoryTreeControllerFactory implements TreeControllerFactory {

    @Override
    public ITreeController controllerForModel(IModel model) {

        System.err.printf("Getting controller for model %s\n", model);

        return new TreeController(this, model);
    }

    @Override
    public List<ITreeController> getChildControllersForModel(IModel model) {
        List<IModel> childModels = model.getChildModels();
        if(childModels==null)
            return null;
        List<ITreeController> childControllers = new ArrayList<>();
        for(IModel x : childModels) {
            childControllers.add(controllerForModel(x));
        }
        return childControllers;
    }
}
