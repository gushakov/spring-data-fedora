package ch.unil.spring.data.fedora.core.mapping;

import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;
import ch.unil.spring.data.fedora.core.mapping.annotation.RelsExt;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author gushakov
 */
public class FedoraMappingContextTest {

    @FedoraObject
    static class Person {

        @Pid
        long id;

        String name;

        Contact contact;
    }

    static class Contact {
        String email;
    }

    @Test
    public void testSimple() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(Collections.singleton(Person.class));
        context.initialize();
        DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) context.getPersistentEntity(Person.class);
        assertThat(entity, is(notNullValue()));
        assertThat(entity.getFoxmlLabel(), is("person"));
        assertThat(entity.getDsId(), is("PERSON"));
        assertThat(entity.getDsvId(), is("PERSON.0"));
        PidFedoraPersistentProperty pidProp = entity.getPidProperty();
        assertThat(pidProp, is(notNullValue()));
        assertThat(pidProp.isTransient(), is(false));
        String pid = pidProp.getPidCreator().getPid(1L, entity);
        assertThat(pid, is("test:person_1"));
        assertThat(pidProp.getPidCreator().isValidPid("test:person_1", entity), is(true));
        assertThat(pidProp.getPidCreator().isValidPid("test:address_1", entity), is(false));
        GenericFedoraPersistentProperty contactProp = (GenericFedoraPersistentProperty) entity.getPersistentProperty("contact");
        assertThat(contactProp, is(notNullValue()));
        assertThat(context.getPersistentEntity(Contact.class), is(notNullValue()));
    }

    @FedoraObject
    static class InvalidPid {
        @Pid
        final String id = "0123456789-0123456789-0123456789-0123456789-0123456789-0123456789-0123456789";
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidPid() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(Collections.singleton(InvalidPid.class));
        context.initialize();
        DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) context.getPersistentEntity(InvalidPid.class);
        entity.getPidProperty().getPidCreator().getPid(new InvalidPid().id, entity);
    }

    @FedoraObject
    public static class PersonWithAddress {

        @Pid
        @XmlAttribute
        private long id;

        @RelsExt
        private Address address;

        @Transient
        private String foobar;

    }

    @FedoraObject
    public static class Address {

        @Pid
        private long id;

        private String email;

    }

    //TODO: test
    @Test
    @Ignore
    public void testRelsExtIsReference() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(Collections.singleton(PersonWithAddress.class));
        context.initialize();
        FedoraPersistentEntity<?> entity = context.getPersistentEntity(PersonWithAddress.class);
        FedoraPersistentProperty assocProp = entity.getPersistentProperty("address");
        assertThat(assocProp.isAssociation(), is(true));
    }

}
