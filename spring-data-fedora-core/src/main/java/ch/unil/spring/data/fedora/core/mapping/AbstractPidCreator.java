package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;

/**
 * @author gushakov
 */
public abstract class AbstractPidCreator implements PidCreator {

    protected ConversionService conversionService;

    protected AbstractPidCreator() {
        this.conversionService = new DefaultConversionService();
    }

    @Override
    public <I> String getPid(I id, DocumentFedoraPersistentEntity<?> entity) {
        Assert.notNull(id);
        Assert.notNull(entity);
        PidFedoraPersistentProperty pidProp = entity.getPidProperty();
        String pid = pidProp.getNamespaceId() + Constants.PID_NAMESPACE_SEPARATOR +
                getEntityName(entity) + Constants.PID_SEPARATOR + conversionService.convert(id, String.class);
        if (pid.length() > Constants.PID_MAX_LENGTH) {
            throw new IllegalStateException("PID (" + pid + ") of entity " + entity.getType().getSimpleName() +
                    " is longer than " + Constants.PID_MAX_LENGTH + " characters");
        }
        return pid;
    }

    @Override
    public boolean isValidPid(String pid, DocumentFedoraPersistentEntity<?> entity) {
        Assert.hasText(pid);
        Assert.notNull(entity);
        PidFedoraPersistentProperty pidProp = entity.getPidProperty();
        return pid.startsWith(pidProp.getNamespaceId() + Constants.PID_NAMESPACE_SEPARATOR + getEntityName(entity) +
                Constants.PID_SEPARATOR);
    }

    @Override
    public <I> I getId(String pid, Class<I> idType, DocumentFedoraPersistentEntity<?> entity) {
        Assert.hasText(pid);
        Assert.notNull(idType);
        Assert.notNull(entity);
        PidFedoraPersistentProperty pidProp = entity.getPidProperty();
        String id = StringUtils.substringAfter(pid, pidProp.getNamespaceId() + Constants.PID_NAMESPACE_SEPARATOR +
                getEntityName(entity) + Constants.PID_SEPARATOR);
        return conversionService.convert(id, idType);
    }

    protected abstract String getEntityName(DatastreamFedoraPersistentEntity<?> entity);

}
