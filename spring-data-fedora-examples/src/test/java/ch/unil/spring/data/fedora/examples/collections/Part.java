package ch.unil.spring.data.fedora.examples.collections;

import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;

/**
 * @author gushakov
 */
@FedoraObject
public class Part {

    @Pid
    private int id;

    private String name;

    public Part(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
