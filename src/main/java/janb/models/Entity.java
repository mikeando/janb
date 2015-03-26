package janb.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Entity {

    EntityType type = null;

    public EntityID id() {
        throw new RuntimeException("NYI");
    }

    public EntityType getType() {
        return type;
    }


}
