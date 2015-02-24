package janb.models;

import janb.mxl.IMxlFile;

/**
 * Created by michaelanderson on 16/02/2015.
 *
 * Not sure this class really belongs as a model - its kinda half way between a controller and
 * a model - but provides a way for models to provide actions to change the main view.
 *
 * Not sure what the best way for models to get access to this class is either.
 */
public interface IViewModel {
    //Might need something part way between an MxlFile and an MxlText.
    void showContent(IMxlFile value);
}
