package janb.models;

/**
 * Created by michaelanderson on 17/03/2015.
 */
public interface EntitySourceListener {
    //TODO: Should these be separate types?
    abstract void onAddEntity(Entity entity);
    abstract void onAddEntityType(EntityType type);
}
