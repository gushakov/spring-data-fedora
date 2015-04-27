package ch.unil.spring.data.fedora.core.convert;

import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.AllArguments;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.BindingPriority;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.Origin;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.RuntimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author gushakov
 */
public abstract class AbstractDelegatingDynamicProxyInterceptor<I, T> {

    public static final Method GET_DELEGATE_METHOD = DelegatingDynamicProxy.class.getMethods()[0];

    private static final Logger logger = LoggerFactory.getLogger(AbstractDelegatingDynamicProxyInterceptor.class);

    protected I id;
    protected Class<T> delegateType;
    protected FedoraConverter converter;
    protected T delegate;

    public AbstractDelegatingDynamicProxyInterceptor(I id, Class<T> delegateType, FedoraConverter converter) {
        this.id = id;
        this.delegateType = delegateType;
        this.converter = converter;
    }

    @RuntimeType
    @BindingPriority(2)
    public Object interceptGetter(@Origin(cacheMethod = true) Method method, @AllArguments String[] args) {
        logger.debug("Intercepted getter {}", method.getName());

        Object result;

        if (delegate == null) {
            loadProxy();
        }

        if (method.getName().equals(GET_DELEGATE_METHOD.getName())) {
            logger.debug("Getting the delegate object");
            result = delegate;
        } else {

            try {
                result = method.invoke(delegate, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Cannot invoke method " + method.getName() + " on proxy delegate instance " + delegate);
            }
        }

        return result;
    }

    @RuntimeType
    @BindingPriority(1)
    public void interceptSetter(@Origin(cacheMethod = true) Method method, @AllArguments String[] args) {
        logger.debug("Intercepted setter {}", method.getName());
        if (delegate == null) {
            loadProxy();
        }
        try {

            method.invoke(delegate, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Cannot invoke method " + method.getName() + " on proxy delegate instance " + delegate);
        }
    }


    protected abstract void loadProxy();

}
