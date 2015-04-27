package ch.unil.spring.data.fedora.core.mapping;

import org.springframework.data.mapping.PersistentProperty;

/**
 * @author gushakov
 */
public interface FedoraPersistentProperty extends PersistentProperty<FedoraPersistentProperty> {

    boolean isPid();

    boolean isRelsExt();

    boolean isDatastreamRef();

    boolean isFedoraObjectRef();

}
