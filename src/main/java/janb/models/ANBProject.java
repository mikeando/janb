package janb.models;

import java.util.List;

/**
* Created by michaelanderson on 24/03/2015.
*/
public interface ANBProject {
    boolean tryUpdate(Entity.EntityField entity);
    boolean trySave(Entity.EntityField entity);

    Entity.ConstEntityField getEntityById(EntityID id);

    List<Entity.ConstEntityField> getEntities();
}
