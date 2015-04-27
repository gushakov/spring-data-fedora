package ch.unil.spring.data.fedora.ws.api;

import ch.unil.spring.data.fedora.config.DefaultWsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.config.WsFedoraConnection;
import ch.unil.spring.data.fedora.core.convert.FedoraMappingConverter;
import ch.unil.spring.data.fedora.core.mapping.DocumentFedoraPersistentEntity;
import ch.unil.spring.data.fedora.core.mapping.FedoraMappingContext;
import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author gushakov
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApimWsClientTest.TestConfig.class})
public class ApimWsClientTest {

    @Configuration
    @PropertySource("classpath:fedora.properties")
    public static class TestConfig {

        @Autowired
        private Environment env;

        @Bean
        public WsFedoraConnection wsConnection() {
            return new DefaultWsFedoraConnectionFactory(env.getProperty("fedora.server.url"),
                    env.getProperty("fedora.username"), env.getProperty("fedora.password")).newConnection();
        }


        @Bean
        public ApimWsClient apimWsClient() {
            return wsConnection().getApimWsClient();
        }

    }

    @Autowired
    private WsFedoraConnection wsConnection;

    @Autowired
    private ApimWsClient apimWsClient;

    @Before
    public void setUp() throws Exception {
        try {
            apimWsClient.purge("test:person_1", "test");
        } catch (Exception e) {
            // ignore
        }
    }

    @FedoraObject
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class Person {

        @Pid
        int id = 1;

        String name = "foo";

    }

    @Test
    public void testSave() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(new HashSet<>(Arrays.asList(Person.class)));
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(wsConnection, context);

        // ingest initial
        Person person = new Person();
        Foxml11Document foxmlDoc = converter.write(person);
        apimWsClient.ingest(foxmlDoc, "test");

        // modify and save
        person.name = "bar";
        foxmlDoc = converter.write(person);
        DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) context.getPersistentEntity(Person.class);
        apimWsClient.save(foxmlDoc.getXmlContent(entity.getDsvId()).getBytes(), converter.getPid(person), entity.getDsId());
    }

}
