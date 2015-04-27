package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.convert.FedoraEntityMarshaller;
import ch.unil.spring.data.fedora.core.convert.XStreamFedoraEntityMarshaller;
import ch.unil.spring.data.fedora.core.mapping.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gushakov
 */
public class FedoraMappingContext extends AbstractMappingContext<GenericFedoraPersistentEntity<?>, FedoraPersistentProperty>
        implements XmlMarshallingMappingContext {

    private static final Logger logger = LoggerFactory.getLogger(FedoraMappingContext.class);

    private Map<String, FedoraEntityMarshaller> marshallers;

    public FedoraMappingContext() {
        this.marshallers = new HashMap<>();
        // register default marshaller
        this.marshallers.put(Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN, new XStreamFedoraEntityMarshaller());
    }

    @Override
    protected <T> GenericFedoraPersistentEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {

        GenericFedoraPersistentEntity<?> entity;

        if (typeInformation.getRawTypeInformation().getType().getAnnotation(FedoraObject.class) != null) {
            entity = new DefaultDocumentFedoraPersistentEntity<>(typeInformation);
        } else if (typeInformation.getRawTypeInformation().getType().getAnnotation(Datastream.class) != null) {
            entity = new DefaultDatastreamFedoraPersistentEntity<>(typeInformation);
        } else {
            entity = new GenericFedoraPersistentEntity<>(typeInformation);
        }

        return entity;
    }

    @Override
    protected FedoraPersistentProperty createPersistentProperty(Field field, PropertyDescriptor descriptor, GenericFedoraPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        FedoraPersistentProperty prop;

        if (field != null && field.getAnnotation(Pid.class) != null) {
            logger.debug("Found PID property <{}> of entity {}", field.getName(), owner.getType().getSimpleName());
            prop = new DefaultPidFedoraPersistentProperty(field, descriptor, owner, simpleTypeHolder);
        } else if (field != null && field.getAnnotation(FedoraObjectRef.class) != null) {
            logger.debug("Found Fedora Object reference property <{}> of entity {}", field.getName(), owner.getType().getSimpleName());
            prop = new DefaultFedoraObjectReferencePersistentProperty(field, descriptor, owner, simpleTypeHolder);
        } else if (field != null && field.getAnnotation(RelsExt.class) != null) {
            logger.debug("Found RELS-EXT property <{}> of entity {}", field.getName(), owner.getType().getSimpleName());
            prop = new DefaultRelsExtFedoraPersistentProperty(field, descriptor, owner, simpleTypeHolder);
        } else if (field != null && field.getAnnotation(DatastreamRef.class) != null) {
            logger.debug("Found Datastream reference property <{}> of entity {}", field.getName(), owner.getType().getSimpleName());
            prop = new DefaultDatastreamReferenceFedoraPersistentProperty(field, descriptor, owner, simpleTypeHolder);
        } else {
            logger.debug("Found generic property <{}> of entity {}", field != null ? field.getName() : descriptor.getName(),
                    owner.getType().getSimpleName());
            prop = new GenericFedoraPersistentProperty(field, descriptor, owner, simpleTypeHolder);
        }

        return prop;
    }

    @Override
    protected GenericFedoraPersistentEntity<?> addPersistentEntity(TypeInformation<?> typeInformation) {
        GenericFedoraPersistentEntity<?> ent = super.addPersistentEntity(typeInformation);
        if (DatastreamFedoraPersistentEntity.class.isAssignableFrom(ent.getClass())) {
            DatastreamFedoraPersistentEntity<?> entity = (DatastreamFedoraPersistentEntity<?>) ent;
            FedoraEntityMarshaller marshaller = marshallers.get(entity.getNamespace());
            if (marshaller == null) {
                // create new marshaller for the namespace
                marshaller = new XStreamFedoraEntityMarshaller(entity.getType().getSimpleName().toLowerCase(), entity.getNamespace());
                marshallers.put(entity.getNamespace(), marshaller);
            }
        }
        return ent;
    }

    @Override
    public FedoraEntityMarshaller getMarshaller(DatastreamFedoraPersistentEntity<?> entity) {
        return marshallers.get(entity.getNamespace());
    }
}
