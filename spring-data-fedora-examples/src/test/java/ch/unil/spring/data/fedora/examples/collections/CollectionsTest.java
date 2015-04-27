package ch.unil.spring.data.fedora.examples.collections;

import ch.unil.spring.data.fedora.core.FedoraTemplate;
import ch.unil.spring.data.fedora.examples.config.FedoraConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author gushakov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FedoraConfig.class})

public class CollectionsTest {

    @Autowired
    private FedoraTemplate fedoraTemplate;

    @Before
    public void setUp() throws Exception {
        fedoraTemplate.purge("test:car_1", "test");
        fedoraTemplate.purge("test:part_1", "test");
        fedoraTemplate.purge("test:part_2", "test");
    }

    @Test
    public void testCollections() throws Exception {
        fedoraTemplate.ingest(new Car(1, "car",
                new ArrayList<>(Arrays.asList(new Part(1, "part1"),
                        new Part(2, "part2")))), "test");
    }
}
