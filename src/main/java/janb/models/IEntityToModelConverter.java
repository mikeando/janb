package janb.models;

/**
 * Created by michaelanderson on 12/03/2015.
 */
public interface IEntityToModelConverter<T extends IModel> {
    public T toModel(Entity entity);
}
