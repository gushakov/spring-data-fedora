package ch.unil.spring.data.fedora.core.mapping.annotation;

import java.lang.annotation.*;

/**
 * @author gushakov
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatastreamRef {
    boolean lazyLoad() default false;
}
