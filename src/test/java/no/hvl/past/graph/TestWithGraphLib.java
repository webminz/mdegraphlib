package no.hvl.past.graph;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.hvl.past.TestBase;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;
import no.hvl.past.util.StreamExt;
import org.junit.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestWithGraphLib extends TestBase {

    protected static final Universe universe = new UniverseImpl(UniverseImpl.EMPTY);
    private final Set<Triple> expected = new HashSet<>();
    private int genID = 0;

    private final ExecutionContext executionContext = new ExecutionContext() {
        @Override
        public Name generateNewNodeName() {
            return Name.identifier("gen_" + (genID++));
        }

        @Override
        public Name generateNewEdgeLabel() {
            return Name.identifier("gen_" + (genID++));
        }

        @Override
        public Random randomGenerator() {
            return new Random(42);
        }

        @Override
        public long systemTime() {
            return 1657662900000L; // 23:55 on 31.12.2020
        }

        @Override
        public Properties metaInformation() {
            return new Properties();
        }

        @Override
        public Universe universe() {
            return universe;
        }
    };

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }


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

    protected void addExpectedTriple(Triple triple) {
        this.expected.add(triple);
    }

    protected Stream<Triple> expected() {
        Set<Triple> result = new HashSet<>(this.expected);
        this.expected.clear();
        return result.stream();
    }

    protected static <T> void assertStreamEquals(Stream<T> expected, Stream<T> actual) {
        Assert.assertEquals(expected.collect(Collectors.toSet()), actual.collect(Collectors.toSet()));
    }

    @SafeVarargs
    protected static <T> void assertStreamEquals(Stream<T> toTest, T... toCompare) {
        Assert.assertEquals(Sets.newHashSet(toCompare), toTest.collect(Collectors.toSet()));
    }

    protected static <T> void assertStreamEquals(Set<T> expected, Stream<T> actual) {
        Assert.assertEquals(expected, actual.collect(Collectors.toSet()));
    }

    protected static Triple t(String src, String lbl, String trg) {
        return new Triple(id(src), id(lbl), id(trg));
    }

    protected static Triple att(String owner, String attName, Name val) {
        return new Triple(id(owner), id(attName).prefixWith(id(owner)), val);
    }

    protected static Triple extend(String subclass, String superclass) {
        return new Triple(id(subclass), id(subclass).subTypeOf(id(superclass)), id(superclass));
    }

    protected  static Name id(String id) {
        return Name.identifier(id);
    }

    protected  static Name id() { return Name.anonymousIdentifier();}

    protected static Name prefixed_id(String prefix, String id) {
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


    protected void assertDangling(Name edgeLabel, GraphError error) {
        assertEquals(edgeLabel, StreamExt.pickOne(error.getDangling()).getEdge().getLabel());;
    }

    protected void assertUnknownMember(Name unknownOrMissing, GraphError error) {
        assertEquals(unknownOrMissing, StreamExt.pickOne(error.getUnknown()).getMapping().getCodomain());;
    }

    protected void assertDuplicate(Name name, GraphError error) {
        assertEquals(name, StreamExt.pickOne(error.getDuplicates()).getName());;
    }

    protected void assertHomViolation(Name violatingEdgeLabel, GraphError error) {
        assertEquals(violatingEdgeLabel, StreamExt.pickOne(error.getHomomorphismViolations()).getDomainEdge().getLabel());;
    }

    protected void assertAmbiguouslyMapped(Name ambiguosMapped, GraphError error) {
        assertEquals(ambiguosMapped,  StreamExt.pickOne(StreamExt.pickOne(error.getAmbigous()).getConflictingMappings()).getDomain());
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
                if (expected.label().iff(actualDiagram.label()).equals(Formula.top())) {
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
