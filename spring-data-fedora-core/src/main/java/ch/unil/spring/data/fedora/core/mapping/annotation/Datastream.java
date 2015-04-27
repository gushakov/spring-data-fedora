package ch.unil.spring.data.fedora.core.mapping.annotation;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.*;
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
public @interface Datastream {

    String namespace() default Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN;

    String id() default Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN;

    Foxml11Document.State state() default Foxml11Document.State.A;

    Foxml11Document.ControlGroup controlGroup() default Foxml11Document.ControlGroup.X;

    String dsvId() default Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN;

    String dsvLabel() default Constants.DEFAULT_ANNOTATION_STRING_VALUE_TOKEN;

    String dsvMimetype() default Constants.DEFAULT_DATASTREAM_MIME_TYPE;

    Class<? extends DsIdCreator> dsIdCreator() default DefaultDsIdCreator.class;

    Class<? extends DsvIdCreator> dsvIdCreator() default DefaultDsvIdCreator.class;

    Class<? extends DsvLabelCreator> dsvLabelCreator() default DefaultDsvLabelCreator.class;
}
