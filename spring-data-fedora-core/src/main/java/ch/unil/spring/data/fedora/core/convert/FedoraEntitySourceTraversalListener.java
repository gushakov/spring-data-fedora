package ch.unil.spring.data.fedora.core.convert;

import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentEntity;
import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentProperty;

import java.util.Map;

/**
 * @author gushakov
 */
public interface FedoraEntitySourceTraversalListener {

    void applyWithPropertySource(Object propertySource, FedoraPersistentEntity<?> propertyEntity, FedoraPersistentProperty parentProperty, FedoraConverter fedoraConverter);

    void applyWithCollectionItemSource(Object itemSource, FedoraPersistentEntity<?> itemEntity, FedoraPersistentProperty containerProperty,
                                       FedoraConverter fedoraConverter);

    void applyWithMapEntrySource(Map.Entry<?, ?> entrySource, Class<?> keyType, FedoraPersistentEntity<?> valueEntity,
                                 Class<?> valueType, FedoraPersistentProperty mapProperty, FedoraConverter fedoraConverter);

}
