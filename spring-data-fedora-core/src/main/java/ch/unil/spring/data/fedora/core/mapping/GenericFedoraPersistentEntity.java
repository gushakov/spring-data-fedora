package ch.unil.spring.data.fedora.core.mapping;

import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;

/**
 * @author gushakov
 */
public class GenericFedoraPersistentEntity<T> extends BasicPersistentEntity<T, FedoraPersistentProperty>
        implements FedoraPersistentEntity<T> {

    public GenericFedoraPersistentEntity(TypeInformation<T> information) {
        super(information);
    }

    @Override
    public void doWithRelsExts(final RelsExtPropertyHandler handler) {
        doWithProperties(new PropertyHandler<FedoraPersistentProperty>() {
            @Override
            public void doWithPersistentProperty(FedoraPersistentProperty property) {
                if (property.isRelsExt()) {
                    handler.doWithRelsExt((RelsExtFedoraPersistentProperty) property);
                }
            }
        });
    }

    @Override
    public void doWithDatastreamRefs(final DatastreamReferencePropertyHandler handler) {
        doWithProperties(new PropertyHandler<FedoraPersistentProperty>() {
            @Override
            public void doWithPersistentProperty(FedoraPersistentProperty property) {
                if (property.isDatastreamRef()) {
                    handler.doWithDatastreamRef((DatastreamReferenceFedoraPersistentProperty) property);
                }
            }
        });
    }
}
