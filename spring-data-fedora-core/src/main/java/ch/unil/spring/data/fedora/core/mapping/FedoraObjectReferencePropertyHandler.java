package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public interface FedoraObjectReferencePropertyHandler {
    void doWithFedoraObjectRef(FedoraObjectReferencePersistentProperty property);
}
