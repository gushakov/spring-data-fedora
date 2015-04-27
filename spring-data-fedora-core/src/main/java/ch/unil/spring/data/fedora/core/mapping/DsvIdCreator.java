package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public interface DsvIdCreator {

    String getDsvId(DefaultDatastreamFedoraPersistentEntity<?> entity);

}
