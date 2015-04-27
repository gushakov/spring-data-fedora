package ch.unil.spring.data.fedora.config;

import ch.unil.spring.data.fedora.ws.api.ApiaWsClient;
import ch.unil.spring.data.fedora.ws.api.ApiaWsTemplate;
import ch.unil.spring.data.fedora.ws.api.ApimWsClient;
import ch.unil.spring.data.fedora.ws.api.ApimWsTemplate;
import ch.unil.spring.data.fedora.ws.api.jaxb.ObjectFactory;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

/**
 * @author gushakov
 */
public class DefaultWsFedoraConnection implements WsFedoraConnection, InitializingBean {
    private ApiaWsClient apiaWsClient;

    private ApimWsClient apimWsClient;

    public DefaultWsFedoraConnection(String fedoraServerUrl, String fedoraUser, String fedoraPassword) {
        try {

            ObjectFactory jaxbFactory = new ObjectFactory();

            Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setContextPath(WsFedoraConnectionFactory.DEFAULT_API_JAXB_PACKAGE);
            marshaller.afterPropertiesSet();

            HttpComponentsMessageSender sender = new HttpComponentsMessageSender();
            sender.setCredentials(new UsernamePasswordCredentials(fedoraUser,
                    fedoraPassword));
            sender.afterPropertiesSet();

            ApiaWsTemplate apiaWsTemplate = new ApiaWsTemplate(fedoraServerUrl, jaxbFactory, marshaller, sender);
            apiaWsTemplate.afterPropertiesSet();
            this.apiaWsClient = apiaWsTemplate;

            ApimWsTemplate apimWsTemplate = new ApimWsTemplate(fedoraServerUrl, jaxbFactory, marshaller, sender);
            apimWsTemplate.afterPropertiesSet();
            this.apimWsClient = apimWsTemplate;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ApiaWsClient getApiaWsClient() {
        return apiaWsClient;
    }

    @Override
    public ApimWsClient getApimWsClient() {
        return apimWsClient;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ((ApiaWsTemplate) apiaWsClient).afterPropertiesSet();
        ((ApimWsTemplate) apimWsClient).afterPropertiesSet();
    }
}
