package ch.unil.spring.data.fedora.core.mapping;


import ch.unil.spring.data.fedora.core.utils.Foxml11Document;

/**
 * @author gushakov
 */
public interface DocumentFedoraPersistentEntity<T> extends DatastreamFedoraPersistentEntity<T> {

    DefaultPidFedoraPersistentProperty getPidProperty();

    Foxml11Document.State getFoxmlState();

    String getFoxmlLabel();

    String getFoxmlOwnerId();

}
