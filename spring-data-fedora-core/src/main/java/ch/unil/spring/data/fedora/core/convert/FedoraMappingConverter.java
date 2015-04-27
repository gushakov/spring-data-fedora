package ch.unil.spring.data.fedora.core.convert;

import ch.unil.spring.data.fedora.config.WsFedoraConnection;
import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.*;
import ch.unil.spring.data.fedora.core.utils.DatastreamContents;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassLoadingStrategy;
import net.bytebuddy.instrumentation.MethodDelegation;
import net.bytebuddy.instrumentation.method.matcher.MethodMatchers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

import javax.xml.bind.JAXBElement;
import java.util.*;


/*
 * Based by org.springframework.data.mongodb.core.convert.MappingMongoConverter
 */

/**
 * @author gushakov
 */
public class FedoraMappingConverter implements FedoraConverter {

    private static final Logger logger = LoggerFactory.getLogger(FedoraMappingConverter.class);

    private WsFedoraConnection wsConnection;

    private XmlMarshallingMappingContext mappingContext;

    private ConversionService conversionService;

    private SimpleTypeHolder simpleTypeHolder;

    public FedoraMappingConverter(WsFedoraConnection wsConnection) {
        this.wsConnection = wsConnection;
        FedoraMappingContext context = new FedoraMappingContext();
        context.afterPropertiesSet();
        this.mappingContext = context;
        this.conversionService = new DefaultConversionService();
        this.simpleTypeHolder = new SimpleTypeHolder();
    }

    public FedoraMappingConverter(WsFedoraConnection wsConnection, XmlMarshallingMappingContext mappingContext) {
        this.wsConnection = wsConnection;
        this.mappingContext = mappingContext;
        this.conversionService = new DefaultConversionService();
        this.simpleTypeHolder = new SimpleTypeHolder();
    }

    Foxml11Document prepareFoxmlDocForWrite(final Object source) {
        Assert.notNull(source);
        FedoraPersistentEntity<?> ent = mappingContext.getPersistentEntity(unwrapJaxbType(source));

        // if this is a document entity
        if (!DocumentFedoraPersistentEntity.class.isAssignableFrom(ent.getClass())) {
            throw new MappingException("Entity " + ent.getType().getSimpleName() + " is not of type DocumentFedoraPersistentEntity");
        }
        final DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) ent;

        final Foxml11Document foxmlDoc = new Foxml11Document(toPid(source, entity));

        // add default object properties
        addFedoraObjectProperties(foxmlDoc, entity);

        // add main datastream and datastream version elements
        addDatastream(foxmlDoc, entity);

        entity.doWithDatastreamRefs(new DatastreamReferencePropertyHandler() {
            @Override
            public void doWithDatastreamRef(DatastreamReferenceFedoraPersistentProperty property) {
                addDatastream(foxmlDoc, (DatastreamFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(property));
            }
        });

        // add default RELS-EXT datastream if this entity has any associations
        addRelsExtDatastream(foxmlDoc);

        return foxmlDoc;
    }

    Foxml11Document prepareFoxmlDocForRead(String pid, Class<?> type) {
        Assert.hasText(pid);
        Assert.notNull(type);

        // get entity's type
        ClassTypeInformation typeInfo = ClassTypeInformation.from(type);

        // get entity
        FedoraPersistentEntity<?> ent = mappingContext.getPersistentEntity(typeInfo);

        // create empty FOXML document
        final Foxml11Document foxmlDoc = new Foxml11Document(pid);

        // if this is a document entity
        if (DocumentFedoraPersistentEntity.class.isAssignableFrom(ent.getClass())) {
            DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) ent;
            // add default object properties
            addFedoraObjectProperties(foxmlDoc, entity);

            // add default datastream and datastream version elements
            addDatastream(foxmlDoc, entity);

            // add embedded datastreams
            entity.doWithDatastreamRefs(new DatastreamReferencePropertyHandler() {
                @Override
                public void doWithDatastreamRef(DatastreamReferenceFedoraPersistentProperty property) {
                    if (!property.lazyLoad()) {
                        addDatastream(foxmlDoc, (DatastreamFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(property));
                    }
                }
            });

            // add default RELS-EXT datastream if this entity has any associations
            addRelsExtDatastream(foxmlDoc);
        }

        return foxmlDoc;
    }

    @Override
    public Foxml11Document write(Object source) {
        Foxml11Document foxmlDoc = prepareFoxmlDocForWrite(source);
        write(source, foxmlDoc);
        return foxmlDoc;
    }

    @Override
    public <I, S> S read(I id, Class<S> type) {
        FedoraPersistentEntity<?> ent = mappingContext.getPersistentEntity(type);
        if (!DocumentFedoraPersistentEntity.class.isAssignableFrom(ent.getClass())) {
            throw new MappingException("No document entity registered with " + type.getName());
        }
        DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) ent;
        final String pid = entity.getPidProperty().getPidCreator().getPid(id, entity);
        final Foxml11Document foxmlDoc = prepareFoxmlDocForRead(pid, type);

        // fetch xml content for default datastream
        DatastreamContents dsContents = wsConnection.getApiaWsClient().getDatastreamContents(pid,
                entity.getDsId());
        foxmlDoc.addXmlContent(entity.getDsvId(), new String(dsContents.getContents()));

        // fetch xml content for datastream references which are not lazily loaded
        entity.doWithDatastreamRefs(new DatastreamReferencePropertyHandler() {
            @Override
            public void doWithDatastreamRef(DatastreamReferenceFedoraPersistentProperty property) {
                if (!property.lazyLoad()) {
                    DatastreamFedoraPersistentEntity<?> dsRefEntity = (DatastreamFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(property);
                    DatastreamContents dsRefContents = wsConnection.getApiaWsClient().getDatastreamContents(pid, dsRefEntity.getDsId());
                    foxmlDoc.addXmlContent(dsRefEntity.getDsvId(), new String(dsRefContents.getContents()));
                }
            }
        });

        return read(type, foxmlDoc);
    }

    @Override
    public <S> S read(String uri, Class<S> type) {
        Assert.hasText(uri);
        Assert.notNull(type);

        String pid = StringUtils.substringAfter(uri, org.fcrepo.common.Constants.FEDORA.uri);

        // find entity matching the uri
        DocumentFedoraPersistentEntity<?> entity = findEntityMatchingUri(pid);

        if (entity == null) {
            throw new MappingException("No entity registered in the mapping context matching URI " + uri);
        }

        Object id = entity.getPidProperty().getPidCreator().getId(pid, Object.class, entity);

        return read(id, type);
    }

    private void addFedoraObjectProperties(Foxml11Document foxmlDoc, DocumentFedoraPersistentEntity<?> entity) {
        foxmlDoc.addObjectProperties();

        foxmlDoc.addObjectProperty(Foxml11Document.Property.STATE, entity.getFoxmlState().toString());
        foxmlDoc.addObjectProperty(Foxml11Document.Property.LABEL, entity.getFoxmlLabel());
        // no OWNER_ID in  Foxml11Document.Property

    }

    private void addDatastream(Foxml11Document foxmlDoc, DatastreamFedoraPersistentEntity<?> entity) {
        foxmlDoc.addDatastream(entity.getDsId(), entity.getState(), entity.getControlGroup(), false);
        foxmlDoc.addDatastreamVersion(entity.getDsId(), entity.getDsvId(), entity.getDsvMimetype(),
                entity.getDsvLabel(), 1, new Date(System.currentTimeMillis()));

    }

    private void addRelsExtDatastream(Foxml11Document foxmlDoc) {
        foxmlDoc.addDatastream(Constants.RELS_EXT_DATASTREAM_ID, Foxml11Document.State.A, Foxml11Document.ControlGroup.X, false);
        foxmlDoc.addDatastreamVersion(Constants.RELS_EXT_DATASTREAM_ID,
                Constants.RELS_EXT_DATASTREAM_ID + Constants.DEFAULT_DATASTREAM_VERSION_SUFFIX,
                Constants.RELS_EXT_DATASTREAM_MIME_TYPE, Constants.DEFAULT_RELS_EXT_DATASTREAM_LABEL,
                1, new Date(System.currentTimeMillis()));
    }


    @Override
    public void write(final Object source, Foxml11Document sink) {
        Assert.notNull(sink);
        if (source == null) {
            return;
        }
        // get persistent entity
        FedoraPersistentEntity<?> entity = mappingContext.getPersistentEntity(unwrapJaxbType(source));
        doWrite(source, sink, entity);
    }

    private void doWrite(final Object source, final Foxml11Document foxmlDoc, FedoraPersistentEntity<?> ent) {
        if (source == null) {
            return;
        }

        if (ent == null) {
            throw new MappingException("No entity is registered with the mapping context for type " + source.getClass().getName());
        }

        if (!DocumentFedoraPersistentEntity.class.isAssignableFrom(ent.getClass())) {
            throw new MappingException("Cannot write entity of type " + ent.getClass());
        }
        final DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) ent;

        // traverse entity to configure marshaller
        final FedoraEntityMarshaller marshaller = mappingContext.getMarshaller(entity);
        if (!marshaller.isConfigured()) {
            traverseEntity(entity, Collections.singletonList((FedoraEntityTraversalListener) marshaller), new ArrayList<Class<?>>());
            marshaller.setConfigured(true);
        }

        // write default datastream for this object
        writeDsvXmlContent(source, entity, foxmlDoc, marshaller);

        // write embedded datastreams
        final BeanWrapper<?> wrapper = BeanWrapper.create(source, conversionService);
        entity.doWithDatastreamRefs(new DatastreamReferencePropertyHandler() {
            @Override
            public void doWithDatastreamRef(DatastreamReferenceFedoraPersistentProperty property) {
                Object dsvObj = wrapper.getProperty(property);
                if (dsvObj != null) {
                    writeDsvXmlContent(dsvObj, (DatastreamFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(property), foxmlDoc,
                            marshaller);
                }
            }
        });

        // traverse entity source
        traverseEntitySource(source, entity, Collections.singletonList((FedoraEntitySourceTraversalListener) foxmlDoc), new ArrayList<Class<?>>());

        // write rels-exts
        foxmlDoc.writeRelsExtDatastreamContent();

    }

    private void traverseEntity(FedoraPersistentEntity<?> entity, final List<FedoraEntityTraversalListener> listeners, final List<Class<?>> ancestorTypes) {
        // check if has been traversed already
        if (ancestorTypes.contains(entity.getType())) {
            return;
        }

        // add entity's type to the list
        ancestorTypes.add(entity.getType());

        final FedoraMappingConverter fedoraConverter = this;

        for (FedoraEntityTraversalListener listener : listeners) {
            listener.applyWithEntity(entity, null, fedoraConverter);
        }

        entity.doWithProperties(new PropertyHandler<FedoraPersistentProperty>() {
            @Override
            public void doWithPersistentProperty(FedoraPersistentProperty property) {
                if (property.isCollectionLike()) {

                    // return if item type is a simple type
                    Class<?> itemType = property.getComponentType();
                    if (simpleTypeHolder.isSimpleType(itemType)) {
                        return;
                    }
                    // call listeners with collection item entity
                    FedoraPersistentEntity<?> itemEntity = mappingContext.getPersistentEntity(itemType);
                    for (FedoraEntityTraversalListener listener : listeners) {
                        listener.applyWithCollectionItemEntity(itemEntity, property, fedoraConverter);
                    }
                    traverseEntity(itemEntity, listeners, ancestorTypes);
                } else if (property.isMap()) {
                    // call listeners with key and value entities of the map's entry
                    TypeInformation<?>[] entryTypes = property.getTypeInformation().getTypeArguments()
                            .toArray(new TypeInformation<?>[2]);

                    // check that key is a simple type
                    Class<?> keyType = entryTypes[0].getType();
                    if (!simpleTypeHolder.isSimpleType(keyType)) {
                        throw new MappingException("Marshalling of maps with entity key is not supported");
                    }

                    Class<?> valueType = entryTypes[1].getType();
                    FedoraPersistentEntity<?> valueEntity = mappingContext.getPersistentEntity(entryTypes[1]);
                    for (FedoraEntityTraversalListener listener : listeners) {
                        listener.applyWithMapEntryEntities(keyType,
                                valueEntity,
                                valueType, property, fedoraConverter);
                    }
                    if (!simpleTypeHolder.isSimpleType(valueType)) {
                        traverseEntity(valueEntity, listeners, ancestorTypes);
                    }
                } else {
                    if (property.isEntity()) {
                        // call listeners with the property's entity
                        FedoraPersistentEntity<?> propEntity = mappingContext.getPersistentEntity(property);
                        for (FedoraEntityTraversalListener listener : listeners) {
                            listener.applyWithEntity(propEntity, property, fedoraConverter);
                        }
                        traverseEntity(propEntity, listeners, ancestorTypes);
                    }
                }
            }
        });
    }

    private void traverseEntitySource(final Object source, final FedoraPersistentEntity<?> entity,
                                      final List<FedoraEntitySourceTraversalListener> listeners, final List<Class<?>> ancestorTypes) {
        // check if has been traversed already
        if (ancestorTypes.contains(entity.getType())) {
            return;
        }

        // add entity's type to the list
        ancestorTypes.add(entity.getType());

        final FedoraConverter fedoraConverter = this;

        for (FedoraEntitySourceTraversalListener listener : listeners) {
            listener.applyWithPropertySource(source, entity, null, fedoraConverter);
        }

        final BeanWrapper<?> wrapper = BeanWrapper.create(source, conversionService);

        entity.doWithProperties(new PropertyHandler<FedoraPersistentProperty>() {
            @Override
            public void doWithPersistentProperty(FedoraPersistentProperty property) {
                Object propertySource = wrapper.getProperty(property);
                if (propertySource == null) {
                    return;
                }
                if (property.isCollectionLike()) {
                    if (property.isArray()) {
                        for (Object item : (Object[]) propertySource) {
                            processItemProperty(property, item);
                        }
                    } else {
                        for (Object item : (Collection<?>) propertySource) {
                            processItemProperty(property, item);
                        }
                    }
                } else if (property.isMap()) {
                    // TODO: implement for maps
                } else {
                    if (property.isEntity()) {
                        FedoraPersistentEntity<?> propertyEntity = mappingContext.getPersistentEntity(property);
                        for (FedoraEntitySourceTraversalListener listener : listeners) {
                            listener.applyWithPropertySource(propertySource, propertyEntity,
                                    property, fedoraConverter);
                        }
                        if (!simpleTypeHolder.isSimpleType(propertyEntity.getType())) {
                            traverseEntitySource(propertySource, propertyEntity, listeners, ancestorTypes);
                        }
                    }
                }
            }

            private void processItemProperty(FedoraPersistentProperty property, Object item) {
                FedoraPersistentEntity<?> propEntity = mappingContext.getPersistentEntity(property.getComponentType());
                for (FedoraEntitySourceTraversalListener listener : listeners) {
                    listener.applyWithCollectionItemSource(item, propEntity, property, fedoraConverter);
                }
                if (!simpleTypeHolder.isSimpleType(propEntity.getType())) {
                    traverseEntitySource(item, propEntity, listeners, ancestorTypes);
                }
            }
        });

    }

    @Override
    public <S> S read(Class<S> type, Foxml11Document source) {

        Assert.notNull(type);
        Assert.notNull(source);

        FedoraPersistentEntity<?> entity = mappingContext.getPersistentEntity(ClassTypeInformation.from(type));

        if (entity == null) {
            throw new MappingException("No entity is registered with " + type.getSimpleName());
        }

        return type.cast(doRead(source, entity));
    }

    private Object doRead(final Foxml11Document foxmlDoc, FedoraPersistentEntity<?> ent) {

        if (!DocumentFedoraPersistentEntity.class.isAssignableFrom(ent.getClass())) {
            throw new MappingException("Cannot read entity of type " + ent.getClass());
        }
        final DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) ent;

        // get the default datastream contents
        final Object targetObj = readDsvXmlContent(entity, foxmlDoc);

        final BeanWrapper<?> wrapper = BeanWrapper.create(targetObj, conversionService);

        // read any Datastream references
        entity.doWithDatastreamRefs(new DatastreamReferencePropertyHandler() {
            @Override
            public void doWithDatastreamRef(DatastreamReferenceFedoraPersistentProperty property) {
                // check if datastream needs to be loaded lazily
                if (property.lazyLoad()) {
                    logger.debug("Creating dynamic proxy for the lazy load of datastream {}", property.getType().getSimpleName());
                    Object dsvObj = createDynamicProxyForDatastreamReference(wrapper.getProperty(entity.getPidProperty()),
                            property);
                    wrapper.setProperty(property, dsvObj);
                } else {
                    // if not lazy read dsv content for the datastream and set the property on the target object directly
                    logger.debug("Loading datastream {} directly", property.getType().getSimpleName());
                    Object dsvObj = readDsvXmlContent((DatastreamFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(property), foxmlDoc);
                    wrapper.setProperty(property, dsvObj);
                }
            }
        });

        return targetObj;
    }

    String toPid(Object entObj, DocumentFedoraPersistentEntity<?> entity) {
        BeanWrapper<Object> entBean = BeanWrapper.create(unwrapJaxbValue(entObj), conversionService);
        return entity.getPidProperty().getPidCreator()
                .getPid(entBean.getProperty(entity.getPidProperty()), entity);

    }

    String toFedoraObjectUri(Object entObj, DocumentFedoraPersistentEntity<?> entity) {
        return org.fcrepo.common.Constants.FEDORA.uri + toPid(entObj, entity);
    }

    public String toFedoraObjectUri(Object source) {
        return toFedoraObjectUri(source, (DocumentFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(source.getClass()));
    }

    @Override
    public Object createDynamicProxyForFedoraObjectReference(String uri) {
        String pid = StringUtils.substringAfter(uri, org.fcrepo.common.Constants.FEDORA.uri);

        // find entity matching the uri
        DocumentFedoraPersistentEntity<?> entity = findEntityMatchingUri(pid);

        if (entity == null) {
            throw new MappingException("No entity registered in the mapping context matching URI " + uri);
        }

        Object id = entity.getPidProperty().getPidCreator().getId(pid, Object.class, entity);

        Object proxy;
        try {
            Class<?> delegateType = entity.getType();
            proxy = new ByteBuddy()
                    .subclass(delegateType)
                    .implement(DelegatingDynamicProxy.class)
                    .method(MethodMatchers.isGetter().or(MethodMatchers.isSetter()))
                    .intercept(MethodDelegation.to(new FedoraObjectReferenceDynamicProxyInterceptor<>(id, delegateType, this)))
                    .make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MappingException("Cannot instantiate a dynamic proxy for Fedora Object URI " + uri, e);
        }
        return proxy;
    }

    private Object createDynamicProxyForDatastreamReference(Object id, DatastreamReferenceFedoraPersistentProperty property) {
        DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) property.getOwner();
        DatastreamFedoraPersistentEntity<?> dsRefEntity = (DatastreamFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(property);
        Object proxy;
        try {
            Class<?> delegateType = dsRefEntity.getType();
            proxy = new ByteBuddy()
                    .subclass(delegateType)
                    .implement(DelegatingDynamicProxy.class)
                    .method(MethodMatchers.isGetter().or(MethodMatchers.isSetter()))
                    .intercept(MethodDelegation.to(new DatastreamReferenceDynamicProxyInterceptor<>(id, entity.getType(),
                            dsRefEntity.getDsId(), dsRefEntity.getType(), this)))
                    .make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MappingException("Cannot instantiate a dynamic proxy for datastream reference property " +
                    property.getName() + " of entity " + entity.getType().getSimpleName(), e);
        }
        return proxy;
    }

    @Override
    public <I, S, D> D readDatastream(I id, Class<S> type, String dsId, Class<D> dsType) {
        Assert.notNull(id);
        Assert.notNull(type);
        Assert.hasText(dsId);
        Assert.notNull(dsType);
        FedoraPersistentEntity<?> ent = mappingContext.getPersistentEntity(type);
        if (!DocumentFedoraPersistentEntity.class.isAssignableFrom(ent.getClass())) {
            throw new MappingException("Entity for type " + type.getSimpleName() + " must be of type DocumentFedoraPersistentEntity");
        }
        DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) ent;
        FedoraPersistentEntity<?> dsEnt = mappingContext.getPersistentEntity(dsType);
        if (!DatastreamFedoraPersistentEntity.class.isAssignableFrom(dsEnt.getClass())) {
            throw new MappingException("Entity for type " + dsType.getSimpleName() + " must be of type DatastreamFedoraPersistentEntity");
        }
        DatastreamFedoraPersistentEntity<?> dsEntity = (DatastreamFedoraPersistentEntity<?>) dsEnt;
        String pid = entity.getPidProperty().getPidCreator().getPid(id, entity);
        Foxml11Document foxmlDoc = prepareFoxmlDocForRead(pid, type);
        addDatastream(foxmlDoc, dsEntity);
        DatastreamContents dsContents = wsConnection.getApiaWsClient().getDatastreamContents(pid, dsId);
        foxmlDoc.addXmlContent(dsEntity.getDsvId(), new String(dsContents.getContents()));
        return dsType.cast(readDsvXmlContent(dsEntity, foxmlDoc));
    }

    @Override
    public MappingContext<? extends FedoraPersistentEntity<?>, FedoraPersistentProperty> getMappingContext() {
        return mappingContext;
    }

    @Override
    public String getPid(Object source) {
        Assert.notNull(source);
        BeanWrapper<?> wrapper = BeanWrapper.create(source, conversionService);
        DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) mappingContext.getPersistentEntity(source.getClass());
        DefaultPidFedoraPersistentProperty pidProp = entity.getPidProperty();
        Object id = wrapper.getProperty(pidProp);
        return pidProp.getPidCreator().getPid(id, entity);
    }

    private DocumentFedoraPersistentEntity<?> findEntityMatchingUri(String pid) {
        DocumentFedoraPersistentEntity<?> entity = null;
        for (FedoraPersistentEntity<?> ent : mappingContext.getPersistentEntities()) {
            if (ent instanceof DocumentFedoraPersistentEntity) {
                DocumentFedoraPersistentEntity<?> dsEnt = (DocumentFedoraPersistentEntity<?>) ent;
                PidCreator pidCreator = dsEnt.getPidProperty().getPidCreator();
                if (pidCreator.isValidPid(pid, dsEnt)) {
                    entity = dsEnt;
                    break;
                }
            }
        }
        return entity;
    }

    private void writeDsvXmlContent(final Object dsvObj, final DatastreamFedoraPersistentEntity<?> entity, Foxml11Document foxmlDoc, FedoraEntityMarshaller marshaller) {
        // marshal datastream version content
        foxmlDoc.addXmlContent(entity.getDsvId(), marshaller.marshal(dsvObj));
    }

    private Object readDsvXmlContent(DatastreamFedoraPersistentEntity<?> entity, Foxml11Document foxmlDoc) {
        String xmlContent = foxmlDoc.getXmlContent(entity.getDsvId());
        if (xmlContent == null) {
            throw new MappingException("No XML contents for datastream version " + entity.getDsvId());
        }
        return mappingContext.getMarshaller(entity).unmarshal(xmlContent);
    }

    private Object unwrapJaxbValue(Object source) {
        Object value;
        if (JAXBElement.class.isAssignableFrom(source.getClass())) {
            value = ((JAXBElement<?>) source).getValue();
        } else {
            value = source;
        }
        return value;
    }

    private Class<?> unwrapJaxbType(Object source) {
        Class<?> clazz;
        if (JAXBElement.class.isAssignableFrom(source.getClass())) {
            clazz = ((JAXBElement<?>) source).getDeclaredType();
        } else {
            clazz = source.getClass();
        }
        return clazz;
    }

}
