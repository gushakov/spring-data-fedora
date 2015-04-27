package ch.unil.spring.data.fedora.core.mapping.annotation;

import ch.unil.spring.data.fedora.core.Constants;

import java.lang.annotation.*;

/**
 * @author gushakov
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RelsExt {

    Constants.Rels_Ext_Rdf_Property rdfProperty() default Constants.Rels_Ext_Rdf_Property.HasMember;

}
