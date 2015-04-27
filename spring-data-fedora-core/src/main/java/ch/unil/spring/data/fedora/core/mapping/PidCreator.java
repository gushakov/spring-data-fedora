package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public interface PidCreator {

    <I> String getPid(I id, DocumentFedoraPersistentEntity<?> entity);

    boolean isValidPid(String pid, DocumentFedoraPersistentEntity<?> entity);

    <I> I getId(String pid, Class<I> idType, DocumentFedoraPersistentEntity<?> entity);

}
