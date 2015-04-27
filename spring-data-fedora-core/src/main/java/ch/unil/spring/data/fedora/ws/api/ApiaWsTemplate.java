package ch.unil.spring.data.fedora.ws.api;

import ch.unil.spring.data.fedora.config.WsFedoraConnectionFactory;
import ch.unil.spring.data.fedora.core.query.FindObjectsField;
import ch.unil.spring.data.fedora.core.utils.DatastreamContents;
import ch.unil.spring.data.fedora.core.utils.DefaultFindObjectsResults;
import ch.unil.spring.data.fedora.core.utils.FindObjectsResults;
import ch.unil.spring.data.fedora.core.utils.MimeTypedDatastreamContents;
import ch.unil.spring.data.fedora.ws.api.jaxb.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.WebServiceMessageSender;

import java.math.BigInteger;

/**
 * @author gushakov
 */
public class ApiaWsTemplate implements ApiaWsClient, InitializingBean {

    private ObjectFactory jaxbFactory;

    private WebServiceTemplate wsTemplate;

    public ApiaWsTemplate(String fedoraServerUrl, ObjectFactory jaxbFactory, Jaxb2Marshaller marshaller,
                          WebServiceMessageSender messageSender) {
        this.jaxbFactory = new ObjectFactory();
        this.jaxbFactory = jaxbFactory;
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMessageSender(messageSender);
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        template.setDefaultUri(fedoraServerUrl + WsFedoraConnectionFactory.DEFAULT_APIA_SERVICE_CONTEXT);
        this.wsTemplate = template;
    }

    @Override
    public boolean exists(String pid) {
        Assert.hasText(pid);
        GetObjectProfile req = jaxbFactory.createGetObjectProfile();
        req.setPid(pid);
        GetObjectProfileResponse res = (GetObjectProfileResponse) wsTemplate.marshalSendAndReceive(req);
        return res.getObjectProfile().getPid().equals(pid);
    }

    @Override
    public DatastreamContents getDatastreamContents(String pid, String dsId) {
        Assert.hasText(pid);
        Assert.hasText(dsId);
        GetDatastreamDissemination req = jaxbFactory.createGetDatastreamDissemination();
        req.setPid(pid);
        req.setDsID(dsId);
        GetDatastreamDisseminationResponse res = (GetDatastreamDisseminationResponse) wsTemplate.marshalSendAndReceive(req);
        return new MimeTypedDatastreamContents(res.getDissemination());
    }

    @Override
    public FindObjectsResults findObjects(String field, String operator, String phrase, long maxResults) {
        Assert.hasText(field);
        Assert.hasText(operator);
        Assert.hasText(phrase);
        FindObjects req = jaxbFactory.createFindObjects();
        ArrayOfString resultFields = jaxbFactory.createArrayOfString();
        resultFields.getItem().add(FindObjectsField.Pid.getField());
        req.setResultFields(resultFields);
        req.setMaxResults(BigInteger.valueOf(maxResults));
        FieldSearchQuery.Conditions conditions = jaxbFactory.createFieldSearchQueryConditions();
        Condition condition = jaxbFactory.createCondition();
        condition.setProperty(field);
        condition.setOperator(ComparisonOperator.fromValue(operator));
        condition.setValue(phrase);
        conditions.getCondition().add(condition);
        FieldSearchQuery query = jaxbFactory.createFieldSearchQuery();
        query.setConditions(jaxbFactory.createFieldSearchQueryConditions(conditions));
        req.setQuery(query);
        FindObjectsResponse res = (FindObjectsResponse) wsTemplate.marshalSendAndReceive(req);
        return new DefaultFindObjectsResults(res.getResult());
    }

    @Override
    public FindObjectsResults resumeFindObjects(String sessionToken) {
        Assert.hasText(sessionToken);
        ResumeFindObjects req = jaxbFactory.createResumeFindObjects();
        req.setSessionToken(sessionToken);
        ResumeFindObjectsResponse res = (ResumeFindObjectsResponse) wsTemplate.marshalSendAndReceive(req);
        return new DefaultFindObjectsResults(res.getResult());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(wsTemplate);
        wsTemplate.afterPropertiesSet();
    }
}
