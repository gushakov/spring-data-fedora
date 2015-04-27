package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.annotation.RelsExt;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author gushakov
 */
public class DefaultRelsExtFedoraPersistentProperty extends GenericFedoraPersistentProperty implements RelsExtFedoraPersistentProperty {

    private RelsExt relsExtAnnot;

    public DefaultRelsExtFedoraPersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                                  PersistentEntity<?, FedoraPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);

        relsExtAnnot = findAnnotation(RelsExt.class);

    }

    @Override
    public boolean isRelsExt() {
        return true;
    }

    @Override
    public Constants.Rels_Ext_Rdf_Property getRdfRelationProperty() {
        return relsExtAnnot.rdfProperty();
    }

}
