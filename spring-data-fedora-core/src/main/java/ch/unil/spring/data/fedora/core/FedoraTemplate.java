package ch.unil.spring.data.fedora.core;

import ch.unil.spring.data.fedora.config.WsFedoraConnection;
import ch.unil.spring.data.fedora.config.WsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.core.convert.DelegatingDynamicProxy;
import ch.unil.spring.data.fedora.core.convert.FedoraConverter;
import ch.unil.spring.data.fedora.core.convert.FedoraMappingConverter;
import ch.unil.spring.data.fedora.core.mapping.*;
import ch.unil.spring.data.fedora.core.query.*;
import ch.unil.spring.data.fedora.core.utils.FindObjectsResults;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gushakov
 */
public class FedoraTemplate implements FedoraOperations {

    private MappingContext<? extends FedoraPersistentEntity<?>, FedoraPersistentProperty> mappingContext;
    private WsFedoraConnection wsConnection;
    private FedoraConverter converter;
    private FedoraExceptionTranslator exceptionTranslator;

    public FedoraTemplate(WsFedoraConnectionFactory connectionFactory) {
        this(connectionFactory, null);
    }

    public FedoraTemplate(WsFedoraConnectionFactory connectionFactory, FedoraConverter converter) {
        Assert.notNull(connectionFactory);
        this.wsConnection = connectionFactory.newConnection();
        if (converter != null) {
            this.converter = converter;
        } else {
            this.converter = new FedoraMappingConverter(wsConnection);
        }
        this.mappingContext = this.converter.getMappingContext();

        this.exceptionTranslator = new FedoraExceptionTranslator();
    }

    @Override
    public FindObjectsQueryBuilder getQueryBuilder() {
        return new FindObjectsQueryBuilder(mappingContext);
    }

    @Override
    public <T> String getPid(T source) {
        return converter.getPid(source);
    }

    @Override
    public boolean exists(String pid) {
        Assert.hasText(pid);
        boolean objectExists = false;
        try {
            objectExists = wsConnection.getApiaWsClient().exists(pid);
        } catch (RuntimeException e) {
            // ignore object not found exception
            ignoreOrRethrowPossiblyTranslatedException(e, EmptyResultDataAccessException.class);
        }
        return objectExists;
    }

    @Override
    public <T> String ingest(T source, String comment) {
        Assert.notNull(source);
        Assert.hasText(comment);
        Foxml11Document foxml11Doc = converter.write(source);
        return wsConnection.getApimWsClient().ingest(foxml11Doc, comment);
    }

    @Override
    public boolean purge(String pid, String comment) {
        Assert.hasText(pid);
        Assert.hasText(comment);
        boolean wasDeleted = false;
        try {
            wasDeleted = wsConnection.getApimWsClient().purge(pid, comment);
        } catch (RuntimeException e) {
            // ignore object not found exception
            ignoreOrRethrowPossiblyTranslatedException(e, EmptyResultDataAccessException.class);
        }
        return wasDeleted;
    }

    @Override
    public <I, T> T load(I id, Class<T> type) {
        Assert.notNull(id);
        Assert.notNull(type);
        try {
            return converter.read(id, type);
        } catch (RuntimeException e) {
            throw exceptionTranslator.translateExceptionIfPossible(e);
        }
    }

    @Override
    public <T> void save(T source) {
        Assert.notNull(source);
        // use the delegate instead of the source object if source is a proxy
        Object delegate;
        if (DelegatingDynamicProxy.class.isAssignableFrom(source.getClass())) {
            DelegatingDynamicProxy<?> proxy = ((DelegatingDynamicProxy<?>) source);
            delegate = proxy.getDelegate();
        } else {
            delegate = source;
        }

        Foxml11Document foxmlDoc = converter.write(delegate);
        DatastreamFedoraPersistentEntity<?> entity = (DatastreamFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(delegate.getClass());
        try {
            wsConnection.getApimWsClient().save(foxmlDoc.getXmlContent(entity.getDsvId()).getBytes(), converter.getPid(delegate), entity.getDsId());
        } catch (RuntimeException e) {
            throw exceptionTranslator.translateExceptionIfPossible(e);
        }
    }


    @Override
    public <T> Page<T> query(FindObjectsQuery query, Class<T> type, FindObjectsPageRequest pageable) {
        Assert.notNull(query);
        Assert.notNull(type);
        Assert.notNull(pageable);
        try {
            DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(type);
            PidFedoraPersistentProperty pidProp = entity.getPidProperty();
            FindObjectsResults findPidsResults;
            String sessionToken = pageable.getSessionToken();
            if (sessionToken != null) {
                // resume find objects
                findPidsResults = wsConnection.getApiaWsClient().resumeFindObjects(sessionToken);
            } else {
                // query for the first time
                findPidsResults = wsConnection.getApiaWsClient().findObjects(query.getField().getField(), query.getOperator().getOperator(),
                        query.getPhrase(), pageable.getPageSize());
            }

            List<String> pids = findPidsResults.getPids();

            // convert from pids to entity objects
            List<T> results = new ArrayList<>();
            for (String pid : pids) {
                Object id = pidProp.getPidCreator().getId(pid, String.class, entity);
                results.add(converter.read(id, type));
            }

            Page<T> page;
            if (findPidsResults.getSessionToken() != null || pageable.getCursor() != FindObjectsPageRequest.NO_CURSOR) {
                page = new FindObjectsPage<>(results, new DefaultFindObjectsPageRequest(pageable.getPageNumber(),
                        pageable.getPageSize(), findPidsResults.getCursor(), findPidsResults.getSessionToken()));
            } else {
                page = new PageImpl<>(results, pageable, (long) findPidsResults.getPids().size());
            }
            return page;
        } catch (RuntimeException e) {
            throw exceptionTranslator.translateExceptionIfPossible(e);
        }
    }

    private void ignoreOrRethrowPossiblyTranslatedException(RuntimeException ex, Class<? extends DataAccessException> ignoreExceptionType) {
        DataAccessException dae = exceptionTranslator.translateExceptionIfPossible(ex);
        if (dae == null || !ignoreExceptionType.isInstance(dae)) {
            if (dae != null) {
                throw dae;
            } else {
                throw ex;
            }
        }
    }

}
