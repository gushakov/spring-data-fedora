package ch.unil.spring.data.fedora.config;

import ch.unil.spring.data.fedora.ws.api.ApiaWsClient;
import ch.unil.spring.data.fedora.ws.api.ApimWsClient;

/**
 * @author gushakov
 */
public interface WsFedoraConnection {

    ApiaWsClient getApiaWsClient();

    ApimWsClient getApimWsClient();

}
