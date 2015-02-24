package janb.controllers;

import janb.models.ScriptModel;

/**
 * TODO: Not sure this belongs in the controllers package - it's more  a generalization of controllers
 * for use in the models where needed.
 */
public abstract interface IController {
    public abstract void presentScript(ScriptModel script);
}
