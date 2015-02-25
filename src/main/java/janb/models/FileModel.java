package janb.models;

import janb.Action;
import janb.controllers.IController;
import janb.mxl.IMxlFile;
import janb.mxl.MxlAnnotation;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class FileModel extends AbstractModel {
    private String title;
    IMxlFile file;
    final IViewModel viewModel;
    List<AnnotationModel> annotations = new ArrayList<>();

    public FileModel(IMxlFile file, IViewModel viewModel) {
        this.viewModel = viewModel;
        if(file==null)
            throw new NullPointerException("file can not be null");
        this.file = file;
        title = file.getBaseName();
        final List<MxlAnnotation> mxlAnnotations = file.getAnnotations();
        if(mxlAnnotations!=null) {
            for (MxlAnnotation mxlAnnotation : mxlAnnotations) {
                annotations.add(new AnnotationModel(mxlAnnotation));
            }
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<Pair<String, Action>> getContextActions() {
        final ArrayList<Pair<String, Action>> actions = new ArrayList<>();
        actions.add(new Pair<>("Open File", this::openFile));

        return actions;
    }

    //TODO: These need to be two-way bindings.
    private void openFile(IController iController) {
        viewModel.showContent(file);
    }


    @Override
    public List<IModel> getChildModels() {
        return new ArrayList<>(annotations);
    }
}
