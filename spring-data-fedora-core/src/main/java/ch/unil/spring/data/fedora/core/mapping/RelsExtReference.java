package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;

/**
 * @author gushakov
 */
public class RelsExtReference<T> {

    private Constants.Rels_Ext_Rdf_Property rdfProperty;

    private T target;

    public RelsExtReference(T target, Constants.Rels_Ext_Rdf_Property rdfProperty) {
        this.target = target;
        this.rdfProperty = rdfProperty;
    }

    public T getTarget() {
        return target;
    }

    public Constants.Rels_Ext_Rdf_Property getRdfRelationProperty() {
        return rdfProperty;
    }

    public Class<?> getType() {
        return target.getClass();
    }
}
