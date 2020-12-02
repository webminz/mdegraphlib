package no.hvl.past.graph;


import no.hvl.past.searching.GraphMatcher;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GraphMatchingTest extends AbstractTest{
// TODO restore this methods

//    @Test
//    public void testMatchingOnlyNodes() {
//        Graph twoNodes = new GraphBuilder("TWO_NODES")
//                .node("1")
//                .node("2")
//                .build();
//        Graph threNodes = new GraphBuilder("THREE_NODES")
//                .node("a")
//                .node("b")
//                .node("c")
//                .build();
//        // according to combinatorics
//        assertEquals(9, twoNodes.findMatches(threNodes, false).size());
//        assertEquals(8, threNodes.findMatches(twoNodes, false).size());
//        assertEquals(6, twoNodes.findMatches(threNodes, true).size());
//        assertEquals(0, threNodes.findMatches(twoNodes, true).size());
//
//        assertEquals(8, new GraphMatcher(false).allMatches(threNodes, twoNodes).size());
//        assertEquals(9, new GraphMatcher(false).allMatches(twoNodes, threNodes).size());
//        assertEquals(0, new GraphMatcher(true).allMatches(threNodes, twoNodes).size());
//        assertEquals(6, new GraphMatcher(true).allMatches(twoNodes, threNodes).size());
//
//    }
//
//    @Test
//    public void testMatchingSmallExamples() {
//        GraphImpl span = new GraphBuilder("SPAN")
//                .edge("a", "1", "b")
//                .edge("a", "2", "c")
//                .build();
//
//        GraphImpl oneLoopOneEdge = new GraphBuilder("ONE_LOO_ONE_EDGE")
//                .edge("a", "1", "a")
//                .edge("a", "2", "z")
//                .build();
//
//        assertEquals(0, oneLoopOneEdge.findMatches(span, false).size());
//        assertEquals(4, span.findMatches(oneLoopOneEdge, false).size());
//    }
//
//
//    @Test
//    public void testMatchingComplexExample() {
//        GraphImpl G = new GraphBuilder("G")
//                .edge("2", "e", "4")
//                .edge("4", "d", "3")
//                .edge("3", "c", "4")
//                .edge("3", "b", "1")
//                .edge("3", "a", "1")
//                .build();
//
//        GraphImpl H = new GraphBuilder("H")
//                .edge("B", "i", "A")
//                .edge("B", "ii", "C")
//                .edge("D", "iii", "C")
//                .build();
//
//
//        assertEquals(21, new GraphMatcher(false).allMatches(H, G).size());
//        assertEquals(2, new GraphMatcher(true).allMatches(H, G).size());
//
//    }
//
//    @Test
//    public void testTypedMatches() {
//        GraphMorphismImpl typedPattern = new GraphMorphismImpl.TypedGrapBuilder(id("pat"), ExampleGraphs.ACCOUNTING_GRAPH)
//                .typedEdge(t("A", "t", "T"), t(ExampleGraphs.ACCOUNT, ExampleGraphs.TYPE, ExampleGraphs.ACCOUNT_TYPE))
//                .build();
//
//        List<GraphMorphismImpl> result = new GraphMatcher(false).allTypedMatches(typedPattern, ExampleGraphs.ACCOUNTING_INSTANCE);
//        assertEquals(10, result.size());
//    }


}
