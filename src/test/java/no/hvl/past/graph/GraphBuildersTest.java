package no.hvl.past.graph;

import com.google.common.collect.Sets;
import no.hvl.past.graph.builtin.predicates.Acyclicity;
import no.hvl.past.graph.builtin.predicates.SourceMultiplicity;
import no.hvl.past.graph.builtin.predicates.TargetMultiplicity;
import no.hvl.past.graph.elements.Triple;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class GraphBuildersTest extends AbstractTest {

    private final Universe universe = new UniverseImpl(UniverseImpl.EMPTY);
    private final GraphBuilders standardBuilder = new GraphBuilders(universe, true, true);
    private final GraphBuilders contextCreatingBuilder = new GraphBuilders(universe, true, false);
    private final GraphBuilders errorIgnoringBuilder = new GraphBuilders(universe, false, true);
    private final GraphBuilders strictErrorBuilder = new GraphBuilders(universe, false, false);

    @Test
    public void testCreateGraphs() throws GraphError {
        // No result yet
        try {
            standardBuilder.fetchResultGraph();
            fail();
        } catch (GraphError error) {

        }

        // happy flow with a simple graph, creating context (source and target)
        standardBuilder.edge("A", "f", "B");
        standardBuilder.node("C");
        standardBuilder.graph("G");
        Graph graph = standardBuilder.fetchResultGraph();
        assertGraphsEqual(graph, Triple.node(id("C")), Triple.edge(id("A"), id("f"), id("B")));


        // the strict builder does not create source and target automatically, thus edge is dangling
        strictErrorBuilder.edge("A", "f", "B");
        strictErrorBuilder.node("C");
        strictErrorBuilder.graph("G");
        try {
            graph = strictErrorBuilder.fetchResultGraph();
            fail();
        } catch (GraphError error) {
            assertDangling(id("f"), error);
        }

        // this builder also does not create context but he ignores errors in a sense that the operation has no effect
        errorIgnoringBuilder.edge("A", "f", "B");
        errorIgnoringBuilder.node("C");
        errorIgnoringBuilder.graph("G");
        graph = errorIgnoringBuilder.fetchResultGraph();
        assertGraphsEqual(graph, Triple.node(id("C")));

        // ambiguous labels are just ignored by this builder
        standardBuilder.edge("A", "f", "B");
        standardBuilder.edge("B", "f", "A");
        standardBuilder.graph("G");
        graph = standardBuilder.fetchResultGraph();
        assertGraphsEqual(graph, Triple.edge(id("A"), id("f"), id("B")));

        // this builder is more strict and report the "duplicate name"
        contextCreatingBuilder.edge("A", "f", "B");
        contextCreatingBuilder.edge("B", "f", "A");
        contextCreatingBuilder.graph("G");
        try {
            graph = contextCreatingBuilder.fetchResultGraph();
        } catch (GraphError error) {
            assertDuplicate(id("f"), error);
        }

        // creating some duplicate and dangling edges but this builder does not care
        errorIgnoringBuilder.node("A");
        errorIgnoringBuilder.node("AA");
        errorIgnoringBuilder.edge("A", "a", "A");
        errorIgnoringBuilder.edge("AA", "a", "AA");
        errorIgnoringBuilder.edge("A", "b", "B");
        errorIgnoringBuilder.edge("C", "c", "A");
        errorIgnoringBuilder.graph("G");
        graph = errorIgnoringBuilder.fetchResultGraph();
        assertGraphsEqual(graph, Triple.edge(id("A"), id("a"), id("A")), Triple.node(id("AA")));

        // the strict builder reports on dangling edges as well
        strictErrorBuilder.node("A");
        strictErrorBuilder.node("B");
        strictErrorBuilder.edge("A", "f", "B");
        strictErrorBuilder.edge("A", "d1", "???");
        strictErrorBuilder.edge("?", "d2", "A");
        strictErrorBuilder.graph("G");
        try {
            graph = strictErrorBuilder.fetchResultGraph();
        } catch (GraphError error) {
            assertDangling(id("d1"), error);
            assertDangling(id("d2"), error);
        }

    }

    @Test
    public void testCreateMorphisms() throws GraphError {

        // we cannot fetch something before it was built
        try {
            standardBuilder.fetchResultMorphism();
            fail();
        } catch (GraphError error) {

        }

        // happy flow with the standard builder
        standardBuilder.edge("A", "f", "B");
        standardBuilder.edge("A", "g", "C");
        standardBuilder.graph("0");
        standardBuilder.edge("O", "r", "O");
        standardBuilder.graph("1");
        standardBuilder.map("A", "O");
        standardBuilder.map("B", "O");
        standardBuilder.map("C", "O");
        standardBuilder.map("f", "r");
        standardBuilder.map("g", "r");
        standardBuilder.morphism("m");
        GraphMorphism morphism = standardBuilder.fetchResultMorphism();
        GraphImpl domain = new GraphImpl(id("0"), Sets.newHashSet(
                Triple.node(id("A")),
                Triple.node(id("B")),
                Triple.node(id("C")),
                Triple.edge(id("A"), id("f"), id("B")),
                Triple.edge(id("A"), id("g"), id("C"))));
        GraphImpl codomain = new GraphImpl(id("0"), Sets.newHashSet(
                Triple.node(id("O")),
                Triple.edge(id("O"), id("r"), id("O"))));
        assertMorphismsEqual(morph(id("m"), domain, codomain, name -> {
            if (name.equals(id("A")) || name.equals(id("B")) || name.equals(id("C"))) {
                return Optional.of(id("O"));
            } else if (name.equals(id("f")) || name.equals(id("g"))) {
                return Optional.of(id("r"));
            } else {
                return Optional.empty();
            }
        }), morphism);


        standardBuilder.edge("A", "f", "B");
        standardBuilder.edge("B", "g", "A");
        standardBuilder.graph("Typ");
        Graph TG = standardBuilder.fetchResultGraph();


        // Context creating builder does not accept hom-prop violations
        contextCreatingBuilder.codomain(TG);
        contextCreatingBuilder.edge("b1", "f1", "a1");
        contextCreatingBuilder.map("a1", "A");
        contextCreatingBuilder.map("b1", "B");
        contextCreatingBuilder.map("f1", "f");
        contextCreatingBuilder.morphism("m");
        try {
            contextCreatingBuilder.fetchResultMorphism();
        } catch (GraphError error) {
            assertHomViolation(id("f1"), error);
        }

        // catching ambiguous mappings
        strictErrorBuilder.domain(TG);
        strictErrorBuilder.codomain(TG);
        strictErrorBuilder.map("A", "A");
        strictErrorBuilder.map("B", "B");
        strictErrorBuilder.map("f", "f");
        strictErrorBuilder.map("A", "B");
        strictErrorBuilder.map("h", "f");
        strictErrorBuilder.map("g", "i");
        strictErrorBuilder.morphism("m");
        try {
            strictErrorBuilder.fetchResultMorphism();
        } catch (GraphError error) {
            assertAmbiguouslyMapped(id("A"), error);
            assertUnknownMember(id("h"), error);
            assertUnknownMember(id("i"), error);
        }

        // testing the error ignoring builder
        errorIgnoringBuilder.map("a", "1");
        errorIgnoringBuilder.map("b", "2");
        errorIgnoringBuilder.node("a");
        errorIgnoringBuilder.node("b");
        errorIgnoringBuilder.node("c");
        errorIgnoringBuilder.graph("G");
        errorIgnoringBuilder.node("1");
        errorIgnoringBuilder.node("2");
        errorIgnoringBuilder.graph("TG");
        errorIgnoringBuilder.map("a", "2");
        errorIgnoringBuilder.map("a", "1");
        errorIgnoringBuilder.map("c", "1");
        errorIgnoringBuilder.morphism("m");
        morphism = errorIgnoringBuilder.fetchResultMorphism();
        final Graph morphismDomain = morphism.domain();
        final Graph morphismCodomain = morphism.codomain();
        assertGraphsEqual(morphismDomain, Triple.node(id("a")), Triple.node(id("b")), Triple.node(id("c")));
        assertGraphsEqual(morphismCodomain, Triple.node(id("1")), Triple.node(id("2")));
        assertMorphismsEqual(morph(id("m"), morphismDomain, morphismCodomain, name -> {
                if (name.equals(id("b"))) {
                    return Optional.empty();
                }
                if (name.equals(id("a"))) {
                    return Optional.of(id("2"));
                }
                if (name.equals(id("c"))) {
                    return Optional.of(id("1"));
                }
                fail();
                return Optional.empty();
            }), morphism);


        // context creating builder creates its codomain
        contextCreatingBuilder.clear();
        contextCreatingBuilder.edge("A", "f", "B");
        contextCreatingBuilder.edge("A", "g", "B");
        contextCreatingBuilder.edge("A", "h", "C");
        contextCreatingBuilder.graph("G");
        contextCreatingBuilder.map("A", "0");
        contextCreatingBuilder.map("B", "1");
        contextCreatingBuilder.map("C", "1");
        contextCreatingBuilder.map("f", "01");
        contextCreatingBuilder.map("g", "01");
        contextCreatingBuilder.map("h", "01");
        contextCreatingBuilder.morphism("m");
        morphism = contextCreatingBuilder.fetchResultMorphism();
        assertEquals(id("cod").appliedTo(id("m")), morphism.codomain().getName());
        assertGraphsEqual(morphism.codomain(), Triple.edge(id("0"), id("01"), id("1")));


        // context creating builder creates both its domain and codomain
        contextCreatingBuilder.clear();
        contextCreatingBuilder.map("A", "0");
        contextCreatingBuilder.map("B", "0");
        contextCreatingBuilder.map("C", "1");
        contextCreatingBuilder.morphism("m");
        morphism = contextCreatingBuilder.fetchResultMorphism();
        assertEquals(id("dom").appliedTo(id("m")), morphism.domain().getName());
        assertEquals(id("cod").appliedTo(id("m")), morphism.codomain().getName());
        assertGraphsEqual(morphism.domain(), Triple.node(id("A")), Triple.node(id("B")), Triple.node(id("C")));
        assertGraphsEqual(morphism.codomain(), Triple.node(id("0")), Triple.node(id("1")));

        // the strict error builder needs both domain and codomain
        strictErrorBuilder.clear();
        strictErrorBuilder.map("A", "0");
        strictErrorBuilder.morphism("m");
        try {
            strictErrorBuilder.fetchResultMorphism();
        } catch (GraphError error) {
            assertUnknownMember(id("A"), error);
        }

        strictErrorBuilder.node("A");
        strictErrorBuilder.graph("domA");
        strictErrorBuilder.map("A", "0");
        strictErrorBuilder.morphism("m");
        try {
            strictErrorBuilder.fetchResultMorphism();
        } catch (GraphError error) {
            assertUnknownMember(id("0"), error);

        }

        // context creating builder is still error sensitive
        contextCreatingBuilder.edge("A", "f", "B");
        contextCreatingBuilder.codomain(TG);
        contextCreatingBuilder.map("A", "A");
        contextCreatingBuilder.map("B", "B");
        contextCreatingBuilder.map("f", "f");
        contextCreatingBuilder.map("unknown", "f");
        contextCreatingBuilder.morphism("m");
        try {
            contextCreatingBuilder.fetchResultMorphism();
        } catch (GraphError error) {
            assertUnknownMember(id("unknown"), error);
        }


        // morphisms may be partial
        standardBuilder.edge("A", "f", "B");
        standardBuilder.node("C");
        standardBuilder.map("A", "0");
        standardBuilder.map("B", "0");
        standardBuilder.map("f", "00");
        standardBuilder.morphism("m");
        morphism = standardBuilder.fetchResultMorphism();
        assertGraphsEqual(morphism.codomain(), Triple.edge(id("0"), id("00"), id("0")));
        assertEquals(Optional.empty(), morphism.map(id("C")));
        assertEquals(Optional.of(id("0")), morphism.map(id("A")));


    }


    @Test
    public void testCreateSketch() throws GraphError {
        try {
            standardBuilder.fetchResultSketch();
            fail();
        } catch (GraphError error) {
        }

        // testing the happy flow
        standardBuilder.edge("Class", "extends", "Class");
        standardBuilder.edge("Class", "ref", "Class");
        standardBuilder.edge("Class", "contains", "Class");
        standardBuilder.edge("Class", "att", "Value");
        standardBuilder.graph("Carrier");
        standardBuilder.startDiagram(TargetMultiplicity.getInstance(0, 1));
        standardBuilder.map(id("0"), id("Class"));
        standardBuilder.map(id("01"), id("extends"));
        standardBuilder.map(id("1"), id("Class"));
        standardBuilder.endDiagram(id("d1"));
        standardBuilder.startDiagram(SourceMultiplicity.getInstance(0, 1));
        standardBuilder.map(id("0"), id("Class"));
        standardBuilder.map(id("01"), id("contains"));
        standardBuilder.map(id("1"), id("Class"));
        standardBuilder.endDiagram(id("d2"));
        standardBuilder.startDiagram(Acyclicity.getInstance());
        standardBuilder.map(id("0"), id("Class"));
        standardBuilder.map(id("00"), id("extends"));
        standardBuilder.endDiagram(id("d3"));
        standardBuilder.startDiagram(Acyclicity.getInstance());
        standardBuilder.map(id("0"), id("Class"));
        standardBuilder.map(id("00"), id("contains"));
        standardBuilder.endDiagram(id("d4"));
        standardBuilder.sketch("OO");
        Sketch sketch = standardBuilder.fetchResultSketch();


        standardBuilder.domain(Universe.ARROW);
        standardBuilder.codomain(sketch.carrier());
        standardBuilder.map(id("0"), id("Class"));
        standardBuilder.map(id("01"), id("extends"));
        standardBuilder.map(id("1"), id("Class"));
        standardBuilder.morphism(id("d1").absolute());
        Diagram d1 = diag(id("d1"), TargetMultiplicity.getInstance(0, 1), standardBuilder.fetchResultMorphism());

        standardBuilder.domain(Universe.ARROW);
        standardBuilder.codomain(sketch.carrier());
        standardBuilder.map(id("0"), id("Class"));
        standardBuilder.map(id("01"), id("contains"));
        standardBuilder.map(id("1"), id("Class"));
        standardBuilder.morphism(id("d2").absolute());
        Diagram d2 = diag(id("d2"), SourceMultiplicity.getInstance(0, 1), standardBuilder.fetchResultMorphism());


        standardBuilder.domain(Universe.LOOP);
        standardBuilder.codomain(sketch.carrier());
        standardBuilder.map(id("0"), id("Class"));
        standardBuilder.map(id("00"), id("extends"));
        standardBuilder.morphism(id("d3").absolute());
        Diagram d3 = diag(id("d3"), Acyclicity.getInstance(), standardBuilder.fetchResultMorphism());

        standardBuilder.domain(Universe.LOOP);
        standardBuilder.codomain(sketch.carrier());
        standardBuilder.map(id("0"), id("Class"));
        standardBuilder.map(id("00"), id("contains"));
        standardBuilder.morphism(id("d4").absolute());
        Diagram d4 = diag(id("d4"), Acyclicity.getInstance(), standardBuilder.fetchResultMorphism());

        standardBuilder.edge("Class", "extends", "Class");
        standardBuilder.edge("Class", "ref", "Class");
        standardBuilder.edge("Class", "contains", "Class");
        standardBuilder.edge("Class", "att", "Value");
        standardBuilder.graph("Carrier");

        assertSketchEquals(sketch, standardBuilder.fetchResultGraph(), d1, d2, d3, d4);
    }



}
