package ch.unil.spring.data.fedora.core.mapping.annotation;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.DefaultFoxmlLabelCreator;
import ch.unil.spring.data.fedora.core.mapping.FoxmlLabelCreator;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * @author gushakov
 */

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Persistent
@Datastream
public @interface FedoraObject {

    Foxml11Document.State state() default Foxml11Document.State.A;

    String label() default Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN;

    String ownerId() default Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN;

    Class<? extends FoxmlLabelCreator> foxmlLabelCreator() default DefaultFoxmlLabelCreator.class;

}
