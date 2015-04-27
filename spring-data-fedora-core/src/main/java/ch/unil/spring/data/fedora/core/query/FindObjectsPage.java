package ch.unil.spring.data.fedora.core.query;

import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * @author gushakov
 */
public class FindObjectsPage<T> extends PageImpl<T> {
    private FindObjectsPageRequest pageable;

    public FindObjectsPage(List<T> content, FindObjectsPageRequest pageable) {
        super(content, pageable, Long.MAX_VALUE);
        this.pageable = pageable;
    }

    @Override
    public int getTotalPages() {
        throw new UnsupportedOperationException("Unavailable for FindObjects request");
    }

    @Override
    public long getTotalElements() {
        throw new UnsupportedOperationException("Unavailable for FindObjects request");
    }

    @Override
    public boolean hasNext() {
        return pageable.getSessionToken() != null;
    }

    @Override
    public boolean isLast() {
        return pageable.getSessionToken() == null;
    }

    @Override
    public String toString() {
        return "FindObjectsPage with " + getContent().size() + " elements";
    }
}
