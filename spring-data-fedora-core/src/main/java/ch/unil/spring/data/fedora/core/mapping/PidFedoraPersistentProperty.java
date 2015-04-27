package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public interface PidFedoraPersistentProperty extends FedoraPersistentProperty {

    String getNamespaceId();

    boolean marshalAsAttribute();

    PidCreator getPidCreator();

}
