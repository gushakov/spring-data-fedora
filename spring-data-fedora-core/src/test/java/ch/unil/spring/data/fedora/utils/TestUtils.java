package ch.unil.spring.data.fedora.utils;


import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.fcrepo.common.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gushakov
 */
public class TestUtils {

    private static final XpathEngine xpathEngine;

    static {
        Map<String, String> ns = new HashMap<String, String>();
        ns.put(Constants.FOXML.prefix, Constants.FOXML.uri);
        ns.put(Constants.RDF.prefix, Constants.RDF.uri);
        ns.put(Constants.FEDORA.prefix, Constants.RELS_EXT.uri);
        ns.put(Constants.OAI_DC.prefix, Constants.OAI_DC.uri);
        ns.put(Constants.DC.prefix, Constants.DC.uri);
        xpathEngine = XMLUnit.newXpathEngine();
        xpathEngine.setNamespaceContext(new SimpleNamespaceContext(ns));
    }

    public static XpathEngine getDefaultXpathEngine() {
        return xpathEngine;
    }


}
