package ch.unil.spring.data.fedora.config;

import org.springframework.util.Assert;

/**
 * @author gushakov
 */
public class DefaultWsFedoraConnectionFactory implements WsFedoraConnectionFactory {

    private String fedoraServerUrl;
    private String fedoraUser;
    private String fedoraPassword;

    public DefaultWsFedoraConnectionFactory(String fedoraServerUrl, String fedoraUser, String fedoraPassword) {
        Assert.hasText(fedoraServerUrl);
        Assert.hasText(fedoraUser);
        Assert.hasText(fedoraPassword);
        this.fedoraServerUrl = fedoraServerUrl;
        this.fedoraUser = fedoraUser;
        this.fedoraPassword = fedoraPassword;
    }

    @Override
    public WsFedoraConnection newConnection() {
        return new DefaultWsFedoraConnection(fedoraServerUrl, fedoraUser, fedoraPassword);
    }
}
