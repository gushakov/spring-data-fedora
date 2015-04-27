package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.annotation.Datastream;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import org.springframework.data.util.TypeInformation;

/**
 * @author gushakov
 */
public class DefaultDatastreamFedoraPersistentEntity<T> extends GenericFedoraPersistentEntity<T>
        implements DatastreamFedoraPersistentEntity<T> {
    private Datastream dsAnnot;

    public DefaultDatastreamFedoraPersistentEntity(TypeInformation<T> information) {
        super(information);

        dsAnnot = findAnnotation(Datastream.class);
    }

    @Override
    public String getNamespace() {
        return dsAnnot.namespace();
    }

    @Override
    public Foxml11Document.State getState() {
        return dsAnnot.state();
    }

    @Override
    public Foxml11Document.ControlGroup getControlGroup() {
        return dsAnnot.controlGroup();
    }

    @Override
    public String getDsvLabel() {
        String label = dsAnnot.dsvLabel();
        if (label.equals(Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN)) {
            // use dsv label creator
            try {
                label = dsAnnot.dsvLabelCreator().newInstance().getDsvLabel(this);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Cannot create datastream version label for entity " + getType().getSimpleName(), e);
            }
        }
        return label;
    }

    @Override
    public String getDsvMimetype() {
        return dsAnnot.dsvMimetype();
    }

    @Override
    public String getDsId() {
        String dsId = dsAnnot.id();
        // use datastream id creator if was not set on the annotation
        if (dsId.equals(Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN)) {
            try {
                dsId = dsAnnot.dsIdCreator().newInstance().getDsId(this);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Cannot create default datastream id", e);
            }

        }
        return dsId;
    }

    @Override
    public String getDsvId() {
        String dsvId = dsAnnot.dsvId();
        // use creator if was not set on the annotation
        if (dsvId.equals(Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN)) {
            try {
                dsvId = dsAnnot.dsvIdCreator().newInstance().getDsvId(this);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Cannot create default datastream version id", e);
            }
        }
        return dsvId;
    }
}
