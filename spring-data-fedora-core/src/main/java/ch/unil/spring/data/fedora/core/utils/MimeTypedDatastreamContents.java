package ch.unil.spring.data.fedora.core.utils;

import ch.unil.spring.data.fedora.ws.api.jaxb.MIMETypedStream;
import org.springframework.util.Assert;

/**
 * @author gushakov
 */
public class MimeTypedDatastreamContents implements DatastreamContents {

    private MIMETypedStream mimeTypedStream;

    public MimeTypedDatastreamContents(MIMETypedStream mimeTypedStream) {
        Assert.notNull(mimeTypedStream);
        this.mimeTypedStream = mimeTypedStream;
    }

    public byte[] getContents() {
        return mimeTypedStream.getStream();
    }

    public String getMimetype() {
        return mimeTypedStream.getMIMEType();
    }
}
