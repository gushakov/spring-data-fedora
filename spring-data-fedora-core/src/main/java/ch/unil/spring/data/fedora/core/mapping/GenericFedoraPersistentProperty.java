package ch.unil.spring.data.fedora.core.mapping;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author gushakov
 */
public class GenericFedoraPersistentProperty extends AnnotationBasedPersistentProperty<FedoraPersistentProperty>
        implements FedoraPersistentProperty {

    public GenericFedoraPersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                           PersistentEntity<?, FedoraPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
    }

    @Override
    protected Association<FedoraPersistentProperty> createAssociation() {
        return new Association<FedoraPersistentProperty>(this, null);
    }

    @Override
    public boolean isPid() {
        return false;
    }

    @Override
    public boolean isRelsExt() {
        return false;
    }

    @Override
    public boolean isDatastreamRef() {
        return false;
    }

    @Override
    public boolean isFedoraObjectRef() {
        return false;
    }
}
