package janb.models;

import janb.util.ANBFile;

/**
 * Created by michaelanderson on 9/03/2015.
 */
public class CharacterBlock implements IEntityDB.ICharacterBlock {
    private final IEntityDB.EntityID id;
    ANBFile file;
    private final IEntitySource.EntityType entityType;

    public CharacterBlock(IEntityDB.EntityID id, ANBFile file, IEntitySource.EntityType entityType) {
        this.id = id;
        this.file = file;
        this.entityType = entityType;
    }

    @Override
    public IEntityDB.EntityID id() {
        return id;
    }

    @Override
    public byte[] readContents() {
        return file.getFS().readFileContents(file);
    }

    @Override
    public void saveContents(byte[] data) {
        file.getFS().writeFileContents(file, data);
    }

    @Override
    public ANBFile getFile() {
        return file;
    }

    @Override
    public IEntitySource.EntityType getType() {
        return entityType;
    }
}
