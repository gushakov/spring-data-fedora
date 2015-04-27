package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public class DefaultFoxmlLabelCreator implements FoxmlLabelCreator {
    @Override
    public String getFoxmlLabel(DocumentFedoraPersistentEntity<?> entity) {
        return entity.getType().getSimpleName().toLowerCase();
    }
}
