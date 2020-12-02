package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import no.hvl.past.util.Pair;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class MorphismTest extends AbstractTest {

    @Test
    public void testSetTheoreticPullback() throws GraphError {

        Graph B = getContextCreatingBuilder()
                .node("1")
                .node("2")
                .node("3")
                .node("4")
                .graph("B")
                .getResult(Graph.class);

        Graph C = getContextCreatingBuilder()
                .node("5")
                .node("6")
                .node("7")
                .graph("C")
                .getResult(Graph.class);

        Graph D = getContextCreatingBuilder()
                .node("a")
                .node("b")
                .node("c")
                .node("d")
                .graph("D")
                .getResult(Graph.class);

        GraphMorphism bd = getContextCreatingBuilder()
                .domain(B)
                .codomain(D)
                .map("1", "a")
                .map("2", "a")
                .map("3", "b")
                .map("4", "c")
                .morphism("bc")
                .getResult(GraphMorphism.class);

        GraphMorphism cd = getContextCreatingBuilder()
                .domain(C)
                .codomain(D)
                .map("5", "a")
                .map("6", "b")
                .map("7", "d")
                .morphism("cd")
                .getResult(GraphMorphism.class);


        Pair<GraphMorphism, GraphMorphism> result = bd.pullback(cd, Name.identifier("A"));
        assertStreamEquals(result.getFirst().domain().elements().map(Triple::getLabel), id("3").pair(id("6")), id("1").pair(id("5")), id("2").pair(id("5")));

    }

    @Test
    public void testPullbackOfRealGraph() throws GraphError {

        Graph G_B = getContextCreatingBuilder()
                .edge("1", "i", "2")
                .edge("1", "ii", "3")
                .graph("G_B")
                .getResult(Graph.class);

        Graph G_c = getContextCreatingBuilder()
                .edge("5", "v", "6")
                .edge("6", "vi", "7")
                .graph("G_C")
                .getResult(Graph.class);

        Graph G_D = getContextCreatingBuilder()
                .edge("A", "a", "B")
                .edge("A", "b", "C")
                .edge("B", "c", "C")
                .graph("G_D")
                .getResult(Graph.class);

        GraphMorphism bd = getContextCreatingBuilder()
                .domain(G_B)
                .codomain(G_D)
                .typedEdge(new Triple(Name.identifier("1"), Name.identifier("i"), Name.identifier("2")),
                        new Triple(Name.identifier("A"), Name.identifier("a"), Name.identifier("B")))
                .typedEdge(new Triple(Name.identifier("1"), Name.identifier("ii"), Name.identifier("3")),
                        new Triple(Name.identifier("A"), Name.identifier("b"), Name.identifier("C")))
                .morphism("bd")
                .getResult(GraphMorphism.class);

        GraphMorphism cd = getContextCreatingBuilder()
                .domain(G_c)
                .codomain(G_D)
                .typedEdge(new Triple(Name.identifier("5"), Name.identifier("v"), Name.identifier("6")),
                        new Triple(Name.identifier("A"), Name.identifier("a"), Name.identifier("B")))
                .typedEdge(new Triple(Name.identifier("6"), Name.identifier("vi"), Name.identifier("7")),
                        new Triple(Name.identifier("B"), Name.identifier("c"), Name.identifier("C")))
                .morphism("cd")
                .getResult(GraphMorphism.class);

        Pair<GraphMorphism, GraphMorphism> result = bd.pullback(cd, Name.identifier("A"));

        assertGraphsEqual(result.getFirst().domain(),
                Triple.node(id("3").pair(id("7"))),
                Triple.node(id("1").pair(id("5"))),
                Triple.node(id("2").pair(id("6"))),
                new Triple(id("1").pair(id("5")),  id("i").pair(id("v")), id("2").pair(id("6"))));
    }


    @Test
    public void testPullbackWithCopying() throws GraphError {
        Graph typeGraph = getContextCreatingBuilder()
                .edge("C", "r", "C")
                .edge("C", "e", "C")
                .graph("TG")
                .getResult(Graph.class);

        Graph testGraph = getContextCreatingBuilder()
                .edge(Name.identifier("B"), Name.identifier("B").subTypeOf(Name.identifier("A")), Name.identifier("A"))
                .edge(Name.identifier("C"), Name.identifier("C").subTypeOf(Name.identifier("A")), Name.identifier("A"))
                .edge("A", "i", "int")
                .edge("B", "s", "String")
                .edge("A", "r1", "D")
                .edge("E", "r2", "A")
                .graph("G")
                .getResult(Graph.class);

        GraphMorphism typing = getContextCreatingBuilder()
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
                .getResult(GraphMorphism.class);

        Graph selectGraph = getContextCreatingBuilder()
                .edge(Name.identifier("1"), Name.identifier("2"), Name.identifier("3"))
                .graph("G")
                .getResult(Graph.class);


        GraphMorphism selection = getContextCreatingBuilder()
                .domain(selectGraph)
                .codomain(typeGraph)
                .map("1", "C")
                .map("2", "r")
                .map("3", "C")
                .morphism("s")
                .getResult(GraphMorphism.class);

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
        assertStreamEquals(typingAlongSelection.preimage(new Triple(id("1"), id("2"), id("3"))),
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
    public void testIsTotal() throws GraphError {
        Graph A = getContextCreatingBuilder()
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .graph("A")
                .getResult(Graph.class);

        Graph B = getContextCreatingBuilder()
                .edge("1", "c", "2")
                .graph("B")
                .getResult(Graph.class);

        GraphMorphism m1 = getContextCreatingBuilder()
                .domain(A)
                .codomain(B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism m2 = getContextCreatingBuilder()
                .domain(B)
                .codomain(A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertFalse(m1.isTotal());
        assertTrue(m2.isTotal());
    }

    @Test
    public void testIsInjective() throws GraphError {
        Graph A = getContextCreatingBuilder()
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .graph("A")
                .getResult(Graph.class);

        Graph B = getContextCreatingBuilder()
                .edge("1", "c", "2")
                .graph("B")
                .getResult(Graph.class);

        GraphMorphism m1 = getContextCreatingBuilder()
                .domain(A)
                .codomain(B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .map("b", "c")
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism m2 = getContextCreatingBuilder()
                .domain(B)
                .codomain(A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertTrue(m1.isTotal());
        assertFalse(m1.isInjective());

        assertTrue(m2.isInjective());
    }

    @Test
    public void testIsSurjective() throws GraphError {

        Graph A = getContextCreatingBuilder()
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .graph("A")
                .getResult(Graph.class);

        Graph B = getContextCreatingBuilder()
                .edge("1", "c", "2")
                .graph("B")
                .getResult(Graph.class);

        GraphMorphism m1 = getContextCreatingBuilder()
                .domain(A)
                .codomain(B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .map("b", "c")
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism m2 = getContextCreatingBuilder()
                .domain(B)
                .codomain(A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertTrue(m2.isTotal());
        assertTrue(m2.isInjective());
        assertFalse(m2.isSurjective());

        assertTrue(m1.isSurjective());
    }

    @Test
    public void testFlatten() throws GraphError {

        Graph typ = getContextCreatingBuilder()
                .edge("Class", "reference", "Class")
                .edge("Class", "attribute", "Type")
                .edge("Class", "extends", "Class")
                .graph("TYP")
                .getResult(Graph.class);

        Graph instance = getContextCreatingBuilder()
                .edge("B", "B.super", "A")
                .edge("C", "C.super", "A")
                .edge("A", "i", "int")
                .edge("B", "s", "String")
                .edge("A", "r", "D")
                .node("untyped")
                .graph("INSTANCE")
                .getResult(Graph.class);

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
                .getResult(GraphMorphism.class);

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
                .getResult(Graph.class);


            Graph actual = typing.flatten();

            assertFalse(typing.isTotal()); // There is an untyped element!
            assertFalse(actual.mentions(Name.identifier("untyped")));
            assertFalse(actual.mentions(Name.identifier("A")));
            assertTrue(actual.mentions(Name.identifier("A").typeBy(Name.identifier("Class"))));
            assertGraphsEqual(expected, actual);
    }


    @Test
    public void testUnflatten() throws GraphError {
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
                .getResult(Graph.class);


        Graph typ = getContextCreatingBuilder()
                .edge("Class", "reference", "Class")
                .edge("Class", "attribute", "Type")
                .edge("Class", "extends", "Class")
                .graph("TYP")
                .getResult(Graph.class);

        Graph instance = getContextCreatingBuilder()
                .edge("B", "B.super", "A")
                .edge("C", "C.super", "A")
                .edge("A", "i", "int")
                .edge("B", "s", "String")
                .edge("A", "r", "D")
                .graph("INSTANCE")
                .getResult(Graph.class);


        GraphMorphism expected = getContextCreatingBuilder()
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
                .getResult(GraphMorphism.class);

        assertMorphismsEqual(expected, GraphMorphism.unflatten(source, typ, id("id")));
    }



    @Test
    public void testWellFormedness() throws GraphError {
        Graph g1 = getContextCreatingBuilder()
                .edge("A", "a", "B")
                .graph("G1")
                .getResult(Graph.class);

        Graph g2 = getContextCreatingBuilder()
                .edge("B", "a", "A")
                .edge("B", "b", "C")
                .graph("G2")
                .getResult(Graph.class);

        // Violates hom-property
        GraphMorphism invalid1 = getErrorIgnoringBuilder()
                .domain(g1)
                .codomain(g2)
                .map("A", "A")
                .map("a", "a")
                .map("B", "B")
                .morphism("m")
                .getResult(GraphMorphism.class);

        assertFalse(invalid1.verify());
        addExpectedTriple(Triple.edge(Name.identifier("A"),Name.identifier("a"), Name.identifier("B")));
        assertStreamEquals(expected(), invalid1.homPropViolations());

        // maps to a non existing element
        GraphMorphism invalid2 = getErrorIgnoringBuilder()
                .domain(g1)
                .codomain(g2)
                .map("A", "B")
                .map("B", "A")
                .map("a", "f")
                .morphism("m")
                .getResult(GraphMorphism.class);

        assertFalse(invalid2.verify());
        addExpectedTriple(t("A","a","B"));
        assertStreamEquals(expected(), invalid2.mappedToUndefined());

        // Turns everything into a node
        GraphMorphism invalid3 = getErrorIgnoringBuilder()
                .domain(g1)
                .codomain(g2)
                .map("A", "A")
                .map("a", "A")
                .map("B", "A")
                .morphism("m")
                .getResult(GraphMorphism.class);

        assertFalse(invalid3.verify());
        addExpectedTriple(t("A","a","B"));
        assertStreamEquals(expected(), invalid3.homPropViolations());

        // Violates hom-property again
        GraphMorphism invalid4 = getErrorIgnoringBuilder()
                .domain(g1)
                .codomain(g2)
                .map("A", "A")
                .map("a", "b")
                .map("B", "C")
                .morphism("m")
                .getResult(GraphMorphism.class);

        assertFalse(invalid4.verify());
        addExpectedTriple(t("A","a","B"));
        assertStreamEquals(expected(), invalid4.homPropViolations());

        // And finally a valid one
        GraphMorphism valid = getErrorIgnoringBuilder()
                .domain(g1)
                .codomain(g2)
                .map("A", "B")
                .map("a", "a")
                .map("B", "A")
                .morphism("m")
                .getResult(GraphMorphism.class);
        assertTrue(valid.verify());
    }

    @Test
    public void testPartiality() throws GraphError {
        Graph g1 = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .edge("B", "g", "C")
                .graph("G1")
                .getResult(Graph.class);

        GraphMorphism partial = getErrorIgnoringBuilder()
                .domain(g1)
                .codomain(g1)
                .map("A", "A")
                .map("f", "f")
                .map("B", "B")
                .morphism("m")
                .getResult(GraphMorphism.class);

        assertFalse(partial.isTotal());
        assertTrue(partial.isInjective());
        assertFalse(partial.isSurjective());

        assertTrue(partial.definedAt(id("B")));
        assertFalse(partial.definedAt(id("C")));

        assertTrue(partial.definedAt(t("A", "f", "B")));
        assertFalse(partial.definedAt(t("B", "g", "C")));
        assertEquals(Optional.of(t("A","f","B")), partial.apply(t("A","f","B")));
        assertEquals(Optional.empty(), partial.apply(t("B", "g", "C")));

        addExpectedTriple(t("A","f","B"));
        assertStreamEquals(expected(), partial.preimage(t("A","f","B")));
        assertStreamEquals(Stream.empty(), partial.preimage(t("B","g","C")));
    }

    @Test
    public void testCompose() throws GraphError {
        Graph g1 = getContextCreatingBuilder()
                .node("A")
                .node("B")
                .node("C")
                .graph("G1")
                .getResult(Graph.class);

        Graph g2 = getContextCreatingBuilder()
                .node("1")
                .node("2")
                .node("3")
                .node("4")
                .graph("G2")
                .getResult(Graph.class);


        Graph g3 = getContextCreatingBuilder()
                .node("f")
                .node("g")
                .node("h")
                .graph("G2")
                .getResult(Graph.class);

        GraphMorphism m1 = getContextCreatingBuilder()
                .domain(g1)
                .codomain(g2)
                .map("A", "1")
                .map("B", "2")
                .map("C", "3")
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism m2 = getContextCreatingBuilder()
                .domain(g2)
                .codomain(g3)
                .map("1", "f")
                .map("2", "g")
                .map("3", "h")
                .map("4", "h")
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertTrue(m1.isTotal());
        assertTrue(m2.isTotal());

        assertFalse(m1.isSurjective());
        assertTrue(m1.isInjective());

        assertTrue(m2.isSurjective());
        assertFalse(m2.isInjective());

        GraphMorphism m1Thenm2 = m1.compose(m2);

        assertTrue(m1Thenm2.isTotal());
        assertTrue(m1Thenm2.isInjective());
        assertTrue(m1Thenm2.isSurjective());

        assertEquals(Optional.of(Triple.node(id("f"))), m1Thenm2.apply(Triple.node(id("A"))));

        addExpectedTriple(Triple.node(id("C")));
        assertStreamEquals(expected(), m1Thenm2.preimage(Triple.node(id("h"))));

        addExpectedTriple(Triple.node(id("3")));
        addExpectedTriple(Triple.node(id("4")));
        assertStreamEquals(expected(), m2.preimage(Triple.node(id("h"))));
    }

    @Test
    public void testPreImages() throws GraphError {

        Graph aLoop = getContextCreatingBuilder()
                .edge("A", "a", "A")
                .graph("ALoop")
                .getResult(Graph.class);

        Graph g = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .edge("B", "g", "C")
                .edge("C", "h", "D")
                .graph("G")
                .getResult(Graph.class);

        GraphMorphism morphism = getContextCreatingBuilder()
                .domain(g)
                .codomain(aLoop)
                .map("A", "A")
                .map("B", "A")
                .map("C", "A")
                .map("D", "A")
                .map("f", "a")
                .map("g", "a")
                .morphism("m")
                .getResult(GraphMorphism.class);

        addExpectedTriple(Triple.node(id("A")));
        addExpectedTriple(Triple.node(id("B")));
        addExpectedTriple(Triple.node(id("C")));
        addExpectedTriple(Triple.node(id("D")));
        assertStreamEquals(expected(), morphism.preimage(Triple.node(id("A"))));

        addExpectedTriple(t("A", "f", "B"));
        addExpectedTriple(t("B", "g", "C"));
        assertStreamEquals(expected(), morphism.preimage(t("A", "a", "A")));

    }

}
