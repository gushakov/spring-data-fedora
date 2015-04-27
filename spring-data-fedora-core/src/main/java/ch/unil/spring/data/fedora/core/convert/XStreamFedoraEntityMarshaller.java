package ch.unil.spring.data.fedora.core.convert;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author gushakov
 */
public class XStreamFedoraEntityMarshaller implements FedoraEntityMarshaller {

    private static final Logger logger = LoggerFactory.getLogger(XStreamFedoraEntityMarshaller.class);

    private XStream xstream;

    private boolean configured;

    public XStreamFedoraEntityMarshaller() {
        this.xstream = new XStream();
        this.xstream.ignoreUnknownElements();
    }

    public XStreamFedoraEntityMarshaller(String prefix, String namespace) {
        Assert.hasText(prefix);
        Assert.hasText(namespace);
        QNameMap nsMap = new QNameMap();
        nsMap.setDefaultPrefix(prefix);
        nsMap.setDefaultNamespace(namespace);
        StaxDriver staxDriver = new StaxDriver(nsMap);
        this.xstream = new XStream(staxDriver);
        this.xstream.ignoreUnknownElements();
    }

    @Override
    public boolean isConfigured() {
        return configured;
    }

    @Override
    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    @Override
    public String marshal(Object entObj) {
        Assert.notNull(entObj);
        String xml;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            xstream.toXML(entObj, os);
            os.flush();
            xml = new String(os.toByteArray());
        } catch (IOException e) {
            throw new MappingException("Error when marshalling entity object " + entObj, e);
        }
        return xml;
    }

    @Override
    public Object unmarshal(String serialized) {
        Assert.hasText(serialized);
        try {
            return xstream.fromXML(serialized);
        } catch (Exception e) {
            throw new MappingException("Error when unmarshalling entity object from " + serialized, e);
        }
    }

    @Override
    public void applyWithEntity(FedoraPersistentEntity<?> childEntity, FedoraPersistentProperty parentProperty, FedoraConverter fedoraConverter) {
        doApplyWithEntity(childEntity, parentProperty, fedoraConverter);
    }

    @Override
    public void applyWithCollectionItemEntity(FedoraPersistentEntity<?> itemEntity, FedoraPersistentProperty containerProperty, FedoraConverter fedoraConverter) {
        doApplyWithEntity(itemEntity, containerProperty, fedoraConverter);
    }

    @Override
    public void applyWithMapEntryEntities(Class<?> keyType, FedoraPersistentEntity<?> valueEntity, Class<?> valueType, FedoraPersistentProperty mapProperty, FedoraConverter fedoraConverter) {
        // register converter for the map entries, see javadoc of NamedMapConverter for examples
        if (valueEntity != null) {
            doApplyWithEntity(valueEntity, mapProperty, fedoraConverter);
            // map with entity values
            xstream.registerLocalConverter(mapProperty.getOwner().getType(), mapProperty.getName(),
                    new NamedMapConverter(xstream.getMapper(), Constants.XSTREAM_MAP_ENTRY_ELEMENT_NAME,
                            Constants.XSTREAM_MAP_ENTRY_KEY_ATTRIBUTE_NAME, keyType,
                            valueEntity.getType().getSimpleName().toLowerCase(), valueType, true, false, xstream.getConverterLookup()));
        } else {
            // simple map
            xstream.registerLocalConverter(mapProperty.getOwner().getType(), mapProperty.getName(),
                    new NamedMapConverter(xstream.getMapper(), Constants.XSTREAM_MAP_ENTRY_ELEMENT_NAME,
                            Constants.XSTREAM_MAP_ENTRY_KEY_ATTRIBUTE_NAME, keyType,
                            Constants.XSTREAM_MAP_ENTRY_VALUE_ATTRIBUTE_NAME, valueType, true, true, xstream.getConverterLookup()));
        }
    }

    protected void doApplyWithEntity(FedoraPersistentEntity<?> entity, FedoraPersistentProperty parentProperty, FedoraConverter fedoraConverter) {
        // set alias for the entity
        xstream.alias(getAlias(entity), entity.getType());

        // do if entity is a document entity
        if (DocumentFedoraPersistentEntity.class.isAssignableFrom(entity.getClass())) {
            DocumentFedoraPersistentEntity<?> docEntity = (DocumentFedoraPersistentEntity<?>) entity;
            // marshal pid as attribute
            PidFedoraPersistentProperty pidProp = docEntity.getPidProperty();
            if (pidProp.marshalAsAttribute()) {
                xstream.useAttributeFor(docEntity.getType(), pidProp.getName());
            }
        }

        if (parentProperty != null) {
            // omit datastream reference from being marshalled
            if (parentProperty.isDatastreamRef()) {
                xstream.omitField(parentProperty.getOwner().getType(), parentProperty.getName());
                logger.debug("Omitting property {} in {} from marshalling", parentProperty.getName(), entity.getType().getSimpleName());
            }

            // register custom converter for Fedora Object reference
            if (parentProperty.isFedoraObjectRef()) {
                xstream.registerLocalConverter(parentProperty.getOwner().getType(), parentProperty.getName(),
                        new XstreamFedoraObjectReferenceConverter((FedoraObjectReferencePersistentProperty) parentProperty, fedoraConverter));
                logger.debug("Registered custom converter for FedoraObjectRef property {} in {}", parentProperty.getName(),
                        parentProperty.getOwner().getType().getSimpleName());
            }
        }
    }

    /**
     * Sets the alias for the entity's class to the value of the lowercase class name of the entity's type.
     *
     * @param entity entity to be marshalled
     * @return marshalling alias for this entity
     */
    protected String getAlias(FedoraPersistentEntity<?> entity) {
        return entity.getType().getSimpleName().toLowerCase();
    }
}
