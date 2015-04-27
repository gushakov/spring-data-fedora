package ch.unil.spring.data.fedora.core.query;

import org.springframework.data.domain.Pageable;

/**
 * @author gushakov
 */
public interface FindObjectsPageRequest extends Pageable {

    public static final long NO_CURSOR = -1L;

    String getSessionToken();

    long getCursor();
}
