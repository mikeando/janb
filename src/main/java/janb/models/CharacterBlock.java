package janb.models;

import janb.util.ANBFile;

/**
 * Created by michaelanderson on 9/03/2015.
 */
public class CharacterBlock implements IEntityDB.ICharacterBlock {
    private final IEntityDB.EntityID id;
    ANBFile file;

    public CharacterBlock(IEntityDB.EntityID id, ANBFile file) {
        this.id = id;
        this.file = file;
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
}
