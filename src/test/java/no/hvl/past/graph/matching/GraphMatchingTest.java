package no.hvl.past.graph.matching;


import no.hvl.past.graph.TestWithGraphLib;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.GraphMorphism;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GraphMatchingTest extends TestWithGraphLib {

    @Test
    public void testMatchingOnlyNodes() throws GraphError {
        Graph twoNodes = getContextCreatingBuilder()
                .node("1")
                .node("2")
                .graph("TWO_NODES")
                .getResult(Graph.class);
        Graph threNodes = getContextCreatingBuilder()
                .node("a")
                .node("b")
                .node("c")
                .graph("THREE_NODES")
                .getResult(Graph.class);
        // according to combinatorics


        assertEquals(8, new GraphMatcher(getExecutionContext(), false).allMatches(threNodes, twoNodes).size());
        assertEquals(9, new GraphMatcher(getExecutionContext(),false).allMatches(twoNodes, threNodes).size());
        assertEquals(0, new GraphMatcher(getExecutionContext(),true).allMatches(threNodes, twoNodes).size());
        assertEquals(6, new GraphMatcher(getExecutionContext(),true).allMatches(twoNodes, threNodes).size());

    }

    @Test
    public void testMatchingSmallExamples() throws GraphError {
        Graph span = getContextCreatingBuilder()
                .edge("a", "1", "b")
                .edge("a", "2", "c")
                .graph("A_SPAN")
                .getResult(Graph.class);

        Graph oneLoopOneEdge = getContextCreatingBuilder()
                .edge("a", "1", "a")
                .edge("a", "2", "z")
                .graph("ONE_LOO_ONE_EDGE")
                .getResult(Graph.class);

        assertEquals(0, new GraphMatcher(getExecutionContext(),false).allMatches( oneLoopOneEdge, span).size());
        assertEquals(4, new GraphMatcher(getExecutionContext(),false).allMatches(span, oneLoopOneEdge).size());
    }


    @Test
    public void testMatchingComplexExample() throws GraphError {
        Graph G = getContextCreatingBuilder()
                .edge("2", "e", "4")
                .edge("4", "d", "3")
                .edge("3", "c", "4")
                .edge("3", "b", "1")
                .edge("3", "a", "1")
                .graph("G")
                .getResult(Graph.class);

        Graph H = getContextCreatingBuilder()
                .edge("B", "i", "A")
                .edge("B", "ii", "C")
                .edge("D", "iii", "C")
                .graph("H")
                .getResult(Graph.class);


        assertEquals(21, new GraphMatcher(getExecutionContext(),false).allMatches(H, G).size());
        assertEquals(2, new GraphMatcher(getExecutionContext(),true).allMatches(H, G).size());

    }

    @Test
    public void testTypedMatches() throws GraphError {
        Graph instance = getContextCreatingBuilder()
                .edge("A", "a", "B")
                .edge("A'", "a'", "B")
                .edge("B", "b", "C")
                .edge("B", "b'", "C'")
                .graph("G_1")
                .getResult(Graph.class);

        Graph type = getContextCreatingBuilder()
                .edge("1", "12", "2")
                .edge("2", "22", "2")
                .graph("G_0")
                .getResult(Graph.class);

        Graph pattern = getContextCreatingBuilder()
                .edge("X", "xy", "Y")
                .graph("G_0Sub1")
                .getResult(Graph.class);


        GraphMorphism typedInstance = getContextCreatingBuilder()
                .domain(instance)
                .codomain(type)
                .map("A", "1")
                .map("A'", "1")
                .map("B", "2")
                .map("C", "2")
                .map("C'", "2")
                .map("a", "12")
                .map("a'", "12")
                .map("b", "22")
                .map("b'", "22")
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism typedPattern = getContextCreatingBuilder()
                .domain(pattern)
                .codomain(type)
                .map("X", "1")
                .map("xy", "12")
                .map("Y", "2")
                .morphism("m0Sub1")
                .getResult(GraphMorphism.class);

        List<GraphMorphism> result = new GraphMatcher(getExecutionContext(),false).allTypedMatches(typedPattern, typedInstance);
        assertEquals(2, result.size());
    }


}
