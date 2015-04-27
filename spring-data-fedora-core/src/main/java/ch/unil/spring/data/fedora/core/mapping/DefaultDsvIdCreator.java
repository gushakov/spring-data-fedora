package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;
import org.springframework.util.Assert;

/**
 * @author gushakov
 */
public class DefaultDsvIdCreator implements DsvIdCreator {

    @Override
    public String getDsvId(DefaultDatastreamFedoraPersistentEntity<?> entity) {
        Assert.notNull(entity);
        return entity.getDsId() + Constants.DEFAULT_DATASTREAM_VERSION_SUFFIX;
    }
}
