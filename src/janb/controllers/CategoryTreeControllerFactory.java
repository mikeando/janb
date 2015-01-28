package janb.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import janb.IModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* Created by michaelanderson on 20/01/2015.
*/
public class CategoryTreeControllerFactory implements TreeControllerFactory {

    @Override
    public ITreeController controllerForModel(IModel model) {

        System.err.printf("Getting controller for model %s\n", model);

        return new TreeController(this, model);
    }

    @Override
    public ObservableList<ITreeController> mapObservable(ObservableList<? extends IModel> entries) {
        if(entries==null)
            return null;

        List<ITreeController> controllers = entries.stream()
               .map(this::controllerForModel)
               .filter(x -> (x != null))
               .collect(Collectors.toCollection(ArrayList::new));

        final ObservableListWrapper<ITreeController> treeControllers = new ObservableListWrapper<>(controllers);
        entries.addListener( new ListChangeListener<IModel>() {
            @Override
            public void onChanged(Change<? extends IModel> c) {
                System.err.printf("Implement mapObservable ListChangeListener.onChanged()");
            }
        });
        return treeControllers;
    }
}
