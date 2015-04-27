package ch.unil.spring.data.fedora.config;

/**
 * @author gushakov
 */
public interface WsFedoraConnectionFactory {

    public static final String DEFAULT_APIA_SERVICE_CONTEXT = "/fedora/services/access";
    public static final String DEFAULT_APIM_SERVICE_CONTEXT = "/fedora/services/management";
    public static final String DEFAULT_API_JAXB_PACKAGE = "ch.unil.spring.data.fedora.ws.api.jaxb";

    WsFedoraConnection newConnection();

}
