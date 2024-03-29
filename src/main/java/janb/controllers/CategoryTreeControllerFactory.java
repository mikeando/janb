package janb.controllers;

import janb.models.IModel;

import java.util.List;
import java.util.stream.Collectors;

/**
* Created by michaelanderson on 20/01/2015.
*/
public class CategoryTreeControllerFactory implements TreeControllerFactory {


    private final IController rootController;

    CategoryTreeControllerFactory(IController rootController) {

        this.rootController = rootController;
    }

    @Override
    public ITreeController controllerForModel(IModel model) {

        System.err.printf("Getting controller for model %s\n", model);

        return new TreeController(this, model, rootController);
    }

    @Override
    public List<ITreeController> getChildControllersForModel(IModel model) {
        List<IModel> childModels = model.getChildModels();
        if(childModels==null)
            return null;
        return childModels.stream().map(this::controllerForModel).collect(Collectors.toList());
    }
}
