package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public interface DatastreamReferencePropertyHandler {
    void doWithDatastreamRef(DatastreamReferenceFedoraPersistentProperty property);
}
