package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.Constants;

/**
 * @author gushakov
 */
public interface FedoraObjectReferencePersistentProperty extends FedoraPersistentProperty {

    boolean lazyLoad();

    boolean addAsRelsExt();

    Constants.Rels_Ext_Rdf_Property getRdfProperty();
}
