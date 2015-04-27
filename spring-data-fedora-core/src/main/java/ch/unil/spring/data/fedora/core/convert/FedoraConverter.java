package ch.unil.spring.data.fedora.core.convert;

import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentEntity;
import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentProperty;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import org.springframework.data.convert.EntityReader;
import org.springframework.data.convert.EntityWriter;
import org.springframework.data.mapping.context.MappingContext;


/**
 * @author gushakov
 */
public interface FedoraConverter extends EntityWriter<Object, Foxml11Document>, EntityReader<Object, Foxml11Document> {

    MappingContext<? extends FedoraPersistentEntity<?>, FedoraPersistentProperty> getMappingContext();

    String getPid(Object source);

    Foxml11Document write(Object source);

    <I, S> S read(I id, Class<S> type);

    <S> S read(String uri, Class<S> type);

    String toFedoraObjectUri(Object source);

    Object createDynamicProxyForFedoraObjectReference(String uri);

    <I, S, D> D readDatastream(I id, Class<S> type, String dsId, Class<D> dsType);
}
