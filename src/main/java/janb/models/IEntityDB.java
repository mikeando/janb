package janb.models;

import janb.util.ANBFile;

import java.io.IOException;
import java.util.*;

/**
 * Abstraction layer for loading and saving of generalized "entities".
 */

public interface IEntityDB {

    public static class EntityID {

        EntityID(List<String> components) {
            id=new ArrayList<>(components);
        }

        EntityID() {
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
                if(!Objects.equals( childComponents.get(i), parentComponents.get(i)))
                    return false;
            }
            return true;
        }
    }

    public static interface ICharacterBlock {
        EntityID id();
        byte[] readContents() throws IOException;
        void saveContents(byte[] data) throws IOException;
        ANBFile getFile();

        IEntitySource.EntityType getType();
    }

    public static interface EntityDBEventListener {
        public void onEntityAdded(ICharacterBlock entity);
        public void onEntityRemoved(ICharacterBlock entity);
    }

    ICharacterBlock getEntityByID(EntityID name);

    List<ICharacterBlock> getAllEntities();

    ICharacterBlock createNewEntity();

    void saveEntity(ICharacterBlock entity);

    void addListener(EntityDBEventListener listener);
    void removeListener(EntityDBEventListener listener);
}
