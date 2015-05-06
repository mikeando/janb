package janb.models;

import java.util.*;

/**
 * Note this class is designed to be immutable. Dont change that.
 * It will break much.
 */
public class EntityID {

    public EntityID(List<String> components) {
        id=new ArrayList<>(components);
    }

    public EntityID() {
        id=new ArrayList<>();
    }

    private final ArrayList<String> id;

    public EntityID child(String name) {
        EntityID child = new EntityID(id);
        child.id.add(name);
        return child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityID entityID = (EntityID) o;

        return id.equals(entityID.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static EntityID fromComponents(String... components) {
        return new EntityID(Arrays.asList(components));
    }

    public List<String> components() {
        return Collections.unmodifiableList(id);
    }

    public String asString() {
        return String.join(".", id);
    }

    @Override
    public String toString() {
        return "EntityID{"+asString()+"}";
    }

    public String shortName() {
        if(id.size()==0)
            return "";
        return id.get(id.size()-1);
    }

    public static boolean isDirectChild(EntityID idParent, EntityID idChild) {
        if(idChild.id.size()!=idParent.id.size()+1)
            return false;
        ArrayList<String> parentComponents = idParent.id;
        ArrayList<String> childComponents = idChild.id;
        for (int i = 0; i < parentComponents.size(); i++) {
            if(!Objects.equals(childComponents.get(i), parentComponents.get(i)))
                return false;
        }
        return true;
    }

    public EntityID parent() {
        //TODO: Handle null and/or empty id
        if(id==null || id.size()==0) {
            return new EntityID();
        }
        return new EntityID(id.subList(0,id.size()-1));
    }

    public EntityID prepend(String prefix) {
        ArrayList<String> components=new ArrayList<>();
        components.add(prefix);
        components.addAll(id);
        return new EntityID(components);
    }
}
