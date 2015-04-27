package ch.unil.spring.data.fedora.examples.basic;

import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;

/**
 * @author gushakov
 */
@FedoraObject
public class Address {

    @Pid
    private int id;

    private String streetAndNumber;

    private String zipCode;

    public Address() {
    }

    public Address(int id, String streetAndNumber, String zipCode) {
        this.id = id;
        this.streetAndNumber = streetAndNumber;
        this.zipCode = zipCode;
    }

    public int getId() {
        return id;
    }

    public String getStreetAndNumber() {
        return streetAndNumber;
    }

    public void setStreetAndNumber(String streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
