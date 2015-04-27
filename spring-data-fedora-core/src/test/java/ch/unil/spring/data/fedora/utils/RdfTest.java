package ch.unil.spring.data.fedora.utils;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.junit.Test;

import java.io.StringReader;

/**
 * @author gushakov
 */
public class RdfTest {
    @Test
    public void testLiteral() throws Exception {

        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix(org.fcrepo.common.Constants.FEDORA.prefix, org.fcrepo.common.Constants.RELS_EXT.uri);

        Resource rdfAboutResource = model.createResource("info:fedora/test:foo");

        Property rdfRelProp = model.createProperty(org.fcrepo.common.Constants.RELS_EXT.uri,
                "hasMember");

        rdfRelProp.addLiteral(RDFS.comment, model.createLiteral("info:fedora/test:bar#address2"));
        rdfRelProp.addLiteral(RDFS.label, model.createLiteral("toto"));


        Resource rdfTargetResource = model.createResource("info:fedora/test:bar#address2");

        model.add(rdfAboutResource, rdfRelProp, rdfTargetResource);

        model.write(System.out);

    }

    @Test
    public void testReadEdf() throws Exception {
        String rdf = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:fedora=\"info:fedora/fedora-system:def/relations-external#\">" +
                "<rdf:Description rdf:about=\"info:fedora/test:personwithmultipleaddresses_1\">" +
                "<fedora:hasMember rdf:resource=\"info:fedora/test:address_1\"/>" +
                "<fedora:hasMember rdf:resource=\"info:fedora/test:foobar_1\"/>" +
                "</rdf:Description>" +
                "</rdf:RDF>";
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix(org.fcrepo.common.Constants.FEDORA.prefix, org.fcrepo.common.Constants.RELS_EXT.uri);
        model.read(new StringReader(rdf), null);

        Property rdfRelProp = model.createProperty(org.fcrepo.common.Constants.RELS_EXT.uri,
                "hasMember");

        StmtIterator stmtIter = model.getResource("info:fedora/test:personwithmultipleaddresses_1").listProperties(rdfRelProp);
        while (stmtIter.hasNext()) {
            Statement statement = stmtIter.nextStatement();
            System.out.println(statement.getSubject().getURI());
            System.out.println(statement.getPredicate().getLocalName());
            System.out.println(((Resource) statement.getObject().as(Resource.class)).getURI());
        }

    }
}
