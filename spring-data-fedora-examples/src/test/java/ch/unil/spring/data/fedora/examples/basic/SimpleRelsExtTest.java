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
public class SimpleRelsExtTest {

    @Autowired
    private FedoraTemplate fedoraTemplate;

    @Before
    public void setUp() throws Exception {
        fedoraTemplate.purge("test:person_1", "test");
        fedoraTemplate.purge("test:address_1", "test");
    }

    @Test
    public void testIngestPersonWithAddressAsRelsExt() throws Exception {
        Person person = makePerson();
        fedoraTemplate.ingest(person, "test");
        fedoraTemplate.ingest(person.getContact().getAddress(), "test");
    }

    private Person makePerson() {
        Address address = new Address(1, "Main St. 123", "12345");
        Contact contact = new Contact("foo.bar@waz.baz", address);
        return new Person(1, "Foo Bar", contact);
    }

}
