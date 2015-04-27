package ch.unil.spring.data.fedora.core.convert;

/**
 * @author gushakov
 */
public interface DelegatingDynamicProxy<T> {
    T getDelegate();
}
