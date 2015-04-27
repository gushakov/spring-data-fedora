package ch.unil.spring.data.fedora.core;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * @author gushakov
 */
public class UncategorizedFedoraRepositoryAccessException extends UncategorizedDataAccessException {
    /**
     * Constructor for UncategorizedDataAccessException.
     *
     * @param msg   the detail message
     * @param cause the exception thrown by underlying data access API
     */
    public UncategorizedFedoraRepositoryAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
