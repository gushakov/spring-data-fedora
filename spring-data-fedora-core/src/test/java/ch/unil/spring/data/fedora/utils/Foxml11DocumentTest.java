package ch.unil.spring.data.fedora.utils;

import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author gushakov
 */
public class Foxml11DocumentTest {

    @Test
    public void testSerializeFoxml() throws Exception {
        Foxml11Document foxml = new Foxml11Document("test:1");
        foxml.addObjectProperty(Foxml11Document.Property.STATE, "A");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        foxml.serialize(os);
        os.flush();
        os.close();
        assertThat(TestUtils.getDefaultXpathEngine()
                        .evaluate("/foxml:digitalObject/@PID", XMLUnit.buildTestDocument(new String(os.toByteArray()))),
                is("test:1"));

    }

}
