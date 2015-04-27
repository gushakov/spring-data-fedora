package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public interface FoxmlLabelCreator {

    String getFoxmlLabel(DocumentFedoraPersistentEntity<?> entity);

}
