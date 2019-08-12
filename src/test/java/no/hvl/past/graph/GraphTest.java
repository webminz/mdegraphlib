package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GraphTest {

    // TODO finish the other test methods!

    Graph INITIAL = new Graph.Builder("0").build();
    Graph FINAL = new Graph.Builder("1").node("*").build();
    Graph EDGE = new Graph.Builder("Edge").edge("s", "e", "t").build();
    Graph TWO = new Graph.Builder("Two").edge("E", "src", "V").edge("E", "tgt", "V").build();
    Graph BIGGER = new Graph.Builder("Example")
            .edge("1", "11", "2")
            .edge("1", "12", "4")
            .edge("1", "13", "5")
            .edge("2", "21", "1")
            .edge("3", "31", "1")
            .edge("3", "32", "1")
            .edge("4", "41", "6")
            .edge("4", "42", "5")
            .edge("5", "51", "6")
            .edge("6", "61", "7")
            .edge("7", "71", "7")
            .edge("8", "81", "6")
            .build();


    @Test
    public void testSum() {

    }

    @Test
    public void testIdentity() {

    }

    @Test
    public void testPrefixing() {

    }

    @Test
    public void testTraversing() {

    }

    @Test
    public void testContainment() {

    }

    @Test
    public void testViolationDanglingEdges() {
        Triple t1 = Triple.fromNode(Name.identifier("A"));
        Triple t2 = new Triple(Name.identifier("A"), Name.identifier("a"), Name.identifier("B"));
        try {
            Graph.create(Name.identifier("G"), new HashSet<>(Arrays.asList(t1, t2)));
            fail();
        } catch (GraphError error) {
            assertEquals(GraphError.ERROR_TYPE.DANGLING_EDGE, error.getErrorType());
            assertEquals(Collections.singleton(t2), error.getAffected());
        }
    }


}
