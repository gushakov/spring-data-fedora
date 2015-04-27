package ch.unil.spring.data.fedora.core.convert;

import ch.unil.spring.data.fedora.core.mapping.FedoraMappingContext;
import ch.unil.spring.data.fedora.core.mapping.annotation.*;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import ch.unil.spring.data.fedora.core.utils.XmlUtils;
import ch.unil.spring.data.fedora.utils.TestUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author gushakov
 */
public class FedoraMappingConverterTest {

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
    public void testPrepareFoxmlForWrite() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(new HashSet<>(Arrays.asList(Person.class)));
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        Person person = new Person();
        person.id = 1L;
        Foxml11Document foxmlDoc = converter.prepareFoxmlDocForWrite(person);
        assertThat(foxmlDoc, is(notNullValue()));
        foxmlDoc.serialize(System.out);

        assertThat(TestUtils.getDefaultXpathEngine()
                        .evaluate("/foxml:digitalObject/@PID",
                                XMLUnit.buildTestDocument(new String(XmlUtils.serialize(foxmlDoc)))),
                is("test:person_1"));
    }

    @Test
    public void testPrepareFoxmlForRead() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(new HashSet<>(Arrays.asList(Person.class)));
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        Foxml11Document foxmlDoc = converter.prepareFoxmlDocForRead("test:person_1", Person.class);
        assertThat(foxmlDoc, is(notNullValue()));
        foxmlDoc.serialize(System.out);
    }

    @Test
    public void testWriteFoxml() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(new HashSet<>(Arrays.asList(Person.class)));
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        Person person = new Person();
        person.id = 1L;
        person.name = "George";
        Contact contact = new Contact();
        contact.email = "foo.bar@wam.baz";
        person.contact = contact;
        Foxml11Document foxmlDoc = converter.prepareFoxmlDocForWrite(person);
        assertThat(foxmlDoc, is(notNullValue()));
        converter.write(person, foxmlDoc);
        foxmlDoc.serialize(System.out);
    }

    @Test
    public void testReadFoxml() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(new HashSet<>(Arrays.asList(Person.class)));
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        Person person1 = new Person();
        person1.id = 1L;
        person1.name = "George";
        Contact contact = new Contact();
        contact.email = "foo.bar@wam.baz";
        person1.contact = contact;
        Foxml11Document foxmlDoc = converter.prepareFoxmlDocForWrite(person1);
        converter.write(person1, foxmlDoc);
        Person person2 = converter.read(Person.class, foxmlDoc);
        assertThat(person2, is(notNullValue()));
        assertThat(person2.id, equalTo(person1.id));
        assertThat(person2.name, equalTo(person1.name));
        assertThat(person2.contact.email, equalTo(person1.contact.email));
    }

    @FedoraObject
    public static class PersonWithAddress {

        public PersonWithAddress() {
        }

        public PersonWithAddress(long id) {
            this.id = id;
        }

        @Pid
        long id;

        @FedoraObjectRef
        Address address;

        @FedoraObjectRef
        List<Address> listOfAddresses;

    }

    @FedoraObject
    public static class Address {

        public Address() {
        }

        public Address(long id, String email) {
            this.id = id;
            this.email = email;
        }

        @Pid
        long id;

        String email;

        public String getEmail() {
            return email;
        }

    }

    @Test
    public void testWriteWithFedoraObjectRefs() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.setInitialEntitySet(new HashSet<>(Arrays.asList(PersonWithAddress.class)));
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        PersonWithAddress person = new PersonWithAddress(1L);
        person.address = new Address(1L, "foo");
        person.listOfAddresses = new ArrayList<>(Arrays.asList(
                new Address(2L, "bar"),
                new Address(3L, "waz")));
        Foxml11Document foxmlDoc = converter.prepareFoxmlDocForWrite(person);
        converter.write(person, foxmlDoc);
        foxmlDoc.serialize(System.out);
        assertThat(TestUtils.getDefaultXpathEngine()
                        .evaluate("//foxml:datastream[@ID='PERSONWITHADDRESS']//address/@uri",
                                XMLUnit.buildTestDocument(new String(XmlUtils.serialize(foxmlDoc)))),
                is("info:fedora/test:address_1"));
        assertThat(TestUtils.getDefaultXpathEngine()
                        .getMatchingNodes("//foxml:xmlContent//fedora:hasMember/@rdf:resource[. = 'info:fedora/test:address_1']",
                                XMLUnit.buildTestDocument(new String(XmlUtils.serialize(foxmlDoc)))).getLength(),
                is(1));
        assertThat(TestUtils.getDefaultXpathEngine()
                        .getMatchingNodes("//foxml:xmlContent//fedora:hasMember/@rdf:resource[. = 'info:fedora/test:address_2']",
                                XMLUnit.buildTestDocument(new String(XmlUtils.serialize(foxmlDoc)))).getLength(),
                is(1));
        assertThat(TestUtils.getDefaultXpathEngine()
                        .getMatchingNodes("//foxml:xmlContent//fedora:hasMember/@rdf:resource[. = 'info:fedora/test:address_3']",
                                XMLUnit.buildTestDocument(new String(XmlUtils.serialize(foxmlDoc)))).getLength(),
                is(1));
    }

    @FedoraObject
    public static class Book {
        @Pid
        int id;
        String title;
        @FedoraObjectRef
        Author author;

        public Book() {
        }

        public Book(int id, String title, Author author) {
            this.id = id;
            this.title = title;
            this.author = author;
        }

        public Book(int id, String title, Author author, List<Reference> references) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.references = references;
        }

        List<Reference> references;

        public String getTitle() {
            return title;
        }

        public Author getAuthor() {
            return author;
        }

        public List<Reference> getReferences() {
            return references;
        }
    }

    @FedoraObject
    public static class Author {
        @Pid
        int id;
        String name;

        public Author() {
        }

        public Author(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public static class Reference {

        @FedoraObjectRef
        Book book;

        public Reference() {
        }

        public Reference(Book book) {
            this.book = book;
        }

        public Book getBook() {
            return book;
        }
    }

    @Test
    public void testWriteWithNestedFedoraObjectRefs() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        Book book = new Book(1, "book1", new Author(1, "author1"),
                new ArrayList<>(Arrays.asList(new Reference(new Book(2, "book2", new Author(2, "author2"))),
                        new Reference(new Book(3, "book3", new Author(3, "author3"))))));
        Foxml11Document foxmlDoc = converter.write(book);
        foxmlDoc.serialize(System.out);
    }

    @FedoraObject
    public static class Car {
        @Pid
        int id = 1;
        Wheel[] wheels = {new Wheel("front-left"),
                new Wheel("front-right"),
                new Wheel("back-left"),
                new Wheel("back-right")};
    }

    public static class Wheel {

        String position;

        public Wheel(String position) {
            this.position = position;
        }
    }

    @Test
    public void testArrays() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        Car car = new Car();
        Foxml11Document foxmlDoc = converter.write(car);
        foxmlDoc.serialize(System.out);
    }

    @FedoraObject
    public static class Vehicle {
        @Pid
        int id = 1;
        Map<Integer, Part> parts;

        Map<Integer, String> simpleMap;

        public Vehicle() {
            parts = new HashMap<>();
            parts.put(1, new Part("engine"));
            parts.put(2, new Part("seat"));
            parts.put(3, new Part("wheel"));

            simpleMap = new HashMap<>();
            simpleMap.put(1, "one");
            simpleMap.put(2, "two");
        }
    }

    public static class Part {

        String name;

        public Part(String name) {
            this.name = name;
        }
    }

    @Test
    public void testMaps() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        Vehicle vehicle = new Vehicle();
        Foxml11Document foxmlDoc = converter.write(vehicle);
        foxmlDoc.serialize(System.out);
    }


    @FedoraObject
    @Datastream(namespace = "uri:auto")
    public static class Automobile {

        @Pid
        long id;

        @DatastreamRef
        Interior interior;

        public Automobile(long id, Interior interior) {
            this.id = id;
            this.interior = interior;
        }

        public long getId() {
            return id;
        }

        public Interior getInterior() {
            return interior;
        }

    }

    @Datastream(namespace = "uri:auto#interior")
    public static class Interior {

        Seat[] seats;

        public Interior(Seat[] seats) {
            this.seats = seats;
        }

        public Seat[] getSeats() {
            return seats;
        }
    }

    public static class Seat {
        String material;

        public Seat(String material) {
            this.material = material;
        }

        public String getMaterial() {
            return material;
        }
    }

    @Test
    public void testEmbeddedDatastream() throws Exception {
        FedoraMappingContext context = new FedoraMappingContext();
        context.initialize();
        FedoraMappingConverter converter = new FedoraMappingConverter(null, context);
        Automobile auto = new Automobile(1L, new Interior(new Seat[]{new Seat("seat1"), new Seat("seat2")}));
        Foxml11Document foxmlDoc = converter.write(auto);
        foxmlDoc.serialize(System.out);
    }

}
