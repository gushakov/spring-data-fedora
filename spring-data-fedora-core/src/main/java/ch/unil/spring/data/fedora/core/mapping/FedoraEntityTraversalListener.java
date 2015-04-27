package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.convert.FedoraConverter;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;

/**
 * @author gushakov
 */
public interface FedoraEntityTraversalListener {

    boolean isConfiguredFor(DocumentFedoraPersistentEntity<?> entity);

    void configureForEntity(FedoraPersistentEntity<?> entity, FedoraPersistentProperty property, Foxml11Document foxmlDoc, FedoraConverter fedoraConverter);

    void configureForCollectionItem(FedoraPersistentEntity<?> itemEntity, FedoraPersistentProperty containerProperty, Foxml11Document foxmlDoc, FedoraConverter fedoraConverter);

    void configureForMapEntry(FedoraPersistentEntity<?> keyEntity, FedoraPersistentEntity<?> valueEntity,
                              FedoraPersistentProperty mapProperty, Foxml11Document foxmlDoc, FedoraConverter fedoraConverter);

}
