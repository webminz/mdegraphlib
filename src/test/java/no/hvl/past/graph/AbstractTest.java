package no.hvl.past.graph;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.junit.Assert;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

class AbstractTest {

    private final Universe universe = new UniverseImpl(UniverseImpl.EMPTY);
    private final Set<Triple> expected = new HashSet<>();

    public GraphBuilders getPrototypeBuilder() {
        return new GraphBuilders(universe, true, true);
    }

    public GraphBuilders getContextCreatingBuilder() {
        return new GraphBuilders(universe, true, false);
    }

    public GraphBuilders getErrorIgnoringBuilder() {
        return new GraphBuilders(universe, false, true);
    }

    public GraphBuilders getStrictBuilder() {
        return new GraphBuilders(universe, false, false);
    }

    public Universe getUniverse() {
        return universe;
    }

    void addExpectedTriple(Triple triple) {
        this.expected.add(triple);
    }

    Stream<Triple> expected() {
        return this.expected.stream();
    }

    static <T> void assertStreamEquals(Stream<T> expected, Stream<T> actual) {
        Assert.assertEquals(expected.collect(Collectors.toSet()), actual.collect(Collectors.toSet()));
    }

    static <T> void assertStreamEquals(Stream<T> toTest, T... toCompare) {
        Assert.assertEquals(Sets.newHashSet(toCompare), toTest.collect(Collectors.toSet()));
    }

    static <T> void assertStreamEquals(Set<T> expected, Stream<T> actual) {
        Assert.assertEquals(expected, actual.collect(Collectors.toSet()));
    }

    static Triple t(String src, String lbl, String trg) {
        return new Triple(id(src), id(lbl), id(trg));
    }

    static Triple att(String owner, String attName, Name val) {
        return new Triple(id(owner), id(attName).prefixWith(id(owner)), val);
    }

    static Triple extend(String subclass, String superclass) {
        return new Triple(id(subclass), id(subclass).subTypeOf(id(superclass)), id(superclass));
    }

    static Name id(String id) {
        return Name.identifier(id);
    }

    static Name id() { return Name.anonymousIdentifier();}

    static Name prefixed_id(String prefix, String id) {
        return Name.identifier(id).prefixWith(Name.identifier(prefix));
    }

    static GraphMorphism morph(Name name, Graph domain, Graph codomain, Function<Name, Optional<Name>> mapping) {
        return new GraphMorphism() {
            @Override
            public Graph domain() {
                return domain;
            }

            @Override
            public Graph codomain() {
                return codomain;
            }

            @Override
            public Optional<Name> map(Name name) {
                return mapping.apply(name);
            }

            @Override
            public Name getName() {
                return name;
            }
        };
    }

    static Diagram diag(Name name, GraphLabelTheory label, GraphMorphism binding) {
        return new DiagramImpl(name, label, binding);
    }

    protected void assertDangling(Name edgeLabel, GraphError error) {
        assertTrue(error.getDangling().contains(edgeLabel));
    }

    protected void assertUnknownMember(Name unknownOrMissing, GraphError error) {
        assertTrue(error.getUnknown().contains(unknownOrMissing));
    }

    protected void assertDuplicate(Name name, GraphError error) {
        assertTrue(error.getDuplicates().contains(name));
    }

    protected void assertHomViolation(Name violatingEdgeLabel, GraphError error) {
        assertTrue(error.getHomomorphismViolations().contains(violatingEdgeLabel));
    }

    protected void assertAmbiguouslyMapped(Name ambiguosMapped, GraphError error) {
        assertTrue(error.getAmbiguousMappings().contains(ambiguosMapped));
    }

    public static void assertGraphsEqual(Graph graph, Triple... allEdges) {
        Set<Triple> allEdgesSet = new HashSet<>();
        Lists.newArrayList(allEdges).forEach(triple -> {
            allEdgesSet.add(Triple.node(triple.getSource()));
            allEdgesSet.add(Triple.node(triple.getTarget()));
            allEdgesSet.add(triple);
        });
        Assert.assertEquals(allEdgesSet, graph.elements().collect(Collectors.toSet()));
    }

    public static void assertGraphsEqual(Graph expected, Graph actual) {
        Assert.assertEquals(expected.elements().collect(Collectors.toSet()), actual.elements().collect(Collectors.toSet()));
    }

    public static void assertMorphismsEqual(GraphMorphism expected, GraphMorphism actual) {
        assertGraphsEqual(expected.domain(), actual.domain());
        assertGraphsEqual(expected.codomain(), actual.codomain());
        expected.domain().elements().forEach(t -> {
            Assert.assertEquals(expected.apply(t), actual.apply(t));
        });
    }

    public static void assertSketchEquals(Sketch actual, Graph expectedCarrier, Diagram... expectedDiagrams) {
        assertGraphsEqual(expectedCarrier, actual.carrier());
        assertEquals(actual.diagrams().count(), (long) expectedDiagrams.length);
        assertTrue(actual.diagrams().allMatch(actualDiagram -> {
            for (Diagram expected : expectedDiagrams) {
                if (expected.label().equals(actualDiagram.label())) {
                    if (actualDiagram.binding().domain().elements().allMatch(t -> actualDiagram.binding().apply(t).equals(expected.binding().apply(t)))) {
                        return true;
                    }
                }
            }
            return false;
        }));
    }

    public static void assertSketchEquals(Sketch expected, Sketch actual) {
        assertGraphsEqual(expected.carrier(), actual.carrier());
        assertStreamEquals(expected.diagrams(), actual.diagrams());
    }


}
