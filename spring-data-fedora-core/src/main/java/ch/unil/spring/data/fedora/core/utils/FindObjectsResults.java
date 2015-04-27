package ch.unil.spring.data.fedora.core.utils;

import java.util.List;

/**
 * @author gushakov
 */
public interface FindObjectsResults {

    String getSessionToken();

    long getCursor();

    List<String> getPids();
}
