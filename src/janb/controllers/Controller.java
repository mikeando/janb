package janb.controllers;

import janb.models.DummyCategoryModel;
import janb.models.IModelEventListener;
import janb.models.Model;
import janb.models.ModelEvent;
import janb.ui.ANBMainCell;
import janb.ui.ANBTreeCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class Controller {

    @FXML
    public TextArea textarea;

    public Model model;

    @FXML
    public TreeView<ANBMainCell> treeView;


    @FXML
    protected void dumpModel(ActionEvent actionEvent) {
        model.dump();
        model.addCategory(new DummyCategoryModel());
    }


    //TODO: Really need some kind of tree model to back this.
    //      But it s not clear how that model should work yet.
    public void createTreeView() {

        TreeControllerFactory factory = new CategoryTreeControllerFactory();
        ITreeController rootController = factory.controllerForModel(model);
        TreeItem<ANBMainCell> rootItem = rootController.getOrBuildTreeItem();

        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);

        Callback<TreeView<ANBMainCell>, TreeCell<ANBMainCell>> callback = (TreeView<ANBMainCell> param) -> new ANBTreeCell();
        treeView.setCellFactory(callback);
    }


    public static class TreeControllerModelEventListener extends IModelEventListener {

        private ITreeController treeController;

        public TreeControllerModelEventListener(ITreeController treeController) {
            this.treeController = treeController;
        }

        @Override
        public void onEvent(ModelEvent event) {
            System.err.printf("Got change event ... %s : %s\n", event.getClass().getName(), event);
            System.err.printf("  on controller %s\n", treeController);
            if(event instanceof ModelEvent.AddEvent) {
                treeController.onModelAddChild((ModelEvent.AddEvent)event);
            }
        }

    }

}
