package ch.unil.spring.data.fedora.ws.api;

import ch.unil.spring.data.fedora.core.utils.Foxml11Document;

/**
 * @author gushakov
 */
public interface ApimWsClient {

    String ingest(Foxml11Document foxmlDoc, String comment);

    boolean purge(String pid, String comment);

    void save(byte[] xmlContent, String pid, String dsId);

}
