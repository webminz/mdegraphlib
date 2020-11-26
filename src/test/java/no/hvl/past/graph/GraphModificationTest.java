package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GraphModificationTest {

//    GraphImpl TEST = new GraphBuilder("G")
//            .edge("1", "a", "2")
//            .edge("2", "b", "3")
//            .build();
//
//    public static void assertContainsEdgeNow(Graph base, Triple triple) {
//        Optional<Triple> result = base.get(triple.getLabel());
//        assertTrue(result.isPresent());
//        assertEquals(triple, result.get());
//        assertTrue(base.mentions(triple.getLabel()));
//        assertTrue(base.contains(triple));
//        assertTrue(base.outgoing(triple.getSource()).contains(triple));
//        assertTrue(base.incoming(triple.getTarget()).contains(triple));
//    }
//
//    public static void assertNotContainsEdgeNow(Graph base, Triple triple) {
//        Optional<Triple> result = base.get(triple.getLabel());
//        assertFalse(result.isPresent());
//        assertFalse(base.mentions(triple.getLabel()));
//        assertFalse(base.contains(triple));
//        assertFalse(base.outgoing(triple.getSource()).contains(triple));
//        assertFalse(base.incoming(triple.getTarget()).contains(triple));
//    }
//
//    public static Set<Triple> produceEdgeSet(Graph graph) {
//        return StreamSupport.stream(graph.spliterator(), false).collect(Collectors.toSet());
//    }
//
//    @Test
//    public void testSimpleInsertion() {
//        Triple newTriple = new Triple(
//                Name.identifier("2"),
//                Name.identifier("c"),
//                Name.identifier("3")
//        );
//        Set<Triple> expected = new HashSet<>(produceEdgeSet(TEST));
//        expected.add(newTriple);
//        Graph g = GraphModification.createWithDeletionPrecedence(
//                TEST, Collections.singleton(newTriple),
//                Collections.emptySet());
//        assertContainsEdgeNow(g, newTriple);
//        assertEquals(expected, produceEdgeSet(g));
//    }
//
//    @Test
//    public void testSimpleDeletion() {
//        Triple oldTriple = new Triple(
//                Name.identifier("2"),
//                Name.identifier("b"),
//                Name.identifier("3")
//        );
//        Set<Triple> expected = new HashSet<>(produceEdgeSet(TEST));
//        expected.remove(oldTriple);
//        Graph g = GraphModification.createWithAdditionPrecedence(
//                TEST, Collections.emptySet(),
//                Collections.singleton(oldTriple)
//        );
//        assertNotContainsEdgeNow(g, oldTriple);
//        assertEquals(expected, produceEdgeSet(g));
//    }
//
//    @Test
//    public void additionAndDeletionDeletionWins() {
//        Triple add = new Triple(
//                Name.identifier("3"),
//                Name.identifier("c"),
//                Name.identifier("4")
//        );
//        Triple del = new Triple(
//                Name.identifier("1"),
//                Name.identifier("a"),
//                Name.identifier("2")
//        );
//        Triple conf = new Triple(
//                Name.identifier("2"),
//                Name.identifier("b"),
//                Name.identifier("3"));
//        Set<Triple> adds = new HashSet<>();
//        adds.add(add);
//        adds.add(conf);
//        Set<Triple> removes = new HashSet<>();
//        removes.add(del);
//        removes.add(conf);
//        Set<Triple> expected = new HashSet<>();
//        expected.add(add);
//        expected.add(Triple.node(Name.identifier("1")));
//        expected.add(Triple.node(Name.identifier("2")));
//        expected.add(Triple.node(Name.identifier("3")));
//        Graph result = GraphModification.createWithDeletionPrecedence(
//                TEST,
//                adds,
//                removes
//        );
//        assertContainsEdgeNow(result, add);
//        assertNotContainsEdgeNow(result, conf);
//        assertNotContainsEdgeNow(result, del);
//        assertEquals(expected, produceEdgeSet(result));
//    }
//
//    @Test
//    public void additionAndDeletionAdditionWins() {
//        Triple add = new Triple(
//                Name.identifier("3"),
//                Name.identifier("c"),
//                Name.identifier("4")
//        );
//        Triple del = new Triple(
//                Name.identifier("1"),
//                Name.identifier("a"),
//                Name.identifier("2")
//        );
//        Triple conf = new Triple(
//                Name.identifier("2"),
//                Name.identifier("d"),
//                Name.identifier("3"));
//        Set<Triple> adds = new HashSet<>();
//        adds.add(add);
//        adds.add(conf);
//        Set<Triple> removes = new HashSet<>();
//        removes.add(del);
//        removes.add(conf);
//        Set<Triple> expected = new HashSet<>();
//        expected.addAll(produceEdgeSet(TEST));
//        expected.add(add);
//        expected.add(conf);
//        expected.remove(del);
//        Graph result = GraphModification.createWithAdditionPrecedence(
//                TEST,
//                adds,
//                removes
//        );
//        assertContainsEdgeNow(result, add);
//        assertContainsEdgeNow(result, conf);
//        assertNotContainsEdgeNow(result, del);
//        assertEquals(expected, produceEdgeSet(result));
//    }
//
//
//    @Test
//    public void testMayProduceDanglingEdges() {
//        try {
//            GraphImpl.materialize(GraphModification.createWithDeletionPrecedence(
//                    TEST,
//                    Collections.emptySet(),
//                    Collections.singleton(Triple.node(Name.identifier("1")))
//            ));
//            fail();
//        } catch (GraphError error) {
//            assertEquals(GraphError.ERROR_TYPE.DANGLING_EDGE, error.getErrorType());
//            assertEquals(Collections.singleton(new Triple(
//                    Name.identifier("1"),
//                    Name.identifier("a"),
//                    Name.identifier("2")
//            )), error.getAffected());
//
//        }
//    }
//
//    @Test
//    public void testRename() {
//        Triple oneOld = new Triple(
//                Name.identifier("1"),
//                Name.identifier("a"),
//                Name.identifier("2")
//        );
//        Triple twoOld = new Triple(
//                Name.identifier("2"),
//                Name.identifier("b"),
//                Name.identifier("3")
//        );
//        Triple oneNew = oneOld.prefix(Name.identifier("G"));
//        Triple twoNew = twoOld.prefix(Name.identifier("G"));
//        Graph result = GraphModification.createWithAdditionPrecedence(
//                TEST,
//                Collections.emptySet(),
//                Collections.emptySet(),
//                Name.identifier("G")
//        );
//        assertContainsEdgeNow(result, oneNew);
//        assertContainsEdgeNow(result, twoNew);
//        assertNotContainsEdgeNow(result, oneOld);
//        assertNotContainsEdgeNow(result, twoOld);
//        Set<Triple> expected = new HashSet<>();
//        expected.add(oneNew);
//        expected.add(twoNew);
//        expected.add(Triple.node(Name.identifier("1")).prefix(Name.identifier("G")));
//        expected.add(Triple.node(Name.identifier("2")).prefix(Name.identifier("G")));
//        expected.add(Triple.node(Name.identifier("3")).prefix(Name.identifier("G")));
//        assertEquals(expected, produceEdgeSet(result));
//    }
//
//    @Test
//    public void testRenameWithInsertsAndDeletes() {
//        Triple a1 = new Triple(
//                Name.identifier("1"),
//                Name.identifier("c"),
//                Name.identifier("2")
//        ).prefix(Name.identifier("G"));
//        Triple a2 = new Triple(
//                Name.identifier("2"),
//                Name.identifier("d"),
//                Name.identifier("3")
//        );
//        Triple del = new Triple(
//                Name.identifier("2"),
//                Name.identifier("b"),
//                Name.identifier("3")
//        );
//        Set<Triple> toAdd = new HashSet<>();
//        toAdd.add(a1);
//        toAdd.add(a2);
//        Graph result = GraphModification.createWithAdditionPrecedence(
//                TEST,
//                toAdd,
//                Collections.singleton(del),
//                Name.identifier("G")
//        );
//        assertContainsEdgeNow(result, a1);
//        assertContainsEdgeNow(result, a2);
//        assertContainsEdgeNow(result, new Triple(
//                Name.identifier("1"),
//                Name.identifier("a"),
//                Name.identifier("2")
//        ).prefix(Name.identifier("G")));
//        assertNotContainsEdgeNow(result, del.prefix(Name.identifier("G")));
//        Set<Triple> expected = new HashSet<>();
//        expected.addAll(produceEdgeSet(TEST).stream().map(t -> t.prefix(Name.identifier("G"))).collect(Collectors.toSet()));
//        expected.remove(del.prefix(Name.identifier("G")));
//        expected.add(a1);
//        expected.add(a2);
//        assertEquals(expected, produceEdgeSet(result));
//    }
//


}
