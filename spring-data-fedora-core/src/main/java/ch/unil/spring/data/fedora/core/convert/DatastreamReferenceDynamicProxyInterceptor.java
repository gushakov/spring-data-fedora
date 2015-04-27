package ch.unil.spring.data.fedora.core.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gushakov
 */
public class DatastreamReferenceDynamicProxyInterceptor<I, T, P> extends AbstractDelegatingDynamicProxyInterceptor<I, T> {

    private static final Logger logger = LoggerFactory.getLogger(DatastreamReferenceDynamicProxyInterceptor.class);

    private Class<P> parentType;

    private String dsId;

    public DatastreamReferenceDynamicProxyInterceptor(I id, Class<P> parentType, String dsId, Class<T> dsType, FedoraConverter converter) {
        super(id, dsType, converter);
        this.parentType = parentType;
        this.dsId = dsId;
    }

    @Override
    protected void loadProxy() {
        logger.debug("Loading a proxy delegate object for a datastream with ID {} of type {} with parent object ID {} and type {}",
                dsId, delegateType.getSimpleName(), id, parentType.getSimpleName());
        delegate = converter.readDatastream(id, parentType, dsId, delegateType);
    }
}
