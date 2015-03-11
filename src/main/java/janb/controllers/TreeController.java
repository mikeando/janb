package janb.controllers;

import janb.Action;
import janb.models.IModel;
import janb.models.IModelEventListener;
import janb.models.ModelEvent;
import janb.ui.ANBMainCell;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by michaelanderson on 20/01/2015.
*/
public class TreeController implements ITreeController {

    protected final List<ITreeController> controllers;
    protected final IController rootController;

    private final TreeControllerFactory factory;
    protected String title = null;
    TreeItem<ANBMainCell> item = null;
    Map<IModel, ITreeController> modelToController = new HashMap<>();

    //Where is the model coming from?
    IModel model;

    TreeController(TreeControllerFactory factory, IModel model, IController rootController) {
        this.factory = factory;
        this.rootController = rootController;
        this.controllers = factory.getChildControllersForModel(model);
        this.title = model.getTitle();
        this.model = model;

        //TODO: When is this released? Is it a week reference?
        //TODO: If  a derived class overrides getListChangeListener, we wont get the right behaviour.
        //      since it wont be fully created yet.
        //      So we should probably create these through a factory method that can call an init function that
        //      will set up the listeners etc.
        //TODO: Calling this means that classes that override it can't be initialised yet -
        //      which will probably result in explosions later. So we probably need to split into
        //      two step construction with factory? (Unless we can get rid of the subclasses altogether
        //      which would be great.)
        IModelEventListener listener = getListChangeListener();
        model.addListener(listener);

        if (controllers != null) {
            for (ITreeController controller : controllers) {
                if (controller == null)
                    throw new NullPointerException("controller should not be null");
                final IModel childModel = controller.getModel();
                if (childModel != null) {
                    modelToController.put(childModel, controller);
                }
            }
        }
    }

    protected IModelEventListener getListChangeListener() {
        return new Controller.TreeControllerModelEventListener(this);
    }

    String getTitle() {
        return title;
    }

    @Override
    public final IModel getModel() {
        return model;
    }


    @Override
    public TreeControllerFactory getTreeControllerFactory() {
        return factory;
    }

    @Override
    public final List<ITreeController> getChildControllers() {
        return controllers;
    }

    @Override
    public void removeChild(ITreeController controller) {
        controllers.remove(controller);
        IModel model = controller.getModel();
        modelToController.remove(model);
    }

    @Override
    public void addChild(int index, ITreeController treeController) {
        controllers.add(index,treeController);
        IModel model = treeController.getModel();
        modelToController.put(model, treeController);
        if(item!=null) {
            item.getChildren().add(index, treeController.getOrBuildTreeItem());
        }
    }

    @Override
    public void onModelAddChild(ModelEvent.AddEvent event) {
        System.err.printf("in TreeController.onModelAddChild()");
        addChild(event.position, getTreeControllerFactory().controllerForModel(event.model));
    }

    @Override
    public IController getRootController() {
        return rootController;
    }

    @Override
    public final TreeItem<ANBMainCell> getItem() {
        return item;
    }

    @Override
    public TreeItem<ANBMainCell> getOrBuildTreeItem() {
        System.err.printf("Building item for title=%s\n", getTitle());
        if (item != null)
            return item;

        item = new TreeItem<>(new ANBMainCell(getTitle(), getContextActions(), getRootController()));
        if (controllers != null) {
            for (ITreeController controller : controllers) {
                item.getChildren().add(controller.getOrBuildTreeItem());
            }
        }
        return item;
    }



    protected List<Pair<String, Action>> getContextActions() {
        System.err.printf("Getting context actions for %s\n", this);
        final ArrayList<Pair<String, Action>> actions = new ArrayList<>();

        if(model!=null) {
            final List<Pair<String, Action>> modelContextActions = model.getContextActions();
            if(modelContextActions!=null) {
                actions.addAll(modelContextActions);
            }
        } else {
            System.err.printf(" --- odd model == null in %s\n", this);
        }
        actions.add( new Pair<>("Hello", controller -> System.err.printf("Hello...")) );
        System.err.printf("   actions are : %s\n", actions);
        return actions;
    }

    @Override
    public String toString() {
        return "{" + super.toString() + "title:\""+title+"\" : model:" + model + "}";
    }
}
