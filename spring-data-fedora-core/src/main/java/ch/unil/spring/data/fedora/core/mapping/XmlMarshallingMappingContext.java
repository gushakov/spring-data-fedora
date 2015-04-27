package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.convert.FedoraEntityMarshaller;
import org.springframework.data.mapping.context.MappingContext;

/**
 * @author gushakov
 */
public interface XmlMarshallingMappingContext extends MappingContext<GenericFedoraPersistentEntity<?>, FedoraPersistentProperty> {

    FedoraEntityMarshaller getMarshaller(DatastreamFedoraPersistentEntity<?> entity);

}
