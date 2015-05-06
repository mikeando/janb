package janb.models;

public interface Entity {
    public EntityID id();
    public EntityType getType();
    void setType(EntityType entityType);
}
