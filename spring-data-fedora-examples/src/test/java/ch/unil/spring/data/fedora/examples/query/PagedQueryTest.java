package ch.unil.spring.data.fedora.examples.query;

import ch.unil.spring.data.fedora.core.FedoraTemplate;
import ch.unil.spring.data.fedora.core.query.*;
import ch.unil.spring.data.fedora.examples.config.FedoraConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author gushakov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FedoraConfig.class})
public class PagedQueryTest {

    @Autowired
    private FedoraTemplate fedoraTemplate;

    @Before
    public void setUp() throws Exception {
        fedoraTemplate.purge("test:foobar_1", "test");
        fedoraTemplate.purge("test:foobar_2", "test");
        fedoraTemplate.purge("test:foobar_3", "test");
        fedoraTemplate.purge("test:foobar_4", "test");
        fedoraTemplate.purge("test:foobar_5", "test");
    }

    @Test
    public void testQuery() throws Exception {
        fedoraTemplate.ingest(new Foobar(1L), "test");
        fedoraTemplate.ingest(new Foobar(2L), "test");
        fedoraTemplate.ingest(new Foobar(3L), "test");
        fedoraTemplate.ingest(new Foobar(4L), "test");
        fedoraTemplate.ingest(new Foobar(5L), "test");

        FindObjectsQuery query = fedoraTemplate.getQueryBuilder()
                .withField(FindObjectsField.Pid)
                .withOperator(FindObjectsConditionOperator.Has)
                .withPhrase("*", Foobar.class)
                .build();

        assertThat(query.getQueryString(), is("pid has test:foobar_*"));

        // first page
        Page<Foobar> page = fedoraTemplate.query(query, Foobar.class, DefaultFindObjectsPageRequest.firstPage(2));
        assertThat(page, is(notNullValue()));
        assertThat(page, is(instanceOf(FindObjectsPage.class)));
        assertThat(page.isFirst(), is(true));
        assertThat(page.isLast(), is(false));
        assertThat(page.hasNext(), is(true));

        // second page
        page = fedoraTemplate.query(query, Foobar.class, (FindObjectsPageRequest) page.nextPageable());
        assertThat(page, is(notNullValue()));
        assertThat(page, is(instanceOf(FindObjectsPage.class)));
        assertThat(page.isFirst(), is(false));
        assertThat(page.isLast(), is(false));
        assertThat(page.hasNext(), is(true));

        // third page
        page = fedoraTemplate.query(query, Foobar.class, (FindObjectsPageRequest) page.nextPageable());
        assertThat(page, is(notNullValue()));
        assertThat(page, is(instanceOf(FindObjectsPage.class)));
        assertThat(page.isFirst(), is(false));
        assertThat(page.isLast(), is(true));
        assertThat(page.hasNext(), is(false));
    }
}
