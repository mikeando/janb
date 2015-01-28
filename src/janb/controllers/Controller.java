package janb.controllers;

import janb.models.DummyCategoryModel;
import janb.models.Model;
import janb.ui.ANBMainCell;
import janb.ui.ANBTreeCell;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import java.util.List;

public class Controller {

    @FXML
    public TextArea textarea;

    public Model model;

    @FXML
    public TreeView<ANBMainCell> treeView;


    @FXML
    protected void dumpModel(ActionEvent actionEvent) {
        model.dump();
        model.getCategories().add(new DummyCategoryModel());
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


    public static class TreeControllerListChangeListener implements ListChangeListener<ITreeController> {

        private ITreeController treeController;

        public TreeControllerListChangeListener(ITreeController treeController) {
            this.treeController = treeController;
        }

        @Override
        public void onChanged(Change<? extends ITreeController> c) {
            System.err.printf("Got change event ... %s : %s\n", c.getClass().getName(), c);

            while(c.next()) {

                //NOTE: Can be both removal and addition - need to do removal first
                if (c.wasRemoved()) {
                    onRemoved(c);
                }

                if (c.wasAdded()) {
                    onAdded(c);
                }

                if (c.wasPermutated()) {
                    System.err.printf("Was a permutation ... \n");
                }

                if (c.wasUpdated()) {
                    System.err.printf("Was an update ... \n");
                }
            }
        }

        protected void onRemoved(Change<? extends ITreeController> c) {
            final TreeItem<ANBMainCell> item = treeController.getItem();
            if(item !=null) {
                item.getChildren().remove(c.getFrom(), c.getTo());
            }
            final List<? extends ITreeController> removed = c.getRemoved();
            System.err.printf("Was a removal... %d - %d : %s \n", c.getFrom(), c.getTo(), removed);
            removed.forEach(treeController::removeChild);
        }

        protected void onAdded(Change<? extends ITreeController> c) {
            final TreeItem<ANBMainCell> item = treeController.getItem();

            System.err.printf("Was an addition... %d - %d : %s \n", c.getFrom(), c.getTo(), c.getAddedSubList());
            int i = c.getFrom();
            for( ITreeController x:  c.getAddedSubList()) {

                if(item !=null) {
                    item.getChildren().add(i, x.getOrBuildTreeItem());
                }

                treeController.addChild(i, x);
                ++i;
            }
        }

    }

}
