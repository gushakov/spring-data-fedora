package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public interface DsvLabelCreator {
    String getDsvLabel(DatastreamFedoraPersistentEntity<?> entity);
}
