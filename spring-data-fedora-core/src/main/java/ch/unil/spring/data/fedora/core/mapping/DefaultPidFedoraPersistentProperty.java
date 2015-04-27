package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author gushakov
 */
public class DefaultPidFedoraPersistentProperty extends GenericFedoraPersistentProperty
        implements PidFedoraPersistentProperty {

    private Pid pidAnnot;

    public DefaultPidFedoraPersistentProperty(Field field, PropertyDescriptor propertyDescriptor, PersistentEntity<?, FedoraPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
        this.pidAnnot = findAnnotation(Pid.class);
    }

    @Override
    public boolean isPid() {
        return true;
    }

    @Override
    public String getNamespaceId() {
        return pidAnnot.namespaceId();
    }

    @Override
    public boolean marshalAsAttribute() {
        return pidAnnot.asAttribute();
    }

    @Override
    public PidCreator getPidCreator() {
        try {
            return pidAnnot.pidCreator().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot create a new instance of PidCreator", e);
        }
    }

}
