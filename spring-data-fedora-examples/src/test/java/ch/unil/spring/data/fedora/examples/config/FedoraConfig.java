package ch.unil.spring.data.fedora.examples.config;

import ch.unil.spring.data.fedora.config.DefaultWsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.config.WsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.core.FedoraTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author gushakov
 */
@Configuration
@PropertySource("classpath:fedora.properties")
public class FedoraConfig {

    @Autowired
    private Environment env;

    @Bean
    public WsFedoraConnectionFactory connectionFactory() {
        return new DefaultWsFedoraConnectionFactory(env.getProperty("fedora.server.url"),
                env.getProperty("fedora.username"), env.getProperty("fedora.password"));
    }

    @Bean
    public FedoraTemplate fedoraTemplate() {
        return new FedoraTemplate(connectionFactory());
    }

}
