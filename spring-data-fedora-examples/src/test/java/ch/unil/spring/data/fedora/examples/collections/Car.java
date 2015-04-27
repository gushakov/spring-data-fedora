package ch.unil.spring.data.fedora.examples.collections;

import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;

import java.util.List;

/**
 * @author gushakov
 */
@FedoraObject
public class Car {

    @Pid
    private int id;

    private String make;

    private List<Part> parts;

    public Car(int id, String make, List<Part> parts) {
        this.id = id;
        this.make = make;
        this.parts = parts;
    }
}
