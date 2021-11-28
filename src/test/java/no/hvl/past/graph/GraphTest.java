package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.ProperComparator;
import no.hvl.past.util.ShouldNotHappenException;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;

import static org.junit.Assert.*;

public class GraphTest extends TestWithGraphLib {


    Graph GRAPH_BASE = buildGraphBase();

    private Graph buildGraphBase()  {
        try {
            return getContextCreatingBuilder()
                    .edge("E", "src", "V")
                    .edge("E", "tgt", "V")
                    .graph("G")
                    .getResult(Graph.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(GraphTest.class, error.getMessage());
        }

    }

    Graph BIGGER = buildBiggerExample();

    private Graph buildBiggerExample()  {
        try {
            return getContextCreatingBuilder()
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
                    .graph("Example")
                    .getResult(Graph.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(GraphTest.class, error.getMessage());
        }

    }


    @Test
    public void testSum() {
        Graph sum = Universe.ONE_NODE.sum(Universe.ARROW);
        addExpectedTriple(Triple.node(Name.identifier("0").prefixWith(Name.identifier("NODE"))));
        addExpectedTriple(Triple.node(Name.identifier("0").prefixWith(Name.identifier("2"))));
        addExpectedTriple(Triple.node(Name.identifier("1").prefixWith(Name.identifier("2"))));
        addExpectedTriple(new Triple(Name.identifier("0"), Name.identifier("01"), Name.identifier("1")).prefix(Name.identifier("2")));
        assertStreamEquals(expected(), sum.elements());
    }

    @Test
    public void testIdentity() {
        GraphMorphism identity = GRAPH_BASE.identity();
        assertTrue(identity.isTotal());
        assertTrue(identity.isMonic());
        assertTrue(identity.isEpic());
        assertEquals(Optional.of(new Triple(Name.identifier("E"), Name.identifier("src"), Name.identifier("V"))), identity.apply(new Triple(Name.identifier("E"), Name.identifier("src"), Name.identifier("V"))));
        assertEquals(Optional.empty(), identity.apply(new Triple(Name.identifier("V"), Name.identifier("src"), Name.identifier("E"))));
        addExpectedTriple(new Triple(Name.identifier("E"), Name.identifier("src"), Name.identifier("V")));
        assertStreamEquals(expected(), identity.preimage(new Triple(Name.identifier("E"), Name.identifier("src"), Name.identifier("V"))));
    }


    @Test
    public void testTraversing() {
        assertTrue(BIGGER.existsPath(Name.identifier("1"), Name.identifier("1")));
        assertTrue(BIGGER.existsPath(Name.identifier("2"), Name.identifier("2")));
        assertTrue(BIGGER.existsPath(Name.identifier("1"), Name.identifier("2")));
        assertTrue(BIGGER.existsPath(Name.identifier("2"), Name.identifier("1")));
        assertTrue(BIGGER.existsPath(Name.identifier("3"), Name.identifier("1")));
        assertFalse(BIGGER.existsPath(Name.identifier("1"), Name.identifier("3")));
        assertTrue(BIGGER.existsPath(Name.identifier("1"), Name.identifier("7")));
        assertFalse(BIGGER.existsPath(Name.identifier("1"), Name.identifier("8")));
    }

    @Test
    public void testHasseDiagram() {
        List<Integer> elemnts = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        ProperComparator<Integer> comparator = new ProperComparator<Integer>() {
            @Override
            public CompareResult cmp(Integer o1, Integer o2) {
                if (o1.equals(o2)) {
                    return CompareResult.EQUAL;
                }
                if (o2 % o1 == 0) {
                    return CompareResult.LESS_THAN;
                }
                if (o1 % o2 == 0) {
                    return CompareResult.BIGGER_THAN;
                }
                return CompareResult.INCOMPARABLE;
            }
        };
        Function<Integer, Name> nameGiver = (i) -> Name.identifier(Integer.toString(i));
        GraphImpl result = GraphImpl.hasseDiagramm(Name.identifier("Dividers 12"), elemnts, comparator, nameGiver, false);

        Set<Triple> expected = new HashSet<>();
        expected.add(new Triple(Name.identifier("1"), Name.identifier("1").subTypeOf(Name.identifier("2")), Name.identifier("2")));
        expected.add(new Triple(Name.identifier("1"), Name.identifier("1").subTypeOf(Name.identifier("3")), Name.identifier("3")));
        expected.add(new Triple(Name.identifier("1"), Name.identifier("1").subTypeOf(Name.identifier("5")), Name.identifier("5")));
        expected.add(new Triple(Name.identifier("1"), Name.identifier("1").subTypeOf(Name.identifier("7")), Name.identifier("7")));
        expected.add(new Triple(Name.identifier("1"), Name.identifier("1").subTypeOf(Name.identifier("11")), Name.identifier("11")));
        expected.add(new Triple(Name.identifier("2"), Name.identifier("2").subTypeOf(Name.identifier("4")), Name.identifier("4")));
        expected.add(new Triple(Name.identifier("2"), Name.identifier("2").subTypeOf(Name.identifier("6")), Name.identifier("6")));
        expected.add(new Triple(Name.identifier("2"), Name.identifier("2").subTypeOf(Name.identifier("10")), Name.identifier("10")));
        expected.add(new Triple(Name.identifier("3"), Name.identifier("3").subTypeOf(Name.identifier("6")), Name.identifier("6")));
        expected.add(new Triple(Name.identifier("3"), Name.identifier("3").subTypeOf(Name.identifier("9")), Name.identifier("9")));
        expected.add(new Triple(Name.identifier("4"), Name.identifier("4").subTypeOf(Name.identifier("8")), Name.identifier("8")));
        expected.add(new Triple(Name.identifier("4"), Name.identifier("4").subTypeOf(Name.identifier("12")), Name.identifier("12")));
        expected.add(new Triple(Name.identifier("6"), Name.identifier("6").subTypeOf(Name.identifier("12")), Name.identifier("12")));
        expected.add(new Triple(Name.identifier("5"), Name.identifier("5").subTypeOf(Name.identifier("10")), Name.identifier("10")));

        assertEquals(expected, result.getEdges());

        Collection<String> elements2 = Arrays.asList("a", "aa", "ab");
        Function<String, Name> namer = Name::identifier;
        GraphImpl result2 = GraphImpl.hasseDiagramm(Name.identifier(""), elements2, namer, true);
        Set<Triple> expected2 = new HashSet<>();
        expected2.add(new Triple(namer.apply("a"), namer.apply("a").subTypeOf(namer.apply("aa")), namer.apply("aa")));
        expected2.add(new Triple(namer.apply("a"), namer.apply("a").subTypeOf(namer.apply("ab")), namer.apply("ab")));
        expected2.add(new Triple(namer.apply("aa"), namer.apply("aa").subTypeOf(namer.apply("ab")), namer.apply("ab")));
        assertEquals(expected2, result2.getEdges());
    }

    @Test
    public void testContainment() {
        assertTrue(ExampleGraphs.PATIENTS_GRAPH.contains(new Triple(Name.identifier("Patient"), Name.identifier("assigned"), Name.identifier("Bed"))));
        assertFalse(ExampleGraphs.PATIENTS_GRAPH.contains(new Triple(Name.identifier("Bed"), Name.identifier("assigned"), Name.identifier("Patient"))));
        assertFalse(ExampleGraphs.PATIENTS_GRAPH.contains(new Triple(Name.identifier("A"), Name.identifier("foo"), Name.identifier("Bar"))));
        assertTrue(ExampleGraphs.PATIENTS_GRAPH.mentions(Name.identifier("Patient")));
        assertTrue(ExampleGraphs.PATIENTS_GRAPH.mentions(Name.identifier("of")));
        assertFalse(ExampleGraphs.PATIENTS_GRAPH.mentions(Name.identifier("Doctor")));
        assertTrue(ExampleGraphs.PATIENTS_GRAPH.isNode(Name.identifier("Observation")));
        assertFalse(ExampleGraphs.PATIENTS_GRAPH.isNode(Name.identifier("of")));
        assertFalse(ExampleGraphs.PATIENTS_GRAPH.isNode(Name.identifier("Doctor")));

        assertEquals(ExampleGraphs.PATIENTS_GRAPH.get(Name.identifier("Patient")), Optional.of(Triple.node(Name.identifier("Patient"))));
        assertEquals(ExampleGraphs.PATIENTS_GRAPH.get(Name.identifier("Doctor")), Optional.empty());
        assertEquals(ExampleGraphs.PATIENTS_GRAPH.get(Name.identifier("assigned")), Optional.of(new Triple(Name.identifier("Patient"), Name.identifier("assigned"), Name.identifier("Bed"))));

    }


    @Test
    public void testCartesianProduct() throws GraphError {
        Graph B = getContextCreatingBuilder()
                .edge("N", "id_N", "N")
                .edge("E", "id_E", "E")
                .edge("E", "src", "N")
                .edge("E", "trg", "N")
                .graph("B")
                .getResult(Graph.class);

        Graph I = getContextCreatingBuilder()
                .edge("0", "id_0", "0")
                .edge("1", "id_1", "1")
                .edge("2", "id_2", "2")
                .edge("3", "id_3", "3")
                .edge("0", "pi_1", "1")
                .edge("0", "pi_2", "2")
                .edge("0", "pi_3", "3")
                .graph("I")
                .getResult(Graph.class);

        Graph result = B.cartesianProduct(I);


        Graph expected = getContextCreatingBuilder()
                .edge(id("N").pair(id("0")), id("id_N").pair(id("id_0")), id("N").pair(id("0")))
                .edge(id("N").pair(id("1")), id("id_N").pair(id("id_1")), id("N").pair(id("1")))
                .edge(id("N").pair(id("2")), id("id_N").pair(id("id_2")), id("N").pair(id("2")))
                .edge(id("N").pair(id("3")), id("id_N").pair(id("id_3")), id("N").pair(id("3")))
                .edge(id("E").pair(id("0")), id("id_E").pair(id("id_0")), id("E").pair(id("0")))
                .edge(id("E").pair(id("1")), id("id_E").pair(id("id_1")), id("E").pair(id("1")))
                .edge(id("E").pair(id("2")), id("id_E").pair(id("id_2")), id("E").pair(id("2")))
                .edge(id("E").pair(id("3")), id("id_E").pair(id("id_3")), id("E").pair(id("3")))
                .edge(id("E").pair(id("0")), id("src").pair(id("id_0")), id("N").pair(id("0")))
                .edge(id("E").pair(id("0")), id("trg").pair(id("id_0")), id("N").pair(id("0")))
                .edge(id("E").pair(id("1")), id("src").pair(id("id_1")), id("N").pair(id("1")))
                .edge(id("E").pair(id("1")), id("trg").pair(id("id_1")), id("N").pair(id("1")))
                .edge(id("E").pair(id("2")), id("src").pair(id("id_2")), id("N").pair(id("2")))
                .edge(id("E").pair(id("2")), id("trg").pair(id("id_2")), id("N").pair(id("2")))
                .edge(id("E").pair(id("3")), id("src").pair(id("id_3")), id("N").pair(id("3")))
                .edge(id("E").pair(id("3")), id("trg").pair(id("id_3")), id("N").pair(id("3")))
                .edge(id("N").pair(id("0")), id("id_N").pair(id("pi_1")), id("N").pair(id("1")))
                .edge(id("E").pair(id("0")), id("id_E").pair(id("pi_1")), id("E").pair(id("1")))
                .edge(id("N").pair(id("0")), id("id_N").pair(id("pi_2")), id("N").pair(id("2")))
                .edge(id("E").pair(id("0")), id("id_E").pair(id("pi_2")), id("E").pair(id("2")))
                .edge(id("N").pair(id("0")), id("id_N").pair(id("pi_3")), id("N").pair(id("3")))
                .edge(id("E").pair(id("0")), id("id_E").pair(id("pi_3")), id("E").pair(id("3")))
                .edge(id("E").pair(id("0")), id("src").pair(id("pi_1")), id("N").pair(id("1")))
                .edge(id("E").pair(id("0")), id("trg").pair(id("pi_1")), id("N").pair(id("1")))
                .edge(id("E").pair(id("0")), id("src").pair(id("pi_2")), id("N").pair(id("2")))
                .edge(id("E").pair(id("0")), id("trg").pair(id("pi_2")), id("N").pair(id("2")))
                .edge(id("E").pair(id("0")), id("src").pair(id("pi_3")), id("N").pair(id("3")))
                .edge(id("E").pair(id("0")), id("trg").pair(id("pi_3")), id("N").pair(id("3")))
                .graph(B.getName().times(I.getName()))
                .getResult(Graph.class);

       // FIXME compare streams
       // assertEquals(expected.getElements(),result.elements().collect(Collectors.toSet()));
    }


    @Test
    public void testViolationDanglingEdges() throws GraphError {
        GraphBuilders builder = getErrorIgnoringBuilder();
        builder.node("A");
        builder.edge("A", "a", "B");
        builder.graph("Dangling");
        Graph invalid1 = builder.getResult(Graph.class);

        assertFalse(invalid1.verify());
        addExpectedTriple(Triple.edge(Name.identifier("A"), Name.identifier("a"), Name.identifier("B")));
        assertStreamEquals(expected(), invalid1.danglingEdges());

    }




}
