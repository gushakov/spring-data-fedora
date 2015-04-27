package ch.unil.spring.data.fedora.examples.basic;

import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObjectRef;

/**
 * @author gushakov
 */
public class Contact {

    private String email;

    @FedoraObjectRef
    private Address address;

    public Contact() {
    }

    public Contact(String email, Address address) {
        this.email = email;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
