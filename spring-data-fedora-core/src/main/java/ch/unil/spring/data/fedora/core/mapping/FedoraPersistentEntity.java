package ch.unil.spring.data.fedora.core.mapping;

import org.springframework.data.mapping.PersistentEntity;

/**
 * @author gushakov
 */
public interface FedoraPersistentEntity<T> extends PersistentEntity<T, FedoraPersistentProperty> {

    void doWithRelsExts(RelsExtPropertyHandler handler);

    void doWithDatastreamRefs(DatastreamReferencePropertyHandler handler);
}
