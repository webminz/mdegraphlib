package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;

import no.hvl.past.graph.TestWithGraphLib;
import no.hvl.past.names.Name;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class CreatingAndTraversing extends TestWithGraphLib {


    @Test
    public void testUntyped() throws GraphError {
        Tree tree = new TreeBuilder()
                .name(Name.identifier("T1"))
                    .root(Name.anonymousIdentifier())
                        .branch("sub")
                            .complexChild(Name.anonymousIdentifier())
                                .branch("id")
                                    .simpleChild(Name.value("1"))
                                .endBranch()
                                .branch("name")
                                    .simpleChild(Name.value("Foo"))
                                    .simpleChild(Name.value("Bar"))
                                .endBranch()
                            .endNode()
                        .complexChild(Name.anonymousIdentifier())
                            .branch("id")
                               .simpleChild(Name.value(BigInteger.valueOf(2)))
                            .endBranch()
                        .endNode()
                        .endBranch()
                    .endRoot()
                .build();

        List<Branch> sub = tree.root().childrenByKey(id("sub")).collect(Collectors.toList());
        assertEquals(2, sub.size());
        assertTrue(sub.get(0).isCollection());
        assertEquals(id("sub"), sub.get(0).key());

        Iterator<Node> iterator = TreeIterator.depthFirstUntypedAll(tree.root());

        assertEquals(tree.root().elementName(), iterator.next().elementName());
        Node sub1 = tree.root().childrenByKeyAndNo(id("sub"), 0).get().child();
        assertEquals(sub1.elementName(), iterator.next().elementName());
        assertEquals(Name.value("1"), iterator.next().elementName());
        assertEquals(Name.value("Foo"), iterator.next().elementName());
        assertEquals(Name.value("Bar"), iterator.next().elementName());
        Node sub2 = tree.root().childrenByKeyAndNo(id("sub"), 1).get().child();
        assertEquals(sub2.elementName(), iterator.next().elementName());
        assertEquals(Name.value(BigInteger.valueOf(2)), iterator.next().elementName());
        assertFalse(iterator.hasNext());

        Graph result = getContextCreatingBuilder()
                .node(tree.root().elementName())
                .node(sub1.elementName())
                .node(sub2.elementName())
                .edge(tree.root().elementName(), Name.identifier("sub").index(0).prefixWith(tree.root().elementName()), sub1.elementName())
                .edge(tree.root().elementName(), Name.identifier("sub").index(1).prefixWith(tree.root().elementName()), sub2.elementName())
                .edge(sub1.elementName(), Name.identifier("id").prefixWith(sub1.elementName()), Name.value("1"))
                .edge(sub2.elementName(), Name.identifier("id").prefixWith(sub2.elementName()), Name.value(BigInteger.valueOf(2)))
                .edge(sub1.elementName(), Name.identifier("name").index(0).prefixWith(sub1.elementName()), Name.value("Foo"))
                .edge(sub1.elementName(), Name.identifier("name").index(1).prefixWith(sub1.elementName()), Name.value("Bar"))
                .graph(Name.identifier("T1"))
                .getResult(Graph.class);

        assertEquals(result.elements().collect(Collectors.toSet()), tree.elements().collect(Collectors.toSet()));
    }




}
