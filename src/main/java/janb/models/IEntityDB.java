package janb.models;

import janb.util.ANBFile;

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

            if (!id.equals(entityID.id)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        //TODO: Doesn't belong in here.
        public static String randomString() {
            Random r = new Random();

            char[] map = {
                    'a','b','c','d','e','f','g','h',
                    'i','j','k','l','m','n','o','p',
                    'q','r','s','t','u','v','w','x',
                    'y','z','0','1','2','3','4','5',
                    '6','7','8','9'};

            String id = "";
            for(int i=0;i<16;++i) {
                id += r.nextInt(map.length);
            }

            return id;
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
    }

    public static interface ICharacterBlock {
        EntityID id();
        byte[] readContents();
        void saveContents(byte[] data);
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
