package ch.unil.spring.data.fedora.core.query;

/**
 * @author gushakov
 */
public interface FindObjectsQuery {

    FindObjectsField getField();

    FindObjectsConditionOperator getOperator();

    String getPhrase();

    String getQueryString();
}
