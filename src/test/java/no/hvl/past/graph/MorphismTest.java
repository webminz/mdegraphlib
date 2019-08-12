package no.hvl.past.graph;

import no.hvl.past.graph.names.Merge;
import no.hvl.past.graph.names.Name;
import no.hvl.past.graph.names.PrintingStrategy;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class MorphismTest {

    /**
     * A naming strategy used throughout the test. Ignores prefixes and concatenates names.
     * Uses some standard symbols for the individual operations.
     */
    NamingStrategy NAMING_STRATEGY = NamingStrategy.concatenate(new PrintingStrategy() {
        @Override
        public String empty() {
            return "";
        }

        @Override
        public String sequentialComposition(String fst, String snd) {
            return fst + ";" + snd;
        }

        @Override
        public String coproduct(String fst, String snd) {
            return fst + "+" + snd;
        }

        @Override
        public String pullback(String applicant, String target) {
            return applicant + "*(" + target + ")";
        }


        @Override
        public String merge(Collection<String> transformedNames) {
            StringBuilder result = new StringBuilder();
            result.append('(');
            Iterator<String> iterator = transformedNames.iterator();
            while (iterator.hasNext()) {
                result.append(iterator.next());
                if (iterator.hasNext()) {
                    result.append(", ");
                }
            }
            result.append(')');
            return result.toString();
        }

        @Override
        public String transform(Name n, String prefix) {
            return n.toString();
        }

        @Override
        public String typedBy(String element, String type) {
            return element + " : " + type;
        }
    });


    @Test
    public void testSetTheoreticPullback() {

        Graph B = new Graph.Builder("B")
                .node("1")
                .node("2")
                .node("3")
                .node("4")
                .build();

        Graph C = new Graph.Builder("C")
                .node("5")
                .node("6")
                .node("7")
                .build();

        Graph D = new Graph.Builder("D")
                .node("a")
                .node("b")
                .node("c")
                .node("d")
                .build();



        Morphism bd = new Morphism.Builder(Name.identifier("bd"), B, D)
                .map("1", "a")
                .map("2", "a")
                .map("3", "b")
                .map("4", "c")
                .build();

        Morphism cd = new Morphism.Builder(Name.identifier("cd"), C, D)
                .map("5", "a")
                .map("6", "b")
                .map("7", "d")
                .build();

        Set<Name> expected = new HashSet<>();
        expected.add(Name.identifier("(3, 6)"));
        expected.add(Name.identifier("(1, 5)"));
        expected.add(Name.identifier("(2, 5)"));
        Multispan result = bd.pullback(Name.identifier("pb"),cd, Name.identifier("A"), NAMING_STRATEGY);

        assertEquals(expected, StreamSupport.stream(result.getApex().spliterator(), false).map(Triple::getLabel).collect(Collectors.toSet()));
    }

    @Test
    public void testPullbackOfRealGraph() {


        Graph G_B = new Graph.Builder("G_B")
                .edge("1", "i", "2")
                .edge("1", "ii", "3")
                .build();

        Graph G_c = new Graph.Builder("G_C")
                .edge("5", "v", "6")
                .edge("6", "vi", "7")
                .build();

        Graph G_D = new Graph.Builder("G_D")
                .edge("A", "a", "B")
                .edge("A", "b", "C")
                .edge("B", "c", "C")
                .build();

        Morphism bd = new Morphism.Builder(Name.identifier("bd"), G_B, G_D)
                .map(new Triple(Name.identifier("1"), Name.identifier("i"), Name.identifier("2")),
                        new Triple(Name.identifier("A"), Name.identifier("a"), Name.identifier("B")))
                .map(new Triple(Name.identifier("1"), Name.identifier("ii"), Name.identifier("3")),
                        new Triple(Name.identifier("A"), Name.identifier("b"), Name.identifier("C")))
                .build();

        Morphism cd = new Morphism.Builder(Name.identifier("cd"), G_c, G_D)
                .map(new Triple(Name.identifier("5"), Name.identifier("v"), Name.identifier("6")),
                        new Triple(Name.identifier("A"), Name.identifier("a"), Name.identifier("B")))
                .map(new Triple(Name.identifier("6"), Name.identifier("vi"), Name.identifier("7")),
                        new Triple(Name.identifier("B"), Name.identifier("c"), Name.identifier("C")))
                .build();

        Multispan result = bd.pullback(Name.identifier("pb"), cd, Name.identifier("A"), NAMING_STRATEGY);


        Set<Triple> expected = new HashSet<>();
        expected.add(Triple.fromNode(Name.identifier("(3, 7)")));
        expected.add(Triple.fromNode(Name.identifier("(1, 5)")));
        expected.add(Triple.fromNode(Name.identifier("(2, 6)")));
        expected.add(new Triple(Name.identifier("(1, 5)"),
                Name.identifier("(i, v)"),
                Name.identifier("(2, 6)")));

        assertEquals(expected, StreamSupport.stream(result.getApex().spliterator(),false).collect(Collectors.toSet()));
    }

    @Test
    public void testIsTotal() {
        Graph A = new Graph.Builder("A")
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .build();

        Graph B = new Graph.Builder("B")
                .edge("1", "c", "2")
                .build();

        Morphism m1 = new Morphism.Builder("m1", A, B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .build();

        Morphism m2 = new Morphism.Builder("m2", B, A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .build();

        assertFalse(m1.isTotal());
        assertTrue(m2.isTotal());
    }

    @Test
    public void testIsInjective() {
        Graph A = new Graph.Builder("A")
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .build();

        Graph B = new Graph.Builder("B")
                .edge("1", "c", "2")
                .build();

        Morphism m1 = new Morphism.Builder("m1", A, B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .map("b", "c")
                .build();

        Morphism m2 = new Morphism.Builder("m2", B, A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .build();

        assertTrue(m1.isTotal());
        assertFalse(m1.isInjective());

        assertTrue(m2.isInjective());
    }

    @Test
    public void testIsSurjective() {

        Graph A = new Graph.Builder("A")
                .edge("1", "a", "2")
                .edge("1", "b", "2")
                .build();

        Graph B = new Graph.Builder("B")
                .edge("1", "c", "2")
                .build();

        Morphism m1 = new Morphism.Builder("m1", A, B)
                .map("1", "1")
                .map("2", "2")
                .map("a", "c")
                .map("b", "c")
                .build();

        Morphism m2 = new Morphism.Builder("m2", B, A)
                .map("1", "1")
                .map("2", "2")
                .map("c", "a")
                .build();

        assertTrue(m2.isTotal());
        assertTrue(m2.isInjective());
        assertFalse(m2.isSurjective());

        assertTrue(m1.isSurjective());
    }

    @Test
    public void testToTypedGraph() {

        Graph typ = new Graph.Builder("TYP")
                .edge("Class", "reference", "Class")
                .edge("Class", "attribute", "Type")
                .edge("Class", "extends", "Class")
                .build();

        Graph instance = new Graph.Builder("INSTANCE")
                .edge("B", "B.super", "A")
                .edge("C", "C.super", "A")
                .edge("A", "i", "int")
                .edge("B","s", "String")
                .edge("A", "r", "D")
                .build();

        Morphism typing = new Morphism.Builder("t", instance, typ)
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
                .build();

        Graph expected = new Graph.Builder(Name.identifier("INSTANCE").typeBy(Name.identifier("TYP")))
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
                .build();
//        Graph actual = typing.toTypedGraph();
//        assertEquals(expected, actual);
//        assertEquals(expected.getElements(), actual.getElements());

    }


    @Test
    public void testFromTypedGraph() {
        Graph source = new Graph.Builder(Name.identifier("INSTANCE").typeBy(Name.identifier("TYP")))
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
                .build();


        Graph typ = new Graph.Builder("TYP")
                .edge("Class", "reference", "Class")
                .edge("Class", "attribute", "Type")
                .edge("Class", "extends", "Class")
                .build();

        Graph instance = new Graph.Builder("INSTANCE")
                .edge("B", "B.super", "A")
                .edge("C", "C.super", "A")
                .edge("A", "i", "int")
                .edge("B","s", "String")
                .edge("A", "r", "D")
                .build();


        Morphism expected = new Morphism.Builder("t", instance, typ)
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
                .build();

        assertEquals(
                StreamSupport.stream(expected.spliterator(), false).collect(Collectors.toSet()),
                StreamSupport.stream(Morphism.fromTypedGraph(Name.identifier("t"), typ, source).spliterator(), false).collect(Collectors.toSet()));
    }

    @Test
    public void testMatching() {

    }

    @Test
    public void testViolationHomProp() {
        Graph g1 = new Graph.Builder("G1")
                .edge("A", "a", "B")
                .build();

        Graph g2 = new Graph.Builder("G2")
                .edge("B", "a", "A")
                .build();

        HashSet<Tuple> mapping = new HashSet<>(Arrays.asList(
                new Tuple(Name.identifier("A"), Name.identifier("A")),
                new Tuple(Name.identifier("a"), Name.identifier("a")),
                new Tuple(Name.identifier("B"), Name.identifier("B"))));

        try {
            Morphism.create(Name.identifier("m"), g1, g2, mapping);
            fail();
        } catch (GraphError error) {
            // yes
        }
    }




}
