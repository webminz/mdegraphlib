package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.GraphTest;
import no.hvl.past.names.Name;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CreatingAndTraversing extends GraphTest {


    @Test
    public void testUntyped() throws GraphError {
        TreeBuildStrategy buildStrategy = new TreeBuildStrategy(Name.identifier("T1"));
        Node.Builder builder = buildStrategy.root();
        Node.Builder sub1 = buildStrategy.objectChild(builder, "sub");
        buildStrategy.simpleChild(sub1, "id", "1");
        Node.Builder sub1Foo = buildStrategy.objectChild(sub1, "foo");
        buildStrategy.simpleChild(sub1Foo, "name", "Foo");
        Node.Builder sub1Bar = buildStrategy.objectChild(sub1, "bar");
        buildStrategy.simpleChild(sub1Bar,"name", "Bar");
        Node.Builder sub2 = buildStrategy.objectChild(builder, "sub");
        buildStrategy.simpleChild(sub2, "id", BigInteger.valueOf(2));
        Tree tree = buildStrategy.tree(builder.build());

        List<Branch> sub = tree.root().childrenByKey("sub").collect(Collectors.toList());
        assertEquals(2, sub.size());
        assertTrue(sub.get(0).isCollection());
        assertEquals("sub", sub.get(0).label());

        Iterator<Node> iterator = TreeIterator.depthFirstUntypedAll(tree.root());

        assertEquals(builder.getElementName(), iterator.next().elementName());
        assertEquals(sub1.getElementName(), iterator.next().elementName());
        assertEquals(Name.value("1"), iterator.next().elementName());
        assertEquals(sub1Foo.getElementName(), iterator.next().elementName());
        assertEquals(Name.value("Foo"), iterator.next().elementName());
        assertEquals(sub1Bar.getElementName(), iterator.next().elementName());
        assertEquals(Name.value("Bar"), iterator.next().elementName());
        assertEquals(sub2.getElementName(), iterator.next().elementName());
        assertEquals(Name.value(BigInteger.valueOf(2)), iterator.next().elementName());
        assertFalse(iterator.hasNext());

        Graph result = getContextCreatingBuilder()
                .node(builder.elementName)
                .node(sub1.elementName)
                .node(sub1Foo.elementName)
                .node(sub1Bar.elementName)
                .node(sub2.elementName)
                .edge(builder.elementName, Name.identifier("sub").index(0).prefixWith(builder.elementName), sub1.elementName)
                .edge(builder.elementName, Name.identifier("sub").index(1).prefixWith(builder.elementName), sub2.elementName)
                .edge(sub1.elementName, Name.identifier("id").prefixWith(sub1.elementName), Name.value("1"))
                .edge(sub2.elementName, Name.identifier("id").prefixWith(sub2.elementName), Name.value(BigInteger.valueOf(2)))
                .edge(sub1.elementName, Name.identifier("foo").prefixWith(sub1.elementName), sub1Foo.elementName)
                .edge(sub1.elementName, Name.identifier("bar").prefixWith(sub1.elementName), sub1Bar.elementName)
                .edge(sub1Foo.elementName, Name.identifier("name").prefixWith(sub1Foo.elementName), Name.value("Foo"))
                .edge(sub1Bar.elementName, Name.identifier("name").prefixWith(sub1Bar.elementName), Name.value("Bar"))
                .graph(Name.identifier("T1"))
                .getResult(Graph.class);

        assertEquals(result.elements().collect(Collectors.toSet()), tree.elements().collect(Collectors.toSet()));
    }

    @Test
    public void testCycles() {
        // TODO
    }


}
