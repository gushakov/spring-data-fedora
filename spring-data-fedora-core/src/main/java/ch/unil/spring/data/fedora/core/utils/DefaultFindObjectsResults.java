package ch.unil.spring.data.fedora.core.utils;

import ch.unil.spring.data.fedora.core.query.FindObjectsPageRequest;
import ch.unil.spring.data.fedora.ws.api.jaxb.FieldSearchResult;
import ch.unil.spring.data.fedora.ws.api.jaxb.ListSession;
import ch.unil.spring.data.fedora.ws.api.jaxb.ObjectFields;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gushakov
 */
public class DefaultFindObjectsResults implements FindObjectsResults {

    private FieldSearchResult fieldSearchResult;

    public DefaultFindObjectsResults(FieldSearchResult fieldSearchResult) {
        this.fieldSearchResult = fieldSearchResult;
    }

    @Override
    public String getSessionToken() {
        String token = null;
        JAXBElement<ListSession> listSession = fieldSearchResult.getListSession();
        if (listSession != null) {
            token = listSession.getValue().getToken();
        }
        return token;
    }

    @Override
    public long getCursor() {
        long cursor;
        JAXBElement<ListSession> listSession = fieldSearchResult.getListSession();
        if (listSession != null) {
            cursor = listSession.getValue().getCursor().longValue();
        } else {
            cursor = FindObjectsPageRequest.NO_CURSOR;
        }
        return cursor;
    }

    @Override
    public List<String> getPids() {
        List<String> contents = new ArrayList<>();
        List<ObjectFields> objectFields = fieldSearchResult.getResultList().getObjectFields();
        if (objectFields != null) {
            for (ObjectFields fields : objectFields) {
                contents.add(fields.getPid().getValue());
            }
        }
        return contents;
    }

}
