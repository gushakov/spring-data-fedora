package ch.unil.spring.data.fedora.core.convert;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.FedoraObjectReferencePersistentProperty;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author gushakov
 */
public class XstreamFedoraObjectReferenceConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(XstreamFedoraObjectReferenceConverter.class);

    private FedoraObjectReferencePersistentProperty property;
    private FedoraConverter fedoraConverter;

    public XstreamFedoraObjectReferenceConverter(FedoraObjectReferencePersistentProperty property, FedoraConverter fedoraConverter) {
        this.property = property;
        this.fedoraConverter = fedoraConverter;
    }

    @Override
    public void marshal(Object entObj, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {
        // TODO: marshal map of FedoraObjectRef
        if (property.isCollectionLike()) {
            if (property.isArray()) {
                //TODO: marshal arrays of FedoraObjectRef
            } else {
                logger.debug("Marshalling list of Fedora object references: property {} of entity {}",
                        property.getName(), property.getOwner().getType().getSimpleName());
                for (Object item : (Collection<?>) entObj) {
                    writer.startNode(property.getComponentType().getSimpleName().toLowerCase());
                    writer.addAttribute(Constants.FEDORA_URI_XML_ATTRIBUTE, fedoraConverter.toFedoraObjectUri(item));
                    writer.endNode();
                }
            }
        } else {
            String uri = fedoraConverter.toFedoraObjectUri(entObj);
            writer.addAttribute(Constants.FEDORA_URI_XML_ATTRIBUTE, uri);
            logger.debug("Marshalled Fedora object reference property {} of entity {} to {}",
                    property.getName(), property.getOwner().getType().getSimpleName(), uri);
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
        // TODO: unmarshal collections and maps
        Object obj;
        String uri = reader.getAttribute(Constants.FEDORA_URI_XML_ATTRIBUTE);
        if (property.lazyLoad()) {
            logger.debug("Creating dynamic proxy for lazy loading of Fedora object with URI " + uri);
            obj = fedoraConverter.createDynamicProxyForFedoraObjectReference(uri);
        } else {
            logger.debug("Directly loading Fedora object with URI " + uri);
            obj = fedoraConverter.read(uri, property.getType());
        }
        return obj;
    }

    @Override
    public boolean canConvert(Class aClass) {
        // TODO: adjust for maps
        if (property.isCollectionLike()) {
            return property.getType().isAssignableFrom(aClass);
        } else {
            return property.getType().equals(aClass);
        }
    }
}
