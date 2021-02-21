package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import no.hvl.past.util.Pair;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class MorphismTest extends AbstractGraphTest {

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
        assertFalse(m1.isMonic());

        assertTrue(m2.isMonic());
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
        assertTrue(m2.isMonic());
        assertFalse(m2.isEpic());

        assertTrue(m1.isEpic());
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
        assertTrue(partial.isMonic());
        assertFalse(partial.isEpic());

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

        assertFalse(m1.isEpic());
        assertTrue(m1.isMonic());

        assertTrue(m2.isEpic());
        assertFalse(m2.isMonic());

        GraphMorphism m1Thenm2 = m1.compose(m2);

        assertTrue(m1Thenm2.isTotal());
        assertTrue(m1Thenm2.isMonic());
        assertTrue(m1Thenm2.isEpic());

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

    // Pullbacks


    @Test
    public void testSetTheoreticPullback() throws GraphError {
        Graph B = getContextCreatingBuilder()
                .node("a")
                .node("b")
                .node("c")
                .node("d")
                .graph("B")
                .getResult(Graph.class);

        Graph C = getContextCreatingBuilder()
                .node("i")
                .node("ii")
                .node("iii")
                .node("iv")
                .graph("C")
                .getResult(Graph.class);

        Graph D = getContextCreatingBuilder()
                .node("1")
                .node("2")
                .node("3")
                .node("4")
                .graph("D")
                .getResult(Graph.class);

        GraphMorphism bd = getContextCreatingBuilder()
                .domain(B)
                .codomain(D)
                .map("a", "3")
                .map("b", "3")
                .map("c", "2")
                .map("d", "1")
                .morphism("bc")
                .getResult(GraphMorphism.class);

        GraphMorphism cd = getContextCreatingBuilder()
                .domain(C)
                .codomain(D)
                .map("i", "3")
                .map("ii", "2")
                .map("iii", "4")
                .map("iv", "4")
                .morphism("cd")
                .getResult(GraphMorphism.class);

        assertFalse(bd.isMonic());
        assertFalse(cd.isMonic());

        Pair<GraphMorphism, GraphMorphism> result = bd.pullback(cd);
        // Checking domains and codomains
        assertEquals(bd.domain(), result.getLeft().codomain());
        assertEquals(cd.domain(), result.getRight().codomain());
        assertEquals(result.getLeft().domain(), result.getRight().domain());
        // Checking elements
        addExpectedTriple(Triple.node(Name.identifier("a").pair(Name.identifier("i"))));
        addExpectedTriple(Triple.node(Name.identifier("b").pair(Name.identifier("i"))));
        addExpectedTriple(Triple.node(Name.identifier("c").pair(Name.identifier("ii"))));
        assertStreamEquals(expected(), result.getFirst().domain().elements());

    }


    @Test
    public void testPullbackOfRealGraph() throws GraphError {
        Graph G_B = getContextCreatingBuilder()
                .edge("1", "f", "3")
                .edge("2", "g", "3")
                .graph("G_B")
                .getResult(Graph.class);

        Graph G_c = getContextCreatingBuilder()
                .edge("a", "h", "a")
                .edge("a", "k", "b")
                .edge("c","l", "c")
                .graph("G_C")
                .getResult(Graph.class);

        Graph G_D = getContextCreatingBuilder()
                .edge("T", "t", "T")
                .node("I")
                .graph("G_D")
                .getResult(Graph.class);

        GraphMorphism bd = getContextCreatingBuilder()
                .domain(G_B)
                .codomain(G_D)
                .map("1", "T")
                .map("2", "T")
                .map("3", "T")
                .map("f", "t")
                .map("g", "t")
                .morphism("bd")
                .getResult(GraphMorphism.class);

        GraphMorphism cd = getContextCreatingBuilder()
                .domain(G_c)
                .codomain(G_D)
                .map("a", "T")
                .map("b", "T")
                .map("c", "I")
                .map("h", "t")
                .map("k", "t")
                .morphism("cd")

                .getResult(GraphMorphism.class);

        Pair<GraphMorphism, GraphMorphism> result = bd.pullback(cd);

        addExpectedTriple(Triple.node(Name.identifier("1").pair(Name.identifier("a"))));
        addExpectedTriple(Triple.node(Name.identifier("1").pair(Name.identifier("b"))));
        addExpectedTriple(Triple.node(Name.identifier("2").pair(Name.identifier("a"))));
        addExpectedTriple(Triple.node(Name.identifier("2").pair(Name.identifier("b"))));
        addExpectedTriple(Triple.node(Name.identifier("3").pair(Name.identifier("a"))));
        addExpectedTriple(Triple.node(Name.identifier("3").pair(Name.identifier("b"))));
        addExpectedTriple(Triple.edge(
                Name.identifier("1").pair(Name.identifier("a")),
                Name.identifier("f").pair(Name.identifier("h")),
                Name.identifier("3").pair(Name.identifier("a"))
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("2").pair(Name.identifier("a")),
                Name.identifier("g").pair(Name.identifier("h")),
                Name.identifier("3").pair(Name.identifier("a"))
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("1").pair(Name.identifier("a")),
                Name.identifier("f").pair(Name.identifier("k")),
                Name.identifier("3").pair(Name.identifier("b"))
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("2").pair(Name.identifier("a")),
                Name.identifier("g").pair(Name.identifier("k")),
                Name.identifier("3").pair(Name.identifier("b"))
        ));

        assertStreamEquals(expected(), result.getFirst().domain().elements());
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

        Pair<GraphMorphism,GraphMorphism> pullback = selection.pullback(typing);
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
    public void testPreimagePullback() throws GraphError {
        Graph g1 = getContextCreatingBuilder()
                .edge("A", "a", "B")
                .edge("A'", "a'", "B")
                .edge("B", "b", "C")
                .edge("B", "b'", "C'")
                .graph("G_1")
                .getResult(Graph.class);

        Graph g0 = getContextCreatingBuilder()
                .edge("1", "12", "2")
                .edge("2", "22", "2")
                .graph("G_0")
                .getResult(Graph.class);

        Graph g0Sub1 = getContextCreatingBuilder()
                .edge("X", "xy", "Y")
                .graph("G_0Sub1")
                .getResult(Graph.class);

        Graph g0Sub2 = getContextCreatingBuilder()
                .edge("Y", "yy", "Y")
                .graph("G_0Sub2")
                .getResult(Graph.class);

        GraphMorphism m1 = getContextCreatingBuilder()
                .domain(g1)
                .codomain(g0)
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

        GraphMorphism m0Sub1 = getContextCreatingBuilder()
                .domain(g0Sub1)
                .codomain(g0)
                .map("X", "1")
                .map("xy", "12")
                .map("Y", "2")
                .morphism("m0Sub1")
                .getResult(GraphMorphism.class);

        GraphMorphism m0Sub2 = getContextCreatingBuilder()
                .domain(g0Sub2)
                .codomain(g0)
                .map("Y", "2")
                .map("yy", "22")
                .map("Y", "2")
                .morphism("m0Sub2")
                .getResult(GraphMorphism.class);

        assertTrue(m0Sub1.isMonic());
        assertTrue(m0Sub2.isMonic());



        Pair<GraphMorphism, GraphMorphism> result1 = m0Sub1.pullback(m1);
        addExpectedTriple(Triple.node(Name.identifier("A")));
        addExpectedTriple(Triple.node(Name.identifier("A'")));
        addExpectedTriple(Triple.node(Name.identifier("B")));
        addExpectedTriple(Triple.node(Name.identifier("C")));
        addExpectedTriple(Triple.node(Name.identifier("C'")));
        addExpectedTriple(Triple.edge(
                Name.identifier("A"),
                Name.identifier("a"),
                Name.identifier("B")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("A'"),
                Name.identifier("a'"),
                Name.identifier("B")
        ));
        assertStreamEquals(expected(), result1.getRight().domain().elements());

        addExpectedTriple(Triple.node(Name.identifier("A")));
        addExpectedTriple(Triple.node(Name.identifier("A'")));

        assertStreamEquals(expected(), result1.getLeft().allInstances(Triple.node(Name.identifier("X"))));

        addExpectedTriple(Triple.edge(
                Name.identifier("A"),
                Name.identifier("a"),
                Name.identifier("B")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("A'"),
                Name.identifier("a'"),
                Name.identifier("B")
        ));

        assertStreamEquals(expected(), result1.getLeft().allInstances(Triple.edge(
                Name.identifier("X"),
                Name.identifier("xy"),
                Name.identifier("Y")
        )));


        // other way round

        Pair<GraphMorphism, GraphMorphism> result2 = m1.pullback(m0Sub2);
        addExpectedTriple(Triple.node(Name.identifier("B")));
        addExpectedTriple(Triple.node(Name.identifier("C'")));
        addExpectedTriple(Triple.node(Name.identifier("C")));
        addExpectedTriple(Triple.edge(
                Name.identifier("B"),
                Name.identifier("b"),
                Name.identifier("C")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("B"),
                Name.identifier("b'"),
                Name.identifier("C'")
        ));
        assertStreamEquals(expected(), result2.getLeft().domain().elements());


    }

    @Test
    public void testGluingTest() throws GraphError {
        Graph L = getContextCreatingBuilder()
                .node("1")
                .graph("L")
                .getResult(Graph.class);

        Graph R = getContextCreatingBuilder()
                .edge("1", "12", "2")
                .graph("R")
                .getResult(Graph.class);

        Graph G = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .graph("G")
                .getResult(Graph.class);

        GraphMorphism r = getContextCreatingBuilder()
                .domain(L)
                .codomain(R)
                .map("1", "1")
                .morphism("r")
                .getResult(GraphMorphism.class);

        GraphMorphism m = getContextCreatingBuilder()
                .domain(L)
                .codomain(G)
                .map("1", "A")
                .morphism("m")
                .getResult(GraphMorphism.class);

        Pair<GraphMorphism, GraphMorphism> result = m.pushout(r);
        assertEquals(m.codomain(), result.getLeft().domain());
        assertEquals(r.codomain(), result.getRight().domain());
        assertEquals(result.getLeft().codomain(), result.getRight().codomain());

        addExpectedTriple(Triple.node(Name.identifier("A")));
        addExpectedTriple(Triple.node(Name.identifier("B")));
        addExpectedTriple(Triple.node(Name.identifier("2").prefixWith(Name.identifier("R"))));
        addExpectedTriple(Triple.edge(
                Name.identifier("A"),
                Name.identifier("f"),
                Name.identifier("B")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("A"),
                Name.identifier("12").prefixWith(Name.identifier("R")),
                Name.identifier("2").prefixWith(Name.identifier("R"))
        ));
        assertStreamEquals(expected(), result.getLeft().codomain().elements());

        // since both are monic, we can also do it the other way round then names are slightly different
        Pair<GraphMorphism, GraphMorphism> result2 = r.pushout(m);
        addExpectedTriple(Triple.node(Name.identifier("1")));
        addExpectedTriple(Triple.node(Name.identifier("2")));
        addExpectedTriple(Triple.node(Name.identifier("B").prefixWith(Name.identifier("G"))));
        addExpectedTriple(Triple.edge(
                Name.identifier("1"),
                Name.identifier("f").prefixWith(Name.identifier("G")),
                Name.identifier("B").prefixWith(Name.identifier("G"))
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("1"),
                Name.identifier("12"),
                Name.identifier("2")
        ));

        assertStreamEquals(expected(), result2.getLeft().codomain().elements());
    }

    @Test
    public void testPushoutWithClustering() throws GraphError {
        Graph A = getContextCreatingBuilder()
                .node("x")
                .node("y")
                .node("z")
                .node("q")
                .graph("G_0")
                .getResult(Graph.class);

        Graph B = getContextCreatingBuilder()
                .node("A")
                .node("B")
                .node("C")
                .node("D")
                .graph("G_1")
                .getResult(Graph.class);

        Graph C = getContextCreatingBuilder()
                .node("1")
                .node("2")
                .node("3")
                .node("4")
                .graph("G_2")
                .getResult(Graph.class);

        GraphMorphism f = getContextCreatingBuilder()
                .domain(A)
                .codomain(B)
                .map("x", "C")
                .map("y", "B")
                .map("z", "A")
                .map("q", "B")
                .morphism("m_1")
                .getResult(GraphMorphism.class);

        GraphMorphism g = getContextCreatingBuilder()
                .domain(A)
                .codomain(C)
                .map("x", "3")
                .map("y", "3")
                .map("z", "1")
                .map("q", "2")
                .morphism("m_2")
                .getResult(GraphMorphism.class);

        Pair<GraphMorphism, GraphMorphism> pushout = f.pushout(g);
        addExpectedTriple(Triple.node(Name.identifier("4").prefixWith(Name.identifier("G_2"))));
        addExpectedTriple(Triple.node(Name.identifier("D").prefixWith(Name.identifier("G_1"))));
        addExpectedTriple(Triple.node(Name.identifier("A").prefixWith(Name.identifier("G_1")).mergeWith(
                Name.identifier("z").prefixWith(Name.identifier("G_0")),
                Name.identifier("1").prefixWith(Name.identifier("G_2")))));
        addExpectedTriple(Triple.node(Name.identifier("3").prefixWith(Name.identifier("G_2")).mergeWith(
                Name.identifier("2").prefixWith(Name.identifier("G_2")),
                Name.identifier("q").prefixWith(Name.identifier("G_0")),
                Name.identifier("x").prefixWith(Name.identifier("G_0")),
                Name.identifier("y").prefixWith(Name.identifier("G_0")),
                Name.identifier("B").prefixWith(Name.identifier("G_1")),
                Name.identifier("C").prefixWith(Name.identifier("G_1"))
        )));
        assertStreamEquals(expected(), pushout.getLeft().codomain().elements());
    }

    @Test
    public void testRuleApp() throws GraphError {
        Graph L = getContextCreatingBuilder()
                .edge("1", "11", "1")
                .node("2")
                .graph("L")
                .getResult(Graph.class);
        Graph R = getContextCreatingBuilder()
                .edge("1", "11", "1")
                .edge("1", "12", "2")
                .graph("R")
                .getResult(Graph.class);
        Graph G = getContextCreatingBuilder()
                .edge("A", "this_A", "A")
                .edge("B", "this_B", "B")
                .edge("A", "f", "B")
                .graph("G")
                .getResult(Graph.class);
        GraphMorphism r = getContextCreatingBuilder()
                .domain(L)
                .codomain(R)
                .map("1", "1")
                .map("2", "2")
                .map("11", "11")
                .morphism("r")
                .getResult(GraphMorphism.class);
        GraphMorphism m = getContextCreatingBuilder()
                .domain(L)
                .codomain(G)
                .map("1", "A")
                .map("11", "this_A")
                .map("2", "A")
                .morphism("m")
                .getResult(GraphMorphism.class);

        Pair<GraphMorphism, GraphMorphism> pushout = m.pushout(r);
        addExpectedTriple(Triple.node(Name.identifier("A")));
        addExpectedTriple(Triple.node(Name.identifier("B")));
        addExpectedTriple(Triple.edge(
                Name.identifier("B"),
                Name.identifier("this_B"),
                Name.identifier("B")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("A"),
                Name.identifier("this_A"),
                Name.identifier("A")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("A"),
                Name.identifier("f"),
                Name.identifier("B")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("A"),
                Name.identifier("12").prefixWith(Name.identifier("R")),
                Name.identifier("A")
        ));
        assertStreamEquals(expected(), pushout.getLeft().codomain().elements());
    }

}
