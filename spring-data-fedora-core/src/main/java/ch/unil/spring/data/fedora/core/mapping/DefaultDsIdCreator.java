package ch.unil.spring.data.fedora.core.mapping;

import org.springframework.util.Assert;

/**
 * @author gushakov
 */
public class DefaultDsIdCreator implements DsIdCreator {
    @Override
    public String getDsId(DefaultDatastreamFedoraPersistentEntity<?> entity) {
        Assert.notNull(entity);
        return entity.getType().getSimpleName().toUpperCase();
    }
}
