package ch.unil.spring.data.fedora.core.query;

import ch.unil.spring.data.fedora.core.mapping.DocumentFedoraPersistentEntity;
import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentEntity;
import ch.unil.spring.data.fedora.core.mapping.FedoraPersistentProperty;
import ch.unil.spring.data.fedora.core.mapping.PidFedoraPersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.util.Assert;

/**
 * @author gushakov
 */
public class FindObjectsQueryBuilder {

    private MappingContext<? extends FedoraPersistentEntity<?>, FedoraPersistentProperty> context;

    private DefaultFindObjectsQuery query;

    public FindObjectsQueryBuilder(MappingContext<? extends FedoraPersistentEntity<?>, FedoraPersistentProperty> context) {
        this.query = new DefaultFindObjectsQuery();
        this.context = context;
    }

    class DefaultFindObjectsQuery implements FindObjectsQuery {

        private FindObjectsField field;

        private FindObjectsConditionOperator operator;

        private String phrase;

        @Override
        public FindObjectsField getField() {
            return field;
        }

        public void setField(FindObjectsField field) {
            this.field = field;
        }

        @Override
        public FindObjectsConditionOperator getOperator() {
            return operator;
        }

        public void setOperator(FindObjectsConditionOperator operator) {
            this.operator = operator;
        }

        @Override
        public String getPhrase() {
            return phrase;
        }

        @Override
        public String getQueryString() {
            return String.format("%s %s %s", field, operator, phrase);
        }

        public void setPhrase(String phrase) {
            this.phrase = phrase;
        }
    }

    public FindObjectsQueryBuilder withField(FindObjectsField field) {
        Assert.notNull(field);
        query.setField(field);
        return this;
    }

    public FindObjectsQueryBuilder withOperator(FindObjectsConditionOperator operator) {
        Assert.notNull(operator);
        query.setOperator(operator);
        return this;
    }

    public FindObjectsQueryBuilder withPhrase(String phrase) {
        Assert.hasText(phrase);
        query.setPhrase(phrase);
        return this;
    }

    public FindObjectsQueryBuilder withPhrase(String phrase, Class<?> type) {
        Assert.hasText(phrase);
        Assert.notNull(type);
        DocumentFedoraPersistentEntity<?> entity = (DocumentFedoraPersistentEntity<?>) context.getPersistentEntity(type);
        PidFedoraPersistentProperty pidProp = entity.getPidProperty();
        query.setPhrase(pidProp.getPidCreator().getPid(phrase, entity));
        return this;
    }

    public FindObjectsQuery build() {
        return query;
    }

}
