package ch.unil.spring.data.fedora.core;

import ch.unil.spring.data.fedora.core.query.FindObjectsPageRequest;
import ch.unil.spring.data.fedora.core.query.FindObjectsQuery;
import ch.unil.spring.data.fedora.core.query.FindObjectsQueryBuilder;
import org.springframework.data.domain.Page;

/**
 * @author gushakov
 */
public interface FedoraOperations {

    FindObjectsQueryBuilder getQueryBuilder();

    <T> String getPid(T source);

    boolean exists(String pid);

    <T> String ingest(T source, String comment);

    boolean purge(String pid, String comment);

    <I, T> T load(I id, Class<T> type);

    <T> void save(T source);

    <T> Page<T> query(FindObjectsQuery query, Class<T> type, FindObjectsPageRequest pageable);

}
