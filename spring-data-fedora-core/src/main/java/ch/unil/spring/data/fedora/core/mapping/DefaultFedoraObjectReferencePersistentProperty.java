package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObjectRef;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author gushakov
 */
public class DefaultFedoraObjectReferencePersistentProperty extends GenericFedoraPersistentProperty implements FedoraObjectReferencePersistentProperty {

    private FedoraObjectRef foRefAnnot;

    public DefaultFedoraObjectReferencePersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                                          PersistentEntity<?, FedoraPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
        this.foRefAnnot = findAnnotation(FedoraObjectRef.class);
    }

    @Override
    public boolean isFedoraObjectRef() {
        return true;
    }

    @Override
    public boolean lazyLoad() {
        return foRefAnnot.lazyLoad();
    }

    @Override
    public boolean addAsRelsExt() {
        return foRefAnnot.addAsRelsExt();
    }

    @Override
    public Constants.Rels_Ext_Rdf_Property getRdfProperty() {
        return foRefAnnot.rdfProperty();
    }
}
