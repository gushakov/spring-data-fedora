package ch.unil.spring.data.fedora.core.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gushakov
 */
public class FedoraObjectReferenceDynamicProxyInterceptor<I, T> extends AbstractDelegatingDynamicProxyInterceptor<I, T> {
    private static final Logger logger = LoggerFactory.getLogger(FedoraObjectReferenceDynamicProxyInterceptor.class);

    public FedoraObjectReferenceDynamicProxyInterceptor(I id, Class<T> delegateType, FedoraConverter converter) {
        super(id, delegateType, converter);
    }

    @Override
    protected void loadProxy() {
        logger.debug("Loading a proxy delegate object of type {}, ID {}", delegateType.getSimpleName(), id);
        delegate = converter.read(id, delegateType);
    }
}
