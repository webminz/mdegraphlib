package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import no.hvl.past.util.Pair;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MorphismTest extends AbstractTest {

    @Test
    public void testSetTheoreticPullback() {

        Graph B = getContextCreatingBuilder()
                .node("1")
                .node("2")
                .node("3")
                .node("4")
                .graph("B")
                .getGraphResult();

        Graph C = getContextCreatingBuilder()
                .node("5")
                .node("6")
                .node("7")
                .graph("C")
                .getGraphResult();

        Graph D = getContextCreatingBuilder()
                .node("a")
                .node("b")
                .node("c")
                .node("d")
                .graph("D")
                .getGraphResult();

        GraphMorphism bd = getContextCreatingBuilder()
                .domain(B)
                .codomain(D)
                .map("1", "a")
                .map("2", "a")
                .map("3", "b")
                .map("4", "c")
                .morphism("bc")
                .getMorphismResult();

        GraphMorphism cd = getContextCreatingBuilder()
                .domain(C)
                .codomain(D)
                .map("5", "a")
                .map("6", "b")
                .map("7", "d")
                .morphism("cd")
                .getMorphismResult();


        Pair<GraphMorphism, GraphMorphism> result = bd.pullback(cd, Name.identifier("A"));
        assertStreamEquals(result.getFirst().domain().elements().map(Triple::getLabel), id("3").pair(id("6")), id("1").pair(id("5")), id("2").pair(id("5")));

    }

    @Test
    public void testPullbackOfRealGraph() {

        Graph G_B = getContextCreatingBuilder()
                .edge("1", "i", "2")
                .edge("1", "ii", "3")
                .graph("G_B")
                .getGraphResult();

        Graph G_c = getContextCreatingBuilder()
                .edge("5", "v", "6")
                .edge("6", "vi", "7")
                .graph("G_C")
                .getGraphResult();

        Graph G_D = getContextCreatingBuilder()
                .edge("A", "a", "B")
                .edge("A", "b", "C")
                .edge("B", "c", "C")
                .graph("G_D")
                .getGraphResult();

        GraphMorphism bd = getContextCreatingBuilder()
                .domain(G_B)
                .codomain(G_D)
                .typedEdge(new Triple(Name.identifier("1"), Name.identifier("i"), Name.identifier("2")),
                        new Triple(Name.identifier("A"), Name.identifier("a"), Name.identifier("B")))
                .typedEdge(new Triple(Name.identifier("1"), Name.identifier("ii"), Name.identifier("3")),
                        new Triple(Name.identifier("A"), Name.identifier("b"), Name.identifier("C")))
                .morphism("bd")
                .getMorphismResult();

        GraphMorphism cd = getContextCreatingBuilder()
                .domain(G_c)
                .codomain(G_D)
                .typedEdge(new Triple(Name.identifier("5"), Name.identifier("v"), Name.identifier("6")),
                        new Triple(Name.identifier("A"), Name.identifier("a"), Name.identifier("B")))
                .typedEdge(new Triple(Name.identifier("6"), Name.identifier("vi"), Name.identifier("7")),
                        new Triple(Name.identifier("B"), Name.identifier("c"), Name.identifier("C")))
                .morphism("cd")
                .getMorphismResult();

        Pair<GraphMorphism, GraphMorphism> result = bd.pullback(cd, Name.identifier("A"));

        assertGraphsEqual(result.getFirst().domain(),
                Triple.node(id("3").pair(id("7"))),
                Triple.node(id("1").pair(id("5"))),
                Triple.node(id("2").pair(id("6"))),
                new Triple(id("1").pair(id("5")),  id("i").pair(id("v")), id("2").pair(id("6"))));
    }


    @Test
    public void testPullbackWithCopying() {
        Graph typeGraph = getContextCreatingBuilder()
                .edge("C", "r", "C")
                .edge("C", "e", "C")
                .graph("TG")
                .getGraphResult();

        Graph testGraph = getContextCreatingBuilder()
                .edge(Name.identifier("B"), Name.identifier("B").subTypeOf(Name.identifier("A")), Name.identifier("A"))
                .edge(Name.identifier("C"), Name.identifier("C").subTypeOf(Name.identifier("A")), Name.identifier("A"))
                .edge("A", "i", "int")
                .edge("B", "s", "String")
                .edge("A", "r1", "D")
                .edge("E", "r2", "A")
                .graph("G")
                .getGraphResult();

        GraphMorphism typing = new GraphBuilders()
                .domain(testGraph)
                .codomain(typeGraph)
                .map("A", "C")
                .map("B", "C")
                .map("C", "C")
                .map("int", "C")
                .map("String", "C")
                .map("E", "C")
                .map("D", "C")
                .map("i", "r")
                .map("s", "r")
                .map("r1", "r")
                .map("r2", "r")
                .map(Name.identifier("B").subTypeOf(Name.identifier("A")), Name.identifier("e"))
                .map(Name.identifier("C").subTypeOf(Name.identifier("A")), Name.identifier("e"))
                .morphism("t_G")
                .getMorphismResult();

        Graph selectGraph = getContextCreatingBuilder()
                .edge(Name.identifier("1"), Name.identifier("2"), Name.identifier("3"))
                .graph("G")
                .getGraphResult();


        GraphMorphism selection = new GraphBuilders()
                .domain(selectGraph)
                .codomain(typeGraph)
                .map("1", "C")
                .map("2", "r")
                .map("3", "C")
                .morphism("s")
                .getMorphismResult();

        Set<Triple> expected = new HashSet<>();
        expected.add(new Triple(
                id("1").pair(id("E")),
                id("2").pair(id("r2")),
                id("3").pair(id("A"))));
        expected.add(new Triple(
                id("1").pair(id("A")),
                id("2").pair(id("r1")),
                id("3").pair(id("D"))));
        expected.add(new Triple(
                id("1").pair(id("A")),
                id("2").pair(id("i")),
                id("3").pair(id("int"))));
        expected.add(new Triple(
                id("1").pair(id("B")),
                id("2").pair(id("s")),
                id("3").pair(id("String"))));
        expected.add(Triple.node(id("1").pair(id("E"))));
        expected.add(Triple.node(id("1").pair(id("A"))));
        expected.add(Triple.node(id("1").pair(id("B"))));
        expected.add(Triple.node(id("1").pair(id("C"))));
        expected.add(Triple.node(id("1").pair(id("D"))));
        expected.add(Triple.node(id("1").pair(id("int"))));
        expected.add(Triple.node(id("1").pair(id("String"))));
        expected.add(Triple.node(id("3").pair(id("A"))));
        expected.add(Triple.node(id("3").pair(id("B"))));
        expected.add(Triple.node(id("3").pair(id("C"))));
        expected.add(Triple.node(id("3").pair(id("D"))));
        expected.add(Triple.node(id("3").pair(id("E"))));
        expected.add(Triple.node(id("3").pair(id("int"))));
        expected.add(Triple.node(id("3").pair(id("String"))));

        Pair<GraphMorphism,GraphMorphism> pullback = selection.pullback(typing, Name.identifier("APEX"));
        GraphMorphism typingAlongSelection = pullback.getFirst();
        assertStreamEquals(typingAlongSelection.select(new Triple(id("1"), id("2"), id("3"))),
                new Triple(
                id("1").pair(id("A")),
                id("2").pair(id("r1")),
                id("3").pair(id("D"))),
                Triple.edge(
                        id("1").pair(id("E")),
                        id("2").pair(id("r2")),
                        id("3").pair(id("A"))
                ),
                Triple.edge(
                        id("1").pair(id("A")),
                        id("2").pair(id("i")),
                        id("3").pair(id("int"))
                ),
                Triple.edge(
                        id("1").pair(id("B")),
                        id("2").pair(id("s")),
                        id("3").pair(id("String"))
                ));
        assertStreamEquals(expected, pullback.getFirst().domain().elements());
    }




    @Test
    public void testIsTotal() {
        Graph A = getContextCreatingBuilder()
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .graph("A")
                .getGraphResult();

        Graph B = getContextCreatingBuilder()
                .edge("1", "c", "2")
                .graph("B")
                .getGraphResult();

        GraphMorphism m1 = new GraphBuilders()
                .domain(A)
                .codomain(B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .morphism("m1")
                .getMorphismResult();

        GraphMorphism m2 = new GraphBuilders()
                .domain(B)
                .codomain(A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .morphism("m2")
                .getMorphismResult();

        assertFalse(m1.isTotal());
        assertTrue(m2.isTotal());
    }

    @Test
    public void testIsInjective() {
        Graph A = getContextCreatingBuilder()
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .graph("A")
                .getGraphResult();

        Graph B = getContextCreatingBuilder()
                .edge("1", "c", "2")
                .graph("B")
                .getGraphResult();

        GraphMorphism m1 = getContextCreatingBuilder()
                .domain(A)
                .codomain(B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .map("b", "c")
                .morphism("m1")
                .getMorphismResult();

        GraphMorphism m2 = getContextCreatingBuilder()
                .domain(B)
                .codomain(A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .morphism("m2")
                .getMorphismResult();

        assertTrue(m1.isTotal());
        assertFalse(m1.isInjective());

        assertTrue(m2.isInjective());
    }

    @Test
    public void testIsSurjective() {

        Graph A = getContextCreatingBuilder()
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .graph("A")
                .getGraphResult();

        Graph B = getContextCreatingBuilder()
                .edge("1", "c", "2")
                .graph("B")
                .getGraphResult();

        GraphMorphism m1 = getContextCreatingBuilder()
                .domain(A)
                .codomain(B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .map("b", "c")
                .morphism("m1")
                .getMorphismResult();

        GraphMorphism m2 = getContextCreatingBuilder()
                .domain(B)
                .codomain(A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .morphism("m2")
                .getMorphismResult();

        assertTrue(m2.isTotal());
        assertTrue(m2.isInjective());
        assertFalse(m2.isSurjective());

        assertTrue(m1.isSurjective());
    }

    @Test
    public void testToTypedGraph() {

        Graph typ = getContextCreatingBuilder()
                .edge("Class", "reference", "Class")
                .edge("Class", "attribute", "Type")
                .edge("Class", "extends", "Class")
                .graph("TYP")
                .getGraphResult();

        Graph instance = getContextCreatingBuilder()
                .graph("INSTANCE")
                .edge("B", "B.super", "A")
                .edge("C", "C.super", "A")
                .edge("A", "i", "int")
                .edge("B", "s", "String")
                .edge("A", "r", "D")
                .node("untyped")
                .getGraphResult();

        GraphMorphism typing = getContextCreatingBuilder()
                .domain(instance)
                .codomain(typ)
                .map("A", "Class")
                .map("B", "Class")
                .map("C", "Class")
                .map("D", "Class")
                .map("int", "Type")
                .map("String", "Type")
                .map("B.super", "extends")
                .map("C.super", "extends")
                .map("r", "reference")
                .map("s", "attribute")
                .map("i", "attribute")
                .morphism("t")
                .getMorphismResult();

        Graph expected = getContextCreatingBuilder()
                .edge(
                        Name.identifier("B").typeBy(Name.identifier("Class")),
                        Name.identifier("B.super").typeBy(Name.identifier("extends")),
                        Name.identifier("A").typeBy(Name.identifier("Class")))
                .edge(
                        Name.identifier("C").typeBy(Name.identifier("Class")),
                        Name.identifier("C.super").typeBy(Name.identifier("extends")),
                        Name.identifier("A").typeBy(Name.identifier("Class")))
                .edge(
                        Name.identifier("A").typeBy(Name.identifier("Class")),
                        Name.identifier("i").typeBy(Name.identifier("attribute")),
                        Name.identifier("int").typeBy(Name.identifier("Type")))
                .edge(
                        Name.identifier("B").typeBy(Name.identifier("Class")),
                        Name.identifier("s").typeBy(Name.identifier("attribute")),
                        Name.identifier("String").typeBy(Name.identifier("Type")))
                .edge(
                        Name.identifier("A").typeBy(Name.identifier("Class")),
                        Name.identifier("r").typeBy(Name.identifier("reference")),
                        Name.identifier("D").typeBy(Name.identifier("Class")))
                .graph(Name.identifier("INSTANCE").typeBy(Name.identifier("TYP")))
                .getGraphResult();


            Graph actual = typing.flatten();

            assertFalse(typing.isTotal()); // There is an untyped element!
            assertFalse(actual.mentions(Name.identifier("untyped")));
            assertFalse(actual.mentions(Name.identifier("A")));
            assertTrue(actual.mentions(Name.identifier("A").typeBy(Name.identifier("Class"))));
            assertGraphsEqual(expected, actual);
    }


    @Test
    public void testFromTypedGraph() {
        Graph source = getContextCreatingBuilder()
                .edge(
                        Name.identifier("B").typeBy(Name.identifier("Class")),
                        Name.identifier("B.super").typeBy(Name.identifier("extends")),
                        Name.identifier("A").typeBy(Name.identifier("Class")))
                .edge(
                        Name.identifier("C").typeBy(Name.identifier("Class")),
                        Name.identifier("C.super").typeBy(Name.identifier("extends")),
                        Name.identifier("A").typeBy(Name.identifier("Class")))
                .edge(
                        Name.identifier("A").typeBy(Name.identifier("Class")),
                        Name.identifier("i").typeBy(Name.identifier("attribute")),
                        Name.identifier("int").typeBy(Name.identifier("Type")))
                .edge(
                        Name.identifier("B").typeBy(Name.identifier("Class")),
                        Name.identifier("s").typeBy(Name.identifier("attribute")),
                        Name.identifier("String").typeBy(Name.identifier("Type")))
                .edge(
                        Name.identifier("A").typeBy(Name.identifier("Class")),
                        Name.identifier("r").typeBy(Name.identifier("reference")),
                        Name.identifier("D").typeBy(Name.identifier("Class")))
                .graph(Name.identifier("INSTANCE").typeBy(Name.identifier("TYP")))
                .getGraphResult();


        Graph typ = getContextCreatingBuilder()
                .edge("Class", "reference", "Class")
                .edge("Class", "attribute", "Type")
                .edge("Class", "extends", "Class")
                .graph("TYP")
                .getGraphResult();

        Graph instance = getContextCreatingBuilder()
                .edge("B", "B.super", "A")
                .edge("C", "C.super", "A")
                .edge("A", "i", "int")
                .edge("B", "s", "String")
                .edge("A", "r", "D")
                .graph("INSTANCE")
                .getGraphResult();


        GraphMorphism expected = new GraphBuilders()
                .domain(instance)
                .codomain(typ)
                .map("A", "Class")
                .map("B", "Class")
                .map("C", "Class")
                .map("D", "Class")
                .map("int", "Type")
                .map("String", "Type")
                .map("B.super", "extends")
                .map("C.super", "extends")
                .map("r", "reference")
                .map("s", "attribute")
                .map("i", "attribute")
                .morphism("t")
                .getMorphismResult();

        assertMorphismsEqual(expected, GraphMorphism.unflatten(source, typ, id("id")));
    }



    @Test
    public void testViolationHomProp() {
        Graph g1 = getContextCreatingBuilder()
                .edge("A", "a", "B")
                .graph("G1")
                .getGraphResult();

        Graph g2 = getContextCreatingBuilder()
                .edge("B", "a", "A")
                .graph("G2")
                .getGraphResult();

        GraphMorphism invalid1 = getErrorIgnoringBuilder()
                .domain(g1)
                .codomain(g2)
                .map("A", "A")
                .map("a", "a")
                .map("B", "B")
                .morphism("m")
                .getMorphismResult();

        assertFalse(invalid1.verify());
        addExpectedTriple(Triple.edge(Name.identifier("A"),Name.identifier("a"), Name.identifier("B")));
        assertStreamEquals(expected(), invalid1.homPropViolations());


        // TODO extend
    }


    // TODO test composition




}
