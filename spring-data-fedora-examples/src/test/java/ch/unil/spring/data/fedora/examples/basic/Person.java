package ch.unil.spring.data.fedora.examples.basic;

import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;

/**
 * @author gushakov
 */
@FedoraObject
public class Person {

    @Pid
    private int id;

    private String name;

    private Contact contact;

    private Address contactAddress;

    public Person() {
    }

    public Person(int id, String name, Contact contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
