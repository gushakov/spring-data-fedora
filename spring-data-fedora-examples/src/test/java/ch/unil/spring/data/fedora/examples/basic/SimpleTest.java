package ch.unil.spring.data.fedora.examples.basic;

import ch.unil.spring.data.fedora.core.FedoraTemplate;
import ch.unil.spring.data.fedora.examples.config.FedoraConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author gushakov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FedoraConfig.class})
public class SimpleTest {

    @Autowired
    private FedoraTemplate fedoraTemplate;

    @Before
    public void setUp() throws Exception {
        fedoraTemplate.purge("test:foobar_1", "logging purge");
    }

    @Test
    public void testIngest() throws Exception {
        fedoraTemplate.ingest(new Foobar(), "logging ingest");
    }


}
