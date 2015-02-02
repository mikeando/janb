package janb.controllers;

import janb.models.IModel;
import janb.models.ModelEvent;
import janb.ui.ANBMainCell;
import javafx.scene.control.TreeItem;

import java.util.List;

/**
* Created by michaelanderson on 20/01/2015.
*/
public interface ITreeController {
    TreeItem<ANBMainCell> getItem();
    TreeItem<ANBMainCell> getOrBuildTreeItem();
    TreeControllerFactory getTreeControllerFactory();
    IModel getModel();
    List<ITreeController> getChildControllers();

    void removeChild(ITreeController controllers);

    void addChild(int index, ITreeController treeController);

    void onModelAddChild(ModelEvent.AddEvent event);
}
