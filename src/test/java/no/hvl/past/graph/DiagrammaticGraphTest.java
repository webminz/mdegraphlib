package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;
import no.hvl.past.graph.operations.DiagrammaticGraph;
import no.hvl.past.graph.operations.Predefined;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

public class DiagrammaticGraphTest {


    @Test
    public void testInheritance() {
        Graph testGraph = new Graph.Builder("test")
                .edge("B", "B.super", "A")
                .edge("C", "C.super", "A")
                .edge("A", "i", "int")
                .edge("B","s", "String")
                .edge("A", "r1", "D")
                .edge("E", "r2", "A")
                .build();

        DiagrammaticGraph dgraph = new DiagrammaticGraph.Builder(testGraph)
                .operation(Predefined.INHERITANCE_OP_NAME)
                .bind("i", "B")
                .bind("s", "B.super")
                .bind("t", "A")
                .apply()
                .operation(Predefined.INHERITANCE_OP_NAME)
                .bind("i", "C")
                .bind("s", "C.super")
                .bind("t", "A")
                .apply()
                .build();

        Set<Triple> allTriples = StreamSupport.stream(dgraph.effectiveGraph().spliterator(), false).collect(Collectors.toSet());

        Set<Triple> expected = new HashSet<>();
        expected.addAll(testGraph.getElements());
        expected.add(new Triple(Name.identifier("B"), Name.identifier("i").prefix(Name.identifier("B.super")), Name.identifier("int")));
        expected.add(new Triple(Name.identifier("B"), Name.identifier("r1").prefix(Name.identifier("B.super")), Name.identifier("D")));
        expected.add(new Triple(Name.identifier("C"), Name.identifier("i").prefix(Name.identifier("C.super")), Name.identifier("int")));
        expected.add(new Triple(Name.identifier("C"), Name.identifier("r1").prefix(Name.identifier("C.super")), Name.identifier("D")));
        expected.add(new Triple(Name.identifier("E"), Name.identifier("r2").prefix(Name.identifier("B.super")), Name.identifier("B")));
        expected.add(new Triple(Name.identifier("E"), Name.identifier("r2").prefix(Name.identifier("C.super")), Name.identifier("C")));

        assertEquals(expected, allTriples);
    }

    // TODO test all the other predicates

}
