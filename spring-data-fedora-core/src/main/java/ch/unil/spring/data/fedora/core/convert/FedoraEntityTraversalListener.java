package ch.unil.spring.data.fedora.core.convert;

import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentEntity;
import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentProperty;

/**
 * @author gushakov
 */
public interface FedoraEntityTraversalListener {

    void applyWithEntity(FedoraPersistentEntity<?> childEntity, FedoraPersistentProperty parentProperty, FedoraConverter fedoraConverter);

    void applyWithCollectionItemEntity(FedoraPersistentEntity<?> itemEntity, FedoraPersistentProperty containerProperty,
                                       FedoraConverter fedoraConverter);

    void applyWithMapEntryEntities(Class<?> keyType, FedoraPersistentEntity<?> valueEntity,
                                   Class<?> valueType, FedoraPersistentProperty mapProperty, FedoraConverter fedoraConverter);

}
