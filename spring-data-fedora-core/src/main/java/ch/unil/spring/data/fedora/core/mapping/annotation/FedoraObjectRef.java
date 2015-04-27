package ch.unil.spring.data.fedora.core.mapping.annotation;

import ch.unil.spring.data.fedora.core.Constants;

import java.lang.annotation.*;

/**
 * @author gushakov
 */
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FedoraObjectRef {
    boolean lazyLoad() default false;

    boolean addAsRelsExt() default true;

    Constants.Rels_Ext_Rdf_Property rdfProperty() default Constants.Rels_Ext_Rdf_Property.HasMember;
}
