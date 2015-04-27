package ch.unil.spring.data.fedora.core.query;

/**
 * @author gushakov
 */
public enum FindObjectsField {
    Pid("pid"), Title("title");


    private String field;

    FindObjectsField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    @Override
    public String toString() {
        return field;
    }
}
