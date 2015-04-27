package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import org.springframework.data.util.TypeInformation;

/**
 * @author gushakov
 */
public class DefaultDocumentFedoraPersistentEntity<T> extends DefaultDatastreamFedoraPersistentEntity<T>
        implements DocumentFedoraPersistentEntity<T> {

    private FedoraObject foAnnot;

    public DefaultDocumentFedoraPersistentEntity(TypeInformation<T> information) {
        super(information);
        foAnnot = findAnnotation(FedoraObject.class);
    }

    @Override
    public DefaultPidFedoraPersistentProperty getPidProperty() {
        return (DefaultPidFedoraPersistentProperty) this.getIdProperty();
    }

    @Override
    public Foxml11Document.State getFoxmlState() {
        return foAnnot.state();
    }

    @Override
    public String getFoxmlLabel() {
        String label = foAnnot.label();
        if (label.equals(Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN)) {
            // use label creator to create the label
            try {
                label = foAnnot.foxmlLabelCreator().newInstance().getFoxmlLabel(this);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Cannot create label for a FedoraObject annotated entity " + getType().getSimpleName(), e);
            }
        }
        return label;
    }

    @Override
    public String getFoxmlOwnerId() {
        return foAnnot.ownerId();
    }
}
