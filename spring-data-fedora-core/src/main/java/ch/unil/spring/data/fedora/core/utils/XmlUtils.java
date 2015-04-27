package ch.unil.spring.data.fedora.core.utils;

import java.io.ByteArrayOutputStream;

/**
 * @author gushakov
 */
public class XmlUtils {

    public static byte[] serialize(Foxml11Document foxmlDoc) throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            foxmlDoc.serialize(os);
            os.flush();
            return os.toByteArray();
        }
    }
}
