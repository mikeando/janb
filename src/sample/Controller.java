package sample;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import sample.ui.ANBMainCell;

public class Controller {

    @FXML
    public TextArea textarea;

    protected Model model;

    @FXML
    public TreeView<ANBMainCell> treeview;


    @FXML
    protected void dumpModel(ActionEvent actionEvent) {
        model.dump();
        model.getCategories().add( new CategoryModel() );
    }

    public static class TestTreeCell extends TreeCell<ANBMainCell> {

        private ContextMenu contextMenu = new ContextMenu();


        private TextField textField;

        public TestTreeCell() {

            //TODO: Need to configure the menu to be more contexty.

            MenuItem menuItem = new MenuItem("Add Something");
            contextMenu.getItems().add(menuItem);
            EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TreeItem<ANBMainCell> item = new TreeItem<>( new ANBMainCell("New one"));
                    getTreeItem().getChildren().add(item);
                }
            };
            menuItem.setOnAction( eh );


        }

        @Override
        public void startEdit() {
            super.startEdit();
            if(textField==null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem().content);
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        public void updateItem(ANBMainCell item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if ( isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                    //TODO: Make this more context aware...
                    setContextMenu(contextMenu);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(t -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(new ANBMainCell(textField.getText()));

                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().content;
        }
    }


    //TODO: Really need some kind of tree model to back this.
    //      But it s not clear how that model should work yet.
    public void createTreeView() {

        TreeRootController rootController = new TreeRootController(model.getCategories());
        TreeItem<ANBMainCell> rootItem = rootController.buildItem();

        treeview.setRoot(rootItem);
        treeview.setShowRoot(false);

        Callback<TreeView<ANBMainCell>, TreeCell<ANBMainCell>> callback = (TreeView<ANBMainCell> param) -> new TestTreeCell();
        treeview.setCellFactory(callback);
    }

    private static class TreeRootController {
        private final ObservableList<CategoryModel> categories;
        final TreeItem<ANBMainCell> rootItem = new TreeItem<>(new ANBMainCell("Root"));

        public TreeRootController(ObservableList<CategoryModel> categories) {
            this.categories = categories;
            ListChangeListener<? super CategoryModel> listener = new ListChangeListener<CategoryModel>() {
                @Override
                public void onChanged(Change<? extends CategoryModel> c) {
                    System.err.printf("Got change event ... %s : %s\n", c.getClass().getName(), c);

                    while(c.next()) {
                        if (c.wasAdded()) {
                            System.err.printf("Was an addition... %d - %d : %s \n", c.getFrom(), c.getTo(), c.getAddedSubList());
                            int i = c.getFrom();
                            for( CategoryModel x:  c.getAddedSubList()) {
                                rootItem.getChildren().add(i, buildTreeItemForCategory(x));
                            }
                        }
                        //NOTE: Can be both removal and addition!
                        if (c.wasRemoved()) {
                            System.err.printf("Was a removal... %d - %d : %s \n", c.getFrom(), c.getTo(), c.getRemoved());
                        }

                        if (c.wasPermutated()) {
                            System.err.printf("Was a permutation ... \n");
                        }
                    }
                }
            };
            //TODO: When is this released? Is it a week reference?
            categories.addListener(listener);
        }

        public TreeItem<ANBMainCell> buildItem() {

            for( CategoryModel c : categories ) {
                rootItem.getChildren().add(buildTreeItemForCategory(c));
            }
            return rootItem;
        }

        private TreeItem<ANBMainCell> buildTreeItemForCategory(CategoryModel c) {
            TreeItem<ANBMainCell> item = new TreeItem<> (new ANBMainCell(c.getTitle()));
            for( EntryModel e : c.getEntries() ) {
                TreeItem<ANBMainCell> entry = new TreeItem<>( new ANBMainCell(e.getTitle()));
                item.getChildren().add(entry);
            }
            return item;
        }
    }
}
