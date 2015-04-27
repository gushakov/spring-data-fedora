package ch.unil.spring.data.fedora.ws.api;

import ch.unil.spring.data.fedora.config.DefaultWsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.core.query.FindObjectsConditionOperator;
import ch.unil.spring.data.fedora.core.query.FindObjectsField;
import ch.unil.spring.data.fedora.core.utils.DatastreamContents;
import ch.unil.spring.data.fedora.core.utils.FindObjectsResults;
import ch.unil.spring.data.fedora.utils.TestUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author gushakov
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApiaWsClientTest.TestConfig.class})
public class ApiaWsClientTest {

    @Configuration
    @PropertySource("classpath:fedora.properties")
    public static class TestConfig {
        @Autowired
        private Environment env;

        @Bean
        public ApiaWsClient apiaWsClient() {
            return new DefaultWsFedoraConnectionFactory(env.getProperty("fedora.server.url"),
                    env.getProperty("fedora.username"), env.getProperty("fedora.password")).newConnection().getApiaWsClient();
        }

    }

    @Autowired
    private ApiaWsClient apiaWsClient;

    @Test
    public void testExists() throws Exception {
        assertThat(apiaWsClient, is(notNullValue()));
        assertThat(apiaWsClient.exists("test:object_00000001"), is(true));
    }

    @Test
    public void testGetDatastreamContents() throws Exception {
        DatastreamContents dsContents = apiaWsClient.getDatastreamContents("test:object_00000001", "DC");
        assertThat(dsContents, is(notNullValue()));
        System.out.println(new String(dsContents.getContents()));
        assertThat(TestUtils.getDefaultXpathEngine()
                        .evaluate("/oai_dc:dc/dc:identifier",
                                XMLUnit.buildTestDocument(new String(dsContents.getContents()))),
                is("test:object_00000001"));
    }

    @Test
    public void testFindObjects() throws Exception {
        FindObjectsResults results = apiaWsClient.findObjects(FindObjectsField.Pid.getField(),
                FindObjectsConditionOperator.Has.getOperator(), "test:*", 10L);
        assertThat(results, is(notNullValue()));
        assertThat(results.getPids(), hasItem("test:object_00000001"));
    }

}
