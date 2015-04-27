package ch.unil.spring.data.fedora.core.query;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author gushakov
 */
public class DefaultFindObjectsPageRequest extends AbstractPageRequest implements FindObjectsPageRequest {

    public static DefaultFindObjectsPageRequest firstPage(int pageSize) {
        return new DefaultFindObjectsPageRequest(0, pageSize, FindObjectsPageRequest.NO_CURSOR, null);
    }

    private long cursor;

    private String sessionToken;

    public DefaultFindObjectsPageRequest(int page, int size, long cursor, String sessionToken) {
        super(page, size);
        this.cursor = cursor;
        this.sessionToken = sessionToken;
    }

    @Override
    public Sort getSort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable next() {
        return new DefaultFindObjectsPageRequest(getPageNumber() + 1, getPageSize(), cursor + getPageSize(), sessionToken);
    }

    @Override
    public Pageable previous() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable first() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public long getCursor() {
        return cursor;
    }

}
