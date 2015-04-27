package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public class DefaultDsvLabelCreator implements DsvLabelCreator {
    @Override
    public String getDsvLabel(DatastreamFedoraPersistentEntity<?> entity) {
        return entity.getType().getSimpleName().toLowerCase();
    }
}
