package janb.models;

import janb.util.ANBFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides a little more context than the lower level IEntityDB
 *
 */
public interface IEntitySource {

    List<EntityType> getEntityTypes();

    EntityType getEntityTypeByShortName(String name);

    IEntityDB.ICharacterBlock createNewEntityOfType(EntityType characterEntityType, String name);

    IEntityDB.ICharacterBlock getEntityByName(String a_character);

    EntityType getEntityTypeByID(IEntityDB.EntityID idB);

    IEntityDB.ICharacterBlock getEntityById(IEntityDB.EntityID id);

    public static class EntityType {
        private final List<ANBFile> locations = new ArrayList<>(1);
        private final IEntityDB.EntityID id;

        public EntityType(IEntityDB.EntityID id) {
            this.id=id;
        }

        public List<String> components() {
            return id.components();
        }

        public String fullName() {
            return id.asString();
        }

        public String shortName() {
            return id.shortName();
        }

        public IEntityDB.EntityID id() {
            return id;
        }

        public List<ANBFile> getSourceLocations() {
            return Collections.unmodifiableList(locations);
        }

        public void addPath(ANBFile file) {
            locations.add(file);
        }
    }

    List<IEntityDB.ICharacterBlock> getEntitiesOfType(EntityType type);
    IEntityDB getDB();
}
