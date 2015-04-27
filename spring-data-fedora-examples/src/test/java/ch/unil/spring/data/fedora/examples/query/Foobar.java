package ch.unil.spring.data.fedora.examples.query;

import ch.unil.spring.data.fedora.core.mapping.annotation.Datastream;
import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;

/**
 * @author gushakov
 */
@FedoraObject
@Datastream(namespace = "http://my.unil.ch/schema/test")
public class Foobar {

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