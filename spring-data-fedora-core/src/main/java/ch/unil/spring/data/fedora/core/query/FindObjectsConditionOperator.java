package ch.unil.spring.data.fedora.core.query;

/**
 * @author gushakov
 */
public enum FindObjectsConditionOperator {
    Has("has"), EqualsTo("eq");

    private String operator;

    FindObjectsConditionOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator;
    }
}
