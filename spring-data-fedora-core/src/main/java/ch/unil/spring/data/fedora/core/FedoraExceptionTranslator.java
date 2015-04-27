package ch.unil.spring.data.fedora.core;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.ws.soap.client.SoapFaultClientException;

/*
 * Based on org.springframework.data.mongodb.core.MongoExceptionTranslator
 */

/**
 * @author gushakov
 */
public class FedoraExceptionTranslator implements PersistenceExceptionTranslator {
    private static final String OBJECT_NOT_FOUND_ERROR_MESSAGE = "Object not found in low-level storage";

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        DataAccessException dae = null;

        if (e instanceof SoapFaultClientException && ((SoapFaultClientException) e).getFaultStringOrReason()
                .contains(OBJECT_NOT_FOUND_ERROR_MESSAGE)) {
            dae = new EmptyResultDataAccessException(((SoapFaultClientException) e).getFaultStringOrReason(), 1, e);
        } else {
            dae = new UncategorizedFedoraRepositoryAccessException(e.getMessage(), e);
        }

        return dae;
    }
}
