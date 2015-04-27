package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;

/**
 * @author gushakov
 */
public interface RelsExtFedoraPersistentProperty extends FedoraPersistentProperty {

    Constants.Rels_Ext_Rdf_Property getRdfRelationProperty();

}
