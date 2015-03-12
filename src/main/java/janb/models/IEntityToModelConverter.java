package janb.models;

/**
 * Created by michaelanderson on 12/03/2015.
 */
public interface IEntityToModelConverter {
    public EventModel toModel(IEntityDB.ICharacterBlock event);
}
