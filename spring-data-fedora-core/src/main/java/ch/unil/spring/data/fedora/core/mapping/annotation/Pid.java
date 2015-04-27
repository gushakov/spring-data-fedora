package ch.unil.spring.data.fedora.core.mapping.annotation;

import ch.unil.spring.data.fedora.core.Constants;
import ch.unil.spring.data.fedora.core.mapping.DefaultPidCreator;
import ch.unil.spring.data.fedora.core.mapping.PidCreator;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * @author gushakov
 */
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Persistent
@Id
public @interface Pid {

    String namespaceId() default Constants.DEFAULT_NAMESPACE_ID;

    boolean asAttribute() default true;

    Class<? extends PidCreator> pidCreator() default DefaultPidCreator.class;
}
