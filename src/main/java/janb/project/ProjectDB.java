package janb.project;

import janb.models.EntityID;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Everything is an DBField.
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
public class ProjectDB {

    public interface DBField {
        MutableDBField mutableCopy();
        ConstDBField constCopy();
        EntityID getLocation();
        void visit(EntityVisitor entityVisitor);
    }

    //TODO: Rename me
    public interface ConstDBField extends DBField {
        void visit(ConstEntityVisitor entityVisitor);

    }

    public interface MutableDBField extends DBField {

    }

    public static abstract class AbstractConstDBField implements ConstDBField {
        final EntityID location;

        public AbstractConstDBField(EntityID location) {
            this.location = location;
        }

        public EntityID getLocation() {
            return location;
        }

    }



    public static abstract class AbstractMutableDBField implements MutableDBField {
        private EntityID location;

        public AbstractMutableDBField(EntityID location) {
            this.location = location;
        }

        public EntityID getLocation() {
            return location;
        }
    }

    public interface TextField extends DBField {
        MutableTextField mutableCopy();
        ConstTextField constCopy();
        String getText();
    }

    /**
     * Stores actual textual data, with (optional) annotations.
     * Uses MXL for the text and annotations.
     */
    public static class ConstTextField extends AbstractConstDBField implements TextField {
        private final String text;

        public ConstTextField(EntityID location, String text) {
            super(location);
            this.text=text;
        }

        @Override
        public MutableTextField mutableCopy() {
            return new MutableTextField(getLocation(), text);
        }

        @Override
        public ConstTextField constCopy() {
            return this;
        }

        @Override
        public String getText() {
            return text;
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



    public static class MutableTextField extends AbstractMutableDBField implements TextField {

        private final String text;

        public MutableTextField(EntityID location, String text) {
            super(location);
            this.text = text;
        }

        @Override
        public MutableTextField mutableCopy() {
            return new MutableTextField(getLocation(), text);
        }

        @Override
        public ConstTextField constCopy() {
            return new ConstTextField(getLocation(), text);
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void visit(EntityVisitor entityVisitor) {
            entityVisitor.onText(this);
        }
    }

    public interface CollectionField extends DBField {
        MutableCollectionField mutableCopy();
        ConstCollectionField constCopy();
        DBField getField(String value);
        //TODO: Should we return more metadata than this?
        Map<String,? extends DBField> getFields();
        CollectionField getPrototype();
    }

    /**
     * Stores a collection of named and ordered EntityFields.
     * Often has a prototype too (with typing done through the prototype).
     */
    public static class ConstCollectionField extends AbstractConstDBField implements CollectionField {

        protected final ConstCollectionField prototype;
        protected final Map<String, ConstDBField> fields;

        public ConstCollectionField(EntityID location, Map<String, ConstDBField> fields, ConstCollectionField prototype) {
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

        private Map<String, DBField> toMutableMap(Map<String, ConstDBField> fields) {
            Map<String, DBField> result = new HashMap<>();
            for (Map.Entry<String, ConstDBField> kv : fields.entrySet()) {
                result.put(kv.getKey(), kv.getValue());
            }
            return result;
        }

        @Override
        public ConstCollectionField constCopy() {
            return this;
        }



        @Override
        public ConstDBField getField(String value) {
            ConstDBField f = fields.get(value);
            if(f!=null)
                return f;
            if(prototype==null)
                return null;
            return prototype.getField(value);
        }

        @Override
        public Map<String, ConstDBField> getFields() {
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
                for( ConstDBField f : fields.values()) {
                    f.visit(entityVisitor);
                }
            }
        }

        @Override
        public void visit(ConstEntityVisitor entityVisitor) {
            boolean visitChildren = entityVisitor.onCollection(this);
            if(visitChildren) {
                for( ConstDBField f : fields.values()) {
                    f.visit(entityVisitor);
                }
            }
        }
    }

    public static class MutableCollectionField extends AbstractMutableDBField implements CollectionField {


        protected final CollectionField prototype;
        protected final Map<String, DBField> fields;


        // Note we keep a copy of the map.
        public MutableCollectionField(EntityID location, Map<String, DBField> fields, CollectionField prototype) {
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
                for( DBField f : fields.values()) {
                    f.visit(entityVisitor);
                }
            }
        }

        private Map<String, ConstDBField> toConstMap(Map<String, DBField> fields) {
            Map<String,ConstDBField> result = new HashMap<>();
            for (Map.Entry<String, DBField> kv : fields.entrySet()) {
                final DBField value = kv.getValue();
                result.put(kv.getKey(), value.constCopy());
            }
            return result;
        }

        @Override
        public DBField getField(String value) {
            return fields.get(value);
        }

        @Override
        public Map<String, DBField> getFields() {
            return Collections.unmodifiableMap(fields);

        }

        @Override
        public CollectionField getPrototype() {
            return prototype;
        }
    }

    public interface ReferenceField extends DBField {
        MutableReferenceField mutableCopy();
        ConstReferenceField constCopy();
    }

    public static class ConstReferenceField extends AbstractConstDBField implements ReferenceField {

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

    public static class MutableReferenceField extends AbstractMutableDBField implements ReferenceField {
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
