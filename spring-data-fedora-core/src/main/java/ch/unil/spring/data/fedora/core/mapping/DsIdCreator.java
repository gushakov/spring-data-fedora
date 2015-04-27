package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public interface DsIdCreator {

    String getDsId(DefaultDatastreamFedoraPersistentEntity<?> dsProp);

}
