package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.utils.Foxml11Document;

/**
 * @author gushakov
 */
public interface DatastreamFedoraPersistentEntity<T> extends FedoraPersistentEntity<T> {

    String getNamespace();

    Foxml11Document.State getState();

    Foxml11Document.ControlGroup getControlGroup();

    String getDsvLabel();

    String getDsvMimetype();

    String getDsId();

    String getDsvId();


}
