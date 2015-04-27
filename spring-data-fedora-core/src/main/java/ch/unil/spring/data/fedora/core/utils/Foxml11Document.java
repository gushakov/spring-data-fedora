package ch.unil.spring.data.fedora.core.utils;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.convert.FedoraConverter;
import ch.unil.spring.data.fedora.core.convert.FedoraEntitySourceTraversalListener;
import ch.unil.spring.data.fedora.core.mapping.FedoraObjectReferencePersistentProperty;
import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentEntity;
import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.springframework.data.mapping.model.MappingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Subclass of {@linkplain org.fcrepo.utilities.Foxml11Document}, allows getting XML content of a datastream version and
 * working with {@linkplain com.hp.hpl.jena.rdf.model.Model} API for creating RDF content for RELS-EXT datastream.
 *
 * @author gushakov
 * @see org.fcrepo.utilities.Foxml11Document
 * @see com.hp.hpl.jena.rdf.model.Model
 * @see ch.unil.spring.data.fedora.core.convert.FedoraEntitySourceTraversalListener
 */
public class Foxml11Document extends org.fcrepo.utilities.Foxml11Document implements FedoraEntitySourceTraversalListener {
    private String pid;

    private Map<String, String> dsvContentsMap;

    private Model rdfModel;
    private Resource rdfAboutResource;

    public Foxml11Document(String pid) {
        super(pid);
        this.pid = pid;
        this.dsvContentsMap = new HashMap<>();
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix(org.fcrepo.common.Constants.FEDORA.prefix, org.fcrepo.common.Constants.RELS_EXT.uri);
        this.rdfModel = model;
        this.rdfAboutResource = rdfModel.createResource(org.fcrepo.common.Constants.FEDORA.uri + pid);
    }

    @Override
    public void addXmlContent(String dsvId, String xmlContent) {
        super.addXmlContent(dsvId, xmlContent);
        dsvContentsMap.put(dsvId, xmlContent);
    }

    public String getPid() {
        return pid;
    }

    public String getXmlContent(String dsvId) {
        return dsvContentsMap.get(dsvId);
    }

    public void writeRelsExtDatastreamContent() {
        // serialize RDF model to the RELS-EXT datastream
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            rdfModel.write(os);
            os.flush();
            addXmlContent(Constants.RELS_EXT_DATASTREAM_ID + Constants.DEFAULT_DATASTREAM_VERSION_SUFFIX,
                    new String(os.toByteArray()));
        } catch (IOException e) {
            throw new MappingException("Error when marshalling RELS-EXT datastream.", e);
        }
    }

    @Override
    public void applyWithPropertySource(Object propertySource, FedoraPersistentEntity<?> propertyEntity, FedoraPersistentProperty parentProperty, FedoraConverter fedoraConverter) {
        doApplyWithPropertySource(propertySource, parentProperty, fedoraConverter);
    }

    @Override
    public void applyWithCollectionItemSource(Object itemSource, FedoraPersistentEntity<?> itemEntity, FedoraPersistentProperty containerProperty, FedoraConverter fedoraConverter) {
        doApplyWithPropertySource(itemSource, containerProperty, fedoraConverter);
    }

    @Override
    public void applyWithMapEntrySource(Map.Entry<?, ?> entrySource, Class<?> keyType, FedoraPersistentEntity<?> valueEntity, Class<?> valueType, FedoraPersistentProperty mapProperty, FedoraConverter fedoraConverter) {
        // apply only for entries with entity values
        if (valueEntity != null) {
            doApplyWithPropertySource(entrySource.getValue(), mapProperty, fedoraConverter);
        }
    }

    protected void doApplyWithPropertySource(Object propertySource, FedoraPersistentProperty parentProperty, FedoraConverter fedoraConverter) {
        if (parentProperty == null) {
            return;
        }

        // add rels-ext entry for each Fedora object reference
        if (parentProperty.isFedoraObjectRef()) {
            FedoraObjectReferencePersistentProperty relsExtProp = (FedoraObjectReferencePersistentProperty) parentProperty;
            String uri = fedoraConverter.toFedoraObjectUri(propertySource);
            com.hp.hpl.jena.rdf.model.Property rdfRelProp = rdfModel.createProperty(org.fcrepo.common.Constants.RELS_EXT.uri,
                    relsExtProp.getRdfProperty().getId());
            Resource rdfTargetResource = rdfModel.createResource(uri);
            rdfModel.add(rdfAboutResource, rdfRelProp, rdfTargetResource);
        }
    }
}
