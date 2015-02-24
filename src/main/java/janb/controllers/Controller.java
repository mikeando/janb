package janb.controllers;

import janb.models.*;
import janb.mxl.IMxlFile;
import janb.mxl.MxlAnnotation;
import janb.mxl.MxlTextLocation;
import janb.ui.ANBMainCell;
import janb.ui.ANBStyle;
import janb.ui.ANBTreeCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.fxmisc.richtext.InlineStyleTextArea;

import java.util.List;

public class Controller {

    //TODO: Not sure this is perfect for our needs - but it at least
    //      supports better highlighting than the built-in javaFX options.

    public InlineStyleTextArea<ANBStyle> textArea;

    TextFlow textFlow;

    public Model model;

    @FXML
    public TreeView<ANBMainCell> treeView;
    public VBox textHolder;


    @FXML
    protected void dumpModel(ActionEvent actionEvent) {
        model.dump();
        model.addCategory(new DummyCategoryModel());
    }

    public void createTreeView() {

        //TODO: This should really be "this" I think.
        IController rootController = new IController() {

            @Override
            public void presentScript(ScriptModel script) {

                Stage stage = new Stage();

                Group root = new Group();
                Scene s = new Scene(root, 300, 300, Color.BLACK);
                Rectangle r = new Rectangle(25,25,250,250);
                r.setFill(Color.BLUE);
                root.getChildren().add(r);

                TextFlow textFlow = new TextFlow();
                textFlow.setPrefWidth(250);
                Text text1 = new Text("Some big red text ");
                text1.setFill(Color.RED);
                text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 40));
                ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList("First", "Second", "Third")
                );
                cb.getSelectionModel().select(0);

                Text text2 = new Text(" little bold orange text");
                text2.setFill(Color.ORANGE);
                text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
                textFlow.getChildren().add(text1);
                textFlow.getChildren().add(cb);
                textFlow.getChildren().add(text2);

                root.getChildren().add(textFlow);

                stage.setScene(s);
                stage.show();

                System.err.printf("Presenting script %s : %s\n", script.getTitle(), script.getScript());

            }
        };

        TreeControllerFactory factory = new CategoryTreeControllerFactory(rootController);
        ITreeController rootTreeController = factory.controllerForModel(model);
        TreeItem<ANBMainCell> rootItem = rootTreeController.getOrBuildTreeItem();

        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);

        Callback<TreeView<ANBMainCell>, TreeCell<ANBMainCell>> callback = (TreeView<ANBMainCell> param) -> new ANBTreeCell();
        treeView.setCellFactory(callback);

        //TODO: createTreeView needs to be called from an init function - where this non-tree code can be setup

        textArea = new InlineStyleTextArea<>(ANBStyle.defaultStyle, ANBStyle::toCss);

        ChangeListener<String> listener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.err.printf("Got a change to observable %s :\n   oldValue '%s'\n   new Value '%s'", observable, oldValue, newValue);
            }
        };
        textArea.textProperty().addListener(listener);
        ListChangeListener<? super CharSequence> paragraphListener = new ListChangeListener<CharSequence>() {
            @Override
            public void onChanged(Change<? extends CharSequence> c) {
                //TODO: Seems that we dont get the before and after state, only the after state.
                //      in both the before and after fields. (At least according to toString!)
                System.err.printf("Got a paragraph change : %s\n", c);
            }
        };

        textHolder.getChildren().add(textArea);

        textArea.getParagraphs().addListener(paragraphListener);
        textArea.setPrefWidth(500.0);

        //NOTE: This is what the InlineStyleTextArea uses internally.
//        Text text1 = new Text("Big italic red text");
//        text1.setFill(Color.RED);
//        text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 40));
//        Text text2 = new Text(" little bold blue text");
//        text2.setFill(Color.BLUE);
//        text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
//        TextFlow textFlow = new TextFlow(text1, text2);
//
//        textContent.getChildren().add(textFlow);
    }

    public IViewModel getViewModel() {

        IViewModel viewModel = new IViewModel() {
            @Override
            public void showContent(IMxlFile file) {

                textArea.replaceText(file.getText().getData());
                final List<MxlAnnotation> annotations = file.getAnnotations();
                for (MxlAnnotation annotation : annotations) {
                    final MxlTextLocation start = annotation.getStart();
                    final MxlTextLocation end = annotation.getEnd();
                    textArea.setStyle(start.location(), end.location(), ANBStyle.randomBold());
                }

                System.err.printf("on key pressed = %s\n", textArea.getOnKeyPressed());
                textArea.onKeyPressedProperty();
                EventHandler<KeyEvent> handler =  new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        System.err.printf("Got pressed event %s\n", event);
                    }
                };
                textArea.setOnKeyPressed(handler);

                //EventHandler<? super KeyEvent> ctrlS = EventHandlerHelper
                //                 .on(keyPressed(S, CONTROL_DOWN)).act(event -> { System.err.printf("CTRL_S\n"); })
                //                 .create();

                //EventHandlerHelper.install(textArea.onKeyPressedProperty(), ctrlS);

            }
        };
        return viewModel;
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
