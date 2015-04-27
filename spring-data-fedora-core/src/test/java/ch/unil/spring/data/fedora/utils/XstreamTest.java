package ch.unil.spring.data.fedora.utils;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author gushakov
 */
public class XstreamTest {

    public static class Foo {

        List<Bar> bars;

        public Foo(List<Bar> bars) {
            this.bars = bars;
        }
    }

    public static class Bar {
        int id;

        public Bar(int id) {
            this.id = id;
        }
    }


    @Test
    public void testMarshalCollection() throws Exception {
        XStream xstream = new XStream();
        xstream.alias("Foo", Foo.class);
        xstream.addImplicitCollection(Foo.class, "bars");
        System.out.println(xstream.toXML(new Foo(Arrays.asList(new Bar(1), new Bar(2)))));
    }

}
