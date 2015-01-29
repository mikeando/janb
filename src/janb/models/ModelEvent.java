package janb.models;

/**
 * Created by michaelanderson on 29/01/2015.
 */
public class ModelEvent {
    public static ModelEvent addEvent(IModel parent, IModel model, int position) {
        return new ModelEvent.AddEvent(parent, model, position);
    }

    public static class AddEvent extends ModelEvent {
        public final IModel parent;
        public final IModel model;
        public final int position;

        public AddEvent(IModel parent, IModel model, int position) {
            super();
            this.parent = parent;
            this.model = model;
            this.position = position;
        }
    }
}
