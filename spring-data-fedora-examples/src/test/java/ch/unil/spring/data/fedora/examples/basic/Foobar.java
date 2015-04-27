package ch.unil.spring.data.fedora.examples.basic;

import ch.unil.spring.data.fedora.core.mapping.annotation.FedoraObject;
import ch.unil.spring.data.fedora.core.mapping.annotation.Pid;

/**
 * @author gushakov
 */
@FedoraObject
public class Foobar {
    @Pid
    private long id = 1L;
    private String foo = "bar";
}
