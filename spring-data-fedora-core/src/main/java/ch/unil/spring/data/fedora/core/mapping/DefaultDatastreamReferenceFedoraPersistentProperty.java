package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.mapping.annotation.Datastream;
import ch.unil.spring.data.fedora.core.mapping.annotation.DatastreamRef;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author gushakov
 */
public class DefaultDatastreamReferenceFedoraPersistentProperty extends GenericFedoraPersistentProperty implements DatastreamReferenceFedoraPersistentProperty {
    private DatastreamRef dsRefAnnot;

    @Override
    public boolean isDatastreamRef() {
        return true;
    }

    public DefaultDatastreamReferenceFedoraPersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                                              PersistentEntity<?, FedoraPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);

        this.dsRefAnnot = findAnnotation(DatastreamRef.class);

        if (AnnotationUtils.findAnnotation(this.getActualType(), Datastream.class) == null) {
            throw new MappingException("Property " + this.getName() + " of entity " + this.getOwner().getType().getSimpleName() +
                    " annotated with DatastreamRef refer to a Datastream type");
        }
    }

    @Override
    public boolean lazyLoad() {
        return dsRefAnnot.lazyLoad();
    }


}
