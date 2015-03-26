package janb.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Everything is an EntityField.
 *
 * Disk Layout:
 *  * Each directory denotes an entity.
 *  * Different types of Entity are determined by their "type" file.
 *
 *  * for a text type the file contains simply "text"
 *    the content is stored in the text.md file in the directory
 *    the meta-data is in the text.md.mxl file. There should be no
 *    other files. (if there are they are ignored)
 *
 *  * for a collection entity - the "type" file does not exist or
 *     contains "collection" or "collection:protottype_id".
 *     the rest of the content of the dir should be sub-entity directories.
 *     Any other files are ignored
 *
 *  * for an entity reference - type file contains "ref:reference_id"
 *    There should be not other content in the directory.
 *    If there is it is ignored.
 *
 *
 *  The default package structure is thus like:
 *
 *
 *  + package
 *     |- files
 *     |  |- chapter1
 *     |  |  |- type = "text"
 *     |  |  |- text.md = "This is chapter 1..."
 *     |  |  |- text.md.mxl = "..."
 *     |  |- chapter2
 *     |     |- type = "text"
 *     |     |- text.md = "This is chapter 1..."
 *     |     |- text.md.mxl = "..."
 *     |- entities
 *     |  |- character
 *     |     |- ed
 *     |       |- type = "collection:prototypes.character
 *     |       |- full_name
 *     |       |   |- type = "text"
 *     |       |   |- text.md = "Ed Foozle"
 *     |       |   |- text.md.mxl = "...."
 *     |       |- job
 *     |       |  |- type = "text"
 *     |       |  |- text.md = "Baker"
 *     |       |  |- text.md.mxl = "..."
 *     |- prototypes
 *        |- character
 *            |- full_name
 *            |   |- type = "text"
 *            |   |- text.md = "No Name"
 *            |   |- text.md.mxl = "..."
 *            |- job (...)
 *            |- age (...)
 *
 *
 * Seems like pretty quickly we'll want a way to specify all those texty bits in a more compact form.
 * Three files and a directory for every attribute means a lot of IO.
 *
 * Probably just a json representation of exactly that.
 *
 * ed
 *  |- attributes.json = '{"full_name":{"type":"text","text":"No Name","mxl":null}, ...}
 *
 * But I guess that can come later....
 * And at least the current aim is nicely trackable with git...
 *
 *
 */
public class Entity {

    EntityType type = null;

    public EntityID id() {
        throw new RuntimeException("NYI");
    }

    public EntityType getType() {
        return type;
    }

    public interface EntityField {
        MutableEntityField mutableCopy();
        ConstEntityField constCopy();
        EntityID getLocation();
        void visit(EntityVisitor entityVisitor);
    }

    public interface ConstEntityField extends EntityField {
        void visit(ConstEntityVisitor entityVisitor);

    }

    public interface MutableEntityField extends EntityField {

    }

    public static abstract class AbstractConstEntityField implements ConstEntityField {
        final EntityID location;

        public AbstractConstEntityField(EntityID location) {
            this.location = location;
        }

        public EntityID getLocation() {
            return location;
        }

    }



    public static abstract class AbstractMutableEntityField implements MutableEntityField {
        private EntityID location;

        public AbstractMutableEntityField(EntityID location) {
            this.location = location;
        }

        public EntityID getLocation() {
            return location;
        }
    }

    public static interface TextField extends EntityField {
        public MutableTextField mutableCopy();
        public ConstTextField constCopy();
    }

    /**
     * Stores actual textual data, with (optional) annotations.
     * Uses MXL for the text and annotations.
     */
    public static class ConstTextField extends AbstractConstEntityField implements TextField {
        public ConstTextField(EntityID location) {
            super(location);
        }

        @Override
        public MutableTextField mutableCopy() {
            return new MutableTextField(getLocation());
        }

        @Override
        public ConstTextField constCopy() {
            return this;
        }

        @Override
        public void visit(EntityVisitor entityVisitor) {
            entityVisitor.onText(this);
        }

        @Override
        public void visit(ConstEntityVisitor entityVisitor) {
            entityVisitor.onText(this);
        }
    }



    public static class MutableTextField extends AbstractMutableEntityField implements TextField {

        public MutableTextField(EntityID location) {
            super(location);
        }

        @Override
        public MutableTextField mutableCopy() {
            return new MutableTextField(getLocation());
        }

        @Override
        public ConstTextField constCopy() {
            return new ConstTextField(getLocation());
        }

        @Override
        public void visit(EntityVisitor entityVisitor) {
            entityVisitor.onText(this);
        }
    }

    public static interface CollectionField extends EntityField {
        public MutableCollectionField mutableCopy();
        public ConstCollectionField constCopy();
        EntityField getField(String value);
        //TODO: Should we return more metadata than this?
        Map<String,? extends EntityField> getFields();
        public CollectionField getPrototype();
    }

    /**
     * Stores a collection of named and ordered EntityFields.
     * Often has a prototype too (with typing done through the prototype).
     */
    public static class ConstCollectionField extends AbstractConstEntityField implements CollectionField {

        protected final ConstCollectionField prototype;
        protected final Map<String, ConstEntityField> fields;

        public ConstCollectionField(EntityID location, Map<String, ConstEntityField> fields, ConstCollectionField prototype) {
            super(location);
            // Gotta take a defensive copy in case the caller changes it...
            // it doesn't need to be a deep copy since its elements must already be constant.
            this.fields = new HashMap<>(fields);
            this.prototype = prototype;
        }

        @Override
        public MutableCollectionField mutableCopy() {
            return new MutableCollectionField(getLocation(), toMutableMap(fields), prototype);
        }

        private Map<String, EntityField> toMutableMap(Map<String, ConstEntityField> fields) {
            Map<String, EntityField> result = new HashMap<>();
            for (Map.Entry<String, ConstEntityField> kv : fields.entrySet()) {
                result.put(kv.getKey(), kv.getValue());
            }
            return result;
        }

        @Override
        public ConstCollectionField constCopy() {
            return this;
        }



        @Override
        public ConstEntityField getField(String value) {
            ConstEntityField f = fields.get(value);
            if(f!=null)
                return f;
            if(prototype==null)
                return null;
            return prototype.getField(value);
        }

        @Override
        public Map<String, ConstEntityField> getFields() {
            return Collections.unmodifiableMap(fields);
        }

        @Override
        public ConstCollectionField getPrototype() {
            return prototype;
        }

        @Override
        public void visit(EntityVisitor entityVisitor) {
            boolean visitChildren = entityVisitor.onCollection(this);
            if(visitChildren) {
                for( ConstEntityField f : fields.values()) {
                    f.visit(entityVisitor);
                }
            }
        }

        @Override
        public void visit(ConstEntityVisitor entityVisitor) {
            boolean visitChildren = entityVisitor.onCollection(this);
            if(visitChildren) {
                for( ConstEntityField f : fields.values()) {
                    f.visit(entityVisitor);
                }
            }
        }
    }

    public static class MutableCollectionField extends AbstractMutableEntityField implements CollectionField {


        protected final CollectionField prototype;
        protected final Map<String, EntityField> fields;


        // Note we keep a copy of the map.
        public MutableCollectionField(EntityID location, Map<String, EntityField> fields, CollectionField prototype) {
            super(location);
            this.fields = new HashMap<>(fields);
            this.prototype = prototype;
        }

        @Override
        public MutableCollectionField mutableCopy() {
            return new MutableCollectionField(getLocation(), fields, prototype);
        }

        @Override
        public ConstCollectionField constCopy() {
            return new ConstCollectionField(getLocation(), toConstMap(fields), prototype.constCopy() );
        }

        @Override
        public void visit(EntityVisitor entityVisitor) {
            boolean visitChildren = entityVisitor.onCollection(this);
            if(visitChildren) {
                for( EntityField f : fields.values()) {
                    f.visit(entityVisitor);
                }
            }
        }

        private Map<String, ConstEntityField> toConstMap(Map<String, EntityField> fields) {
            Map<String,ConstEntityField> result = new HashMap<>();
            for (Map.Entry<String, EntityField> kv : fields.entrySet()) {
                final EntityField value = kv.getValue();
                result.put(kv.getKey(), value.constCopy());
            }
            return result;
        }

        @Override
        public EntityField getField(String value) {
            return fields.get(value);
        }

        @Override
        public Map<String, EntityField> getFields() {
            return Collections.unmodifiableMap(fields);

        }

        @Override
        public CollectionField getPrototype() {
            return prototype;
        }
    }

    public interface ReferenceField extends EntityField {
        public MutableReferenceField mutableCopy();
        public ConstReferenceField constCopy();
    }

    public static class ConstReferenceField extends AbstractConstEntityField implements ReferenceField {

        public ConstReferenceField(EntityID location) {
            super(location);
        }

        @Override
        public MutableReferenceField mutableCopy() {
            return new MutableReferenceField(getLocation());
        }

        @Override
        public ConstReferenceField constCopy() {
            return this;
        }

        @Override
        public void visit(EntityVisitor entityVisitor) {
            entityVisitor.onReference(this);
        }

        @Override
        public void visit(ConstEntityVisitor entityVisitor) {
            entityVisitor.onReference(this);
        }
    }

    public static class MutableReferenceField extends AbstractMutableEntityField implements ReferenceField {
        public MutableReferenceField(EntityID location) {
            super(location);
        }

        @Override
        public MutableReferenceField mutableCopy() {
            return new MutableReferenceField(getLocation());
        }

        @Override
        public ConstReferenceField constCopy() {
            return new ConstReferenceField(getLocation());
        }

        @Override
        public void visit(EntityVisitor entityVisitor) {
            entityVisitor.onReference(this);
        }
    }

    // Not a valid value, used to implement the null-value pattern.
    //public static class NullField implements EntityField {
    //
    //}

    public interface EntityVisitor {

        boolean onCollection(CollectionField cf);

        void onText(TextField tf);

        void onReference(ReferenceField rf);
    }

    public interface ConstEntityVisitor {

        boolean onCollection(ConstCollectionField cf);

        void onText(ConstTextField tf);

        void onReference(ConstReferenceField rf);
    }

}
