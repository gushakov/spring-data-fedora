package ch.unil.spring.data.fedora.ws.api;

import ch.unil.spring.data.fedora.core.utils.DatastreamContents;
import ch.unil.spring.data.fedora.core.utils.FindObjectsResults;

/**
 * @author gushakov
 */
public interface ApiaWsClient {

    boolean exists(String pid);

    DatastreamContents getDatastreamContents(String pid, String dsId);

    FindObjectsResults findObjects(String field, String operator, String phrase, long maxResults);

    FindObjectsResults resumeFindObjects(String sessionToken);
}
