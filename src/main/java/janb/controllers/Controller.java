package janb.controllers;

import janb.models.*;
import janb.mxl.IMxlFile;
import janb.mxl.MxlAnnotation;
import janb.mxl.MxlTextLocation;
import janb.scripts.*;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.fxmisc.richtext.InlineStyleTextArea;

import java.util.ArrayList;
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
            public void presentScript(ScriptModel scriptModel) {

                Stage stage = new Stage();

                Group root = new Group();
                Scene s = new Scene(root, 300, 300, Color.WHITE);

                TextFlow textFlow = new TextFlow();
                textFlow.setPrefWidth(250);
                root.getChildren().add(textFlow);

                stage.setScene(s);
                stage.show();

                final Script script = scriptModel.getScript();
                System.err.printf("Presenting script %s : %s\n", scriptModel.getTitle(), script);

                ScriptBinder binding = new ScriptBinder() {
                    @Override
                    public BoundChoice getBoundChoice(String tag) {
                        System.err.printf("Getting bound choice for %s\n", tag);
                        return new BoundChoice("culture", FXCollections.observableArrayList("a culture", "another culture"));
                    }

                    @Override
                    public void setUID(ArrayList<ScriptUIDBuilder.UIDElement> elements) {
                        System.err.printf("Setting UID to %s\n", elements);
                    }

                    @Override
                    public void addText(ArrayList<ScriptTextBuilder.TextElement> elements) {
                        System.err.printf("Adding text : %s\n", elements);

                        for(ScriptTextBuilder.TextElement x:elements) {
                            if(x.text!=null) {
                                Text text = new Text(x.text);
                                //text.setFill(Color.ORANGE);
                                //text.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
                                textFlow.getChildren().add(text);
                            }
                            if(x.choice!=null) {
                                ChoiceBox<String> cb = new ChoiceBox<>(x.choice.values);
                                cb.getSelectionModel().select(0);
                                textFlow.getChildren().add(cb);
                            }
                        }
                    }
                };

                script.setBinder(binding);
                script.action();
                script.clearBinder();

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

        ChangeListener<String> listener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> System.err.printf("Got a change to observable %s :\n   oldValue '%s'\n   new Value '%s'", observable, oldValue, newValue);
        textArea.textProperty().addListener(listener);
        ListChangeListener<? super CharSequence> paragraphListener = (ListChangeListener.Change<? extends CharSequence> c) -> System.err.printf("Got a paragraph change : %s\n", c);

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

        return new IViewModel() {
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
                EventHandler<KeyEvent> handler = (KeyEvent event) -> System.err.printf("Got pressed event %s\n", event);
                textArea.setOnKeyPressed(handler);

                //EventHandler<? super KeyEvent> ctrlS = EventHandlerHelper
                //                 .on(keyPressed(S, CONTROL_DOWN)).act(event -> { System.err.printf("CTRL_S\n"); })
                //                 .create();

                //EventHandlerHelper.install(textArea.onKeyPressedProperty(), ctrlS);

            }
        };
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
