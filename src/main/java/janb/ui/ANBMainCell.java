package janb.ui;

import janb.Action;
import janb.controllers.IController;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by michaelanderson on 24/12/2014.
 */
public class ANBMainCell {
    public final String content;
    public final List<Pair<String, Action>> contextMenu;
    public final IController controller;

    public ANBMainCell(String s, List<Pair<String, Action>> contextMenu, IController controller) {
        content = s;
        this.contextMenu = contextMenu;
        this.controller = controller;
    }
}
