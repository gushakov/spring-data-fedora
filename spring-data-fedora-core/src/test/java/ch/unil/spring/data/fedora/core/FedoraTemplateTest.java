package ch.unil.spring.data.fedora.core;

import ch.unil.spring.data.fedora.config.DefaultWsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.config.WsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.core.mapping.*;
import ch.unil.spring.data.fedora.core.mapping.annotation.*;
import ch.unil.spring.data.fedora.core.query.*;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;


/**
 * @author gushakov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FedoraTemplateTest.TestConfig.class})
public class FedoraTemplateTest {

    @Configuration
    @PropertySource("classpath:fedora.properties")
    public static class TestConfig {

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

    @Autowired
    private FedoraTemplate fedoraTemplate;

    @Before
    public void setUp() throws Exception {
        fedoraTemplate.purge("test:person_1", "test");
        fedoraTemplate.purge("test:personwithcontact_1", "test");
        fedoraTemplate.purge("test:address_1", "test");
        fedoraTemplate.purge("test:foobar_1", "test");
        fedoraTemplate.purge("test:foobar_2", "test");
        fedoraTemplate.purge("test:foobar_3", "test");
        fedoraTemplate.purge("test:foobar_4", "test");
        fedoraTemplate.purge("test:foobar_5", "test");
        fedoraTemplate.purge("test:personwithmultipleaddresses_1", "test");
        fedoraTemplate.purge("test:book_1", "test");
        fedoraTemplate.purge("test:author_1", "test");
        fedoraTemplate.purge("test:author_2", "test");
        fedoraTemplate.purge("test:car_1", "test");
        fedoraTemplate.purge("test:dashpanel_1", "test");
        fedoraTemplate.purge("test:automobile_1", "test");

    }

    @FedoraObject
    static class Person {
        @Pid
        int id = 1;

        String name = "George";
    }

    @Test
    public void testIngest() throws Exception {
        String pid = fedoraTemplate.ingest(new Person(), "test");
        assertThat(pid, is("test:person_1"));
    }

    @Test
    public void testLoad() throws Exception {
        fedoraTemplate.ingest(new Person(), "test");
        Person person = fedoraTemplate.load(1, Person.class);
        assertThat(person, is(notNullValue()));
        assertThat(person.id, is(1));
    }

    @Test
    public void testExists() throws Exception {
        assertThat(fedoraTemplate.exists("test:object_00000001"), is(true));
        assertThat(fedoraTemplate.exists("test:object_0000000x"), is(false));
    }

    @FedoraObject
    public static class PersonWithContact {
        @Pid
        private final int id = 1;

        private Contact contact;

        public Contact getContact() {
            return contact;
        }

        public void setContact(Contact contact) {
            this.contact = contact;
        }

        private Address relsExtAddress;

    }

    public static class Contact {

        @FedoraObjectRef(lazyLoad = true)
        private Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    @FedoraObject
    public static class Address {

        @Pid
        private int id = 1;

        private String email;

        public Address() {
        }

        public Address(int id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

    @Test
    public void testFedoraObjectReference() throws Exception {
        PersonWithContact person = new PersonWithContact();
        Contact contact = new Contact();
        Address address = new Address();
        address.setEmail("foo@bar");
        contact.setAddress(address);
        person.setContact(contact);

        fedoraTemplate.ingest(person, "test");
        fedoraTemplate.ingest(person.getContact().getAddress(), "test");

        PersonWithContact readPerson = fedoraTemplate.load(person.id, PersonWithContact.class);
        assertThat(readPerson, is(notNullValue()));

        Address readAddress = readPerson.getContact().getAddress();
        assertThat(readAddress.getEmail(), is("foo@bar"));
        readAddress.setEmail("wam@baz");
        fedoraTemplate.save(readAddress);
    }

    @FedoraObject
    @Datastream(namespace = "http://my.unil.ch/schema/test")
    public static class Foobar {

        @Pid
        private long id;

        public Foobar() {
        }

        public Foobar(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }

    @Test
    public void testSinglePageQuery() throws Exception {
        fedoraTemplate.ingest(new Foobar(1L), "test");

        FindObjectsQuery query = fedoraTemplate.getQueryBuilder()
                .withField(FindObjectsField.Pid)
                .withOperator(FindObjectsConditionOperator.Has)
                .withPhrase("*", Foobar.class)
                .build();

        assertThat(query.getQueryString(), is("pid has test:foobar_*"));
        Page<Foobar> page = fedoraTemplate.query(query, Foobar.class, DefaultFindObjectsPageRequest.firstPage(10));
        assertThat(page, is(notNullValue()));
        assertThat(page, is(not(instanceOf(FindObjectsPage.class))));
        assertThat(page.getTotalElements(), is(1L));
        assertThat(page.getTotalPages(), is(1));
        assertThat(page.isFirst(), is(true));
        assertThat(page.isLast(), is(true));
        assertThat(page.hasNext(), is(false));
        assertThat(page, hasItem(Matchers.<Foobar>hasProperty("id", equalTo(1L))));
    }


    public static class NewsPidCreator extends AbstractPidCreator {
        @Override
        protected String getEntityName(DatastreamFedoraPersistentEntity<?> entity) {
            return "news";
        }
    }

    public static class NewsDsIdCreator implements DsIdCreator {
        @Override
        public String getDsId(DefaultDatastreamFedoraPersistentEntity<?> dsProp) {
            return "DATA";
        }
    }

    @Test
    public void testMultiplePagesQuery() throws Exception {
        fedoraTemplate.ingest(new Foobar(1L), "test");
        fedoraTemplate.ingest(new Foobar(2L), "test");
        fedoraTemplate.ingest(new Foobar(3L), "test");
        fedoraTemplate.ingest(new Foobar(4L), "test");
        fedoraTemplate.ingest(new Foobar(5L), "test");

        FindObjectsQuery query = fedoraTemplate.getQueryBuilder()
                .withField(FindObjectsField.Pid)
                .withOperator(FindObjectsConditionOperator.Has)
                .withPhrase("*", Foobar.class)
                .build();

        assertThat(query.getQueryString(), is("pid has test:foobar_*"));

        // first page
        Page<Foobar> page = fedoraTemplate.query(query, Foobar.class, DefaultFindObjectsPageRequest.firstPage(2));
        assertThat(page, is(notNullValue()));
        assertThat(page, is(instanceOf(FindObjectsPage.class)));
        assertThat(page.isFirst(), is(true));
        assertThat(page.isLast(), is(false));
        assertThat(page.hasNext(), is(true));

        // second page
        page = fedoraTemplate.query(query, Foobar.class, (FindObjectsPageRequest) page.nextPageable());
        assertThat(page, is(notNullValue()));
        assertThat(page, is(instanceOf(FindObjectsPage.class)));
        assertThat(page.isFirst(), is(false));
        assertThat(page.isLast(), is(false));
        assertThat(page.hasNext(), is(true));

        // third page
        page = fedoraTemplate.query(query, Foobar.class, (FindObjectsPageRequest) page.nextPageable());
        assertThat(page, is(notNullValue()));
        assertThat(page, is(instanceOf(FindObjectsPage.class)));
        assertThat(page.isFirst(), is(false));
        assertThat(page.isLast(), is(true));
        assertThat(page.hasNext(), is(false));
    }

    @FedoraObject
    @Datastream(namespace = "http://my.unil.ch/schema/test")
    static class PersonWithMultipleAddresses {
        @Pid
        private int id = 1;

        @RelsExt
        private List<RelsExtReference<?>> addresses;
    }

    @Test
    public void testCollectionRelsExt() throws Exception {
        PersonWithMultipleAddresses person = new PersonWithMultipleAddresses();
        RelsExtReference<Foobar> foobarRelsExt = new RelsExtReference<>(new Foobar(1L), Constants.Rels_Ext_Rdf_Property.HasMember);
        RelsExtReference<Address> addressRelsExt = new RelsExtReference<>(new Address(1), Constants.Rels_Ext_Rdf_Property.HasMember);
        person.addresses = Arrays.asList(foobarRelsExt, addressRelsExt);
        fedoraTemplate.ingest(person, "test");
    }


    @FedoraObject
    public static class Book {
        @Pid
        int id;
        String title;
        @FedoraObjectRef
        Author author;
    }

    @FedoraObject
    public static class Author {
        @Pid
        int id;
        String name;

        public String getName() {
            return name;
        }
    }

    @Test
    public void testAssociations2() throws Exception {
        Book book = new Book();
        book.id = 1;
        book.title = "A book";
        Author author = new Author();
        author.id = 1;
        author.name = "An author";
        book.author = author;

        fedoraTemplate.ingest(book, "test");
        fedoraTemplate.ingest(book.author, "test");

        Book readBook = fedoraTemplate.load(book.id, Book.class);
        assertThat(readBook, is(notNullValue()));
        assertThat(readBook.author.getName(), is("An author"));

        Author author2 = new Author();
        author2.id = 2;
        author2.name = "Another author";
        fedoraTemplate.ingest(author2, "test");

    }

    @FedoraObject
    public static class Car {
        @Pid
        final int i = 1;
        Interior interior = new Interior();
    }

    public static class Interior {
        @FedoraObjectRef
        final DashPanel dashPanel = new DashPanel();
    }

    @FedoraObject
    public static class DashPanel {
        @Pid
        final int i = 1;
    }

    @Test
    public void testAssociationWithIntermediateObject() throws Exception {
        Car car = new Car();
        fedoraTemplate.ingest(car, "test");
        fedoraTemplate.ingest(car.interior.dashPanel, "test");
        Car readCar = fedoraTemplate.load(1, Car.class);
        assertThat(readCar.interior.dashPanel, is(notNullValue()));
    }


    @FedoraObject
    @Datastream(namespace = "uri:auto")
    public static class Automobile {

        @Pid
        long id;

        //        @DatastreamRef(lazyLoad = false)
        @DatastreamRef(lazyLoad = true)
        Engine engine;

        public Automobile(long id, Engine engine) {
            this.id = id;
            this.engine = engine;
        }

        public long getId() {
            return id;
        }

        public Engine getEngine() {
            return engine;
        }
    }

    @Datastream(namespace = "uri:auto#engine")
    public static class Engine {

        Part[] parts;

        public Engine() {
        }

        public Engine(Part[] parts) {
            this.parts = parts;
        }

        public Part[] getParts() {
            return parts;
        }
    }

    public static class Part {
        String name;

        public Part(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void testIngestWithEmbeddedDatastreams() throws Exception {
        Automobile auto = new Automobile(1L, new Engine(new Part[]{new Part("ignition"), new Part("cylinder")}));
        fedoraTemplate.ingest(auto, "test");
    }

    //TODO: test
    @Test
    @Ignore
    public void testLoadWithEmbeddedDatastreams() throws Exception {
        fedoraTemplate.ingest(new Automobile(1L, new Engine(new Part[]{new Part("ignition"), new Part("cylinder")})), "test");
        Automobile auto = fedoraTemplate.load(1L, Automobile.class);
        assertThat(auto, is(notNullValue()));
        assertThat(auto.getEngine(), is(notNullValue()));
        assertThat(auto.getEngine().getParts(), is(notNullValue()));
        assertThat(auto.getEngine().getParts(), arrayWithSize(2));
        System.out.println(auto.getEngine().getParts()[0].getName());
    }

}
