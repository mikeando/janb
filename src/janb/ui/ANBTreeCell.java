package janb.ui;

import janb.Action;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;

import java.util.ArrayList;

/**
* Created by michaelanderson on 20/01/2015.
*/
public class ANBTreeCell extends TreeCell<ANBMainCell> {

    private ContextMenu contextMenu = new ContextMenu();


    private TextField textField;

    public ANBTreeCell() {
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
                setContextMenu(contextMenu);
            }

            final ObservableList<MenuItem> contextMenuItems = contextMenu.getItems();

            if(item.contextMenu!=null) {
                ArrayList<MenuItem> tempContextMenuItems = new ArrayList<>();
                for( Pair<String, Action> x : item.contextMenu ) {
                    MenuItem menuItem = new MenuItem(x.getKey());
                    tempContextMenuItems.add(menuItem);
                    EventHandler<ActionEvent> eh = event -> x.getValue().act();
                    menuItem.setOnAction(eh);
                }
                contextMenuItems.setAll(tempContextMenuItems);

            } else {
                contextMenuItems.clear();
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                commitEdit(new ANBMainCell(textField.getText(), null));

            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().content;
    }
}
