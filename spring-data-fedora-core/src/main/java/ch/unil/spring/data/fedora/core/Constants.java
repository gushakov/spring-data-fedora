package ch.unil.spring.data.fedora.core;

/**
 * @author gushakov
 */
public class Constants {

    public static final String PID_NAMESPACE_SEPARATOR = ":";
    public static final String PID_SEPARATOR = "_";

    public static final int PID_MAX_LENGTH = 64;

    public static final String DEFAULT_ANNOTATION_STRING_VALUE_TOKEN = "##default";

    public static final String DEFAULT_DATASTREAM_MIME_TYPE = "text/xml";

    public static final String DEFAULT_NAMESPACE_ID = "test";

    public static final String DEFAULT_DATASTREAM_VERSION_SUFFIX = ".0";

    public static final String RELS_EXT_DATASTREAM_ID = "RELS-EXT";

    public static final String RELS_EXT_DATASTREAM_MIME_TYPE = "application/rdf+xml";

    public static final String DEFAULT_RELS_EXT_DATASTREAM_LABEL = "Object external relations";

    public static final String FEDORA_URI_XML_ATTRIBUTE = "uri";

    public static final String XSTREAM_MAP_ENTRY_ELEMENT_NAME = "entry";

    public static final String XSTREAM_MAP_ENTRY_KEY_ATTRIBUTE_NAME = "key";

    public static final String XSTREAM_MAP_ENTRY_VALUE_ATTRIBUTE_NAME = "value";

    public enum Rels_Ext_Rdf_Property {
        HasPart("hasPart"),
        HasMember("hasMember");

        private String id;

        private Rels_Ext_Rdf_Property(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }
    }

}
