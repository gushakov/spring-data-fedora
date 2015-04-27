package ch.unil.spring.data.fedora.ws.api;

import ch.unil.spring.data.fedora.config.WsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.core.utils.Foxml11Document;
import ch.unil.spring.data.fedora.ws.api.jaxb.*;
import org.fcrepo.common.xml.format.FOXML1_1Format;
import org.fcrepo.utilities.DateUtility;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.WebServiceMessageSender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author gushakov
 */
public class ApimWsTemplate implements ApimWsClient, InitializingBean {

    private ObjectFactory jaxbFactory;

    private WebServiceTemplate wsTemplate;

    public ApimWsTemplate(String fedoraServerUrl, ObjectFactory jaxbFactory, Jaxb2Marshaller marshaller,
                          WebServiceMessageSender messageSender) {
        this.jaxbFactory = jaxbFactory;
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMessageSender(messageSender);
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        template.setDefaultUri(fedoraServerUrl + WsFedoraConnectionFactory.DEFAULT_APIM_SERVICE_CONTEXT);
        this.wsTemplate = template;
    }

    @Override
    public String ingest(Foxml11Document foxmlDoc, String comment) {
        Assert.notNull(foxmlDoc);
        Assert.hasText(comment);
        Ingest req = jaxbFactory.createIngest();
        req.setFormat(FOXML1_1Format.getInstance().toString());
        req.setLogMessage(comment);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            foxmlDoc.serialize(os);
            os.flush();
            req.setObjectXML(os.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        IngestResponse res = (IngestResponse) wsTemplate.marshalSendAndReceive(req);
        return res.getObjectPID();
    }

    @Override
    public boolean purge(String pid, String comment) {
        Assert.hasText(pid);
        PurgeObject req = jaxbFactory.createPurgeObject();
        req.setPid(pid);
        req.setLogMessage(comment);
        PurgeObjectResponse res = (PurgeObjectResponse) wsTemplate.marshalSendAndReceive(req);
        return DateUtility.convertStringToDate(res.getPurgedDate()).getTime() > 0L;
    }

    @Override
    public void save(byte[] xmlContent, String pid, String dsId) {
        Assert.isTrue(xmlContent != null && xmlContent.length > 0);
        Assert.hasText(pid);
        Assert.hasText(dsId);
        ModifyDatastreamByValue req = jaxbFactory.createModifyDatastreamByValue();
        req.setPid(pid);
        req.setDsID(dsId);
        req.setDsContent(xmlContent);
        ModifyDatastreamByValueResponse res = (ModifyDatastreamByValueResponse) wsTemplate.marshalSendAndReceive(req);
        System.out.println(res.getModifiedDate());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(wsTemplate);
        wsTemplate.afterPropertiesSet();
    }
}
