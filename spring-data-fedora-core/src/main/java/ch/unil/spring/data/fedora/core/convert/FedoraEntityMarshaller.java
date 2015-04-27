package ch.unil.spring.data.fedora.core.convert;

/**
 * @author gushakov
 */
public interface FedoraEntityMarshaller extends FedoraEntityTraversalListener {

    boolean isConfigured();

    void setConfigured(boolean configured);

    String marshal(Object entObj);

    Object unmarshal(String serialized);
}
