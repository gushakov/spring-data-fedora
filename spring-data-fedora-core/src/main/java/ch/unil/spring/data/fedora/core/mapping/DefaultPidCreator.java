package ch.unil.spring.data.fedora.core.mapping;

/**
 * @author gushakov
 */
public class DefaultPidCreator extends AbstractPidCreator {
    @Override
    protected String getEntityName(DatastreamFedoraPersistentEntity<?> entity) {
        return entity.getType().getSimpleName().toLowerCase();
    }

//    private ConversionService conversionService;
//
//    public DefaultPidCreator() {
//        this.conversionService = new DefaultConversionService();
//    }
//
//    @Override
//    public <I> String getPid(I id, DatastreamFedoraPersistentEntity<?> entity) {
//        Assert.notNull(id);
//        Assert.notNull(entity);
//        PidFedoraPersistentProperty pidProp = entity.getPidProperty();
//        String pid = pidProp.getNamespaceId() + Constants.PID_NAMESPACE_SEPARATOR +
//                entity.getType().getSimpleName().toLowerCase() + Constants.PID_SEPARATOR + conversionService.convert(id, String.class);
//        if (pid.length() > Constants.PID_MAX_LENGTH) {
//            throw new IllegalStateException("PID (" + pid + ") of entity " + entity.getType().getSimpleName() +
//                    " is longer than " + Constants.PID_MAX_LENGTH + " characters");
//        }
//        return pid;
//    }
//
//    @Override
//    public boolean isValidPid(String pid, DatastreamFedoraPersistentEntity<?> entity) {
//        Assert.hasText(pid);
//        Assert.notNull(entity);
//        PidFedoraPersistentProperty pidProp = entity.getPidProperty();
//        return pid.startsWith(pidProp.getNamespaceId() + Constants.PID_NAMESPACE_SEPARATOR + entity.getType().getSimpleName().toLowerCase() +
//                Constants.PID_SEPARATOR);
//    }
//
//    @Override
//    public <I> I getId(String pid, Class<I> idType, DatastreamFedoraPersistentEntity<?> entity) {
//        Assert.hasText(pid);
//        Assert.notNull(idType);
//        Assert.notNull(entity);
//        PidFedoraPersistentProperty pidProp = entity.getPidProperty();
//        String id = StringUtils.substringAfter(pid, pidProp.getNamespaceId() + Constants.PID_NAMESPACE_SEPARATOR +
//                entity.getType().getSimpleName().toLowerCase() + Constants.PID_SEPARATOR);
//        return conversionService.convert(id, idType);
//    }

}
