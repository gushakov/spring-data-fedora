## spring-data-fedora

associations

The purpose of this project is to extend Spring Data API for persisting and querying data from
[Fedora Commons Repository](https://wiki.duraspace.org/display/FEDORA37/Fedora+3.7+Documentation).

### Description.

The idea behind this integration is to use a small set of custom annotations to be added to domain objects which
allow generation of valid FOXML1.1 document to be stored in a Fedora repository.

Each object of a type annotated with `@FedoraObject` will be mapped to a FOXML1.1 document with one datastream of type "X" containing
the result of serializing this object with JAXB marshaller. For example,

```java
// declare domain object
@FedoraObject
public class Foobar {
    @Pid private long id = 1L;
    private String foo = "bar";
}

// ingest object with FedoraTemplate
fedoraTemplate.ingest(new Foobar(), "logging ingest");
```
Will produce a Fedora object with pid `test:foobar_1` containing a single extensible datastream.

```xml
<foxml:datastream ID="FOOBAR" STATE="A" CONTROL_GROUP="X" VERSIONABLE="false">
    <foxml:datastreamVersion ID="FOOBAR.0" LABEL="foobar" CREATED="2014-11-10T17:09:05.137Z" MIMETYPE="text/xml" SIZE="44">
        <foxml:xmlContent>
            <foobar id="1">
                <foo>bar</foo>
            </foobar>
        </foxml:xmlContent>
    </foxml:datastreamVersion>
</foxml:datastream>
```

Querying the repository is also very easy using provided `ch.unil.spring.data.fedora.core.query.FindObjectsQueryBuilder` query builder.

```java
// build query "pid has test:foobar_*"
FindObjectsQuery query = fedoraTemplate.getQueryBuilder()
        .withField(FindObjectsField.Pid)
        .withOperator(FindObjectsConditionOperator.Has)
        .withPhrase("*", Foobar.class)
        .build();

// use the query to get a page of domain objects (two at a time)
Page<Foobar> page = fedoraTemplate.query(query, Foobar.class, DefaultFindObjectsPageRequest.firstPage(2));

for (Foobar foobar: page){
    // iterate through results
}

// get the next page
if (page.hasNext()){
    page = fedoraTemplate.query(query, Foobar.class, (FindObjectsPageRequest) page.nextPageable());
}
```

### Examples.

Some examples:

- [SimpleTest.java](https://github.unil.ch/gushakov/spring-data-fedora/blob/xstream/spring-data-fedora-examples/src/test/java/ch/unil/spring/data/fedora/examples/basic/SimpleTest.java)

- [SimpleRelsExtTest.java](https://github.unil.ch/gushakov/spring-data-fedora/blob/master/spring-data-fedora-examples/src/test/java/ch/unil/spring/data/fedora/examples/basic/SimpleRelsExtTest.java).
