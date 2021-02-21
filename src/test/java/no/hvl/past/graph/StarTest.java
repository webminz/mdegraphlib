package no.hvl.past.graph;

import com.google.common.collect.Sets;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StarTest extends AbstractGraphTest {


    @Test
    public void testSmallComprehensiveSystem() throws GraphError {
        Sketch g0 = getContextCreatingBuilder()
                .edge("ID", "name", "String")
                .node("FG")
                .graph(Name.identifier("G0").absolute())
                .sketch("G0")
                .getResult(Sketch.class);

        Sketch g1 = getContextCreatingBuilder()
                .edge("A", "x", "F")
                .edge("A", "name", "String")
                .graph(Name.identifier("G1").absolute())
                .sketch("G1")
                .getResult(Sketch.class);

        Sketch g2 = getContextCreatingBuilder()
                .edge("B", "y", "G")
                .edge("B", "name", "String")
                .graph(Name.identifier("G2").absolute())
                .sketch("G2")
                .getResult(Sketch.class);

        Sketch g3 = getContextCreatingBuilder()
                .edge("D", "d", "C")
                .edge("C", "e", "E")
                .edge("C", "name", "String")
                .graph(Name.identifier("G3").absolute())
                .sketch("G3")
                .getResult(Sketch.class);

        GraphMorphism m1 = getContextCreatingBuilder()
                .domain(g0.carrier())
                .codomain(g1.carrier())
                .map(Name.identifier("ID"), Name.identifier("A"))
                .map(Name.identifier("name"), Name.identifier("name"))
                .map(Name.identifier("String"), Name.identifier("String"))
                .map(Name.identifier("FG"), Name.identifier("F"))
                .morphism("m1")
                .getResult(GraphMorphism.class);


        GraphMorphism m2 = getContextCreatingBuilder()
                .domain(g0.carrier())
                .codomain(g2.carrier())
                .map(Name.identifier("ID"), Name.identifier("B"))
                .map(Name.identifier("name"), Name.identifier("name"))
                .map(Name.identifier("String"), Name.identifier("String"))
                .map(Name.identifier("FG"), Name.identifier("G"))
                .morphism("m2")
                .getResult(GraphMorphism.class);

        GraphMorphism m3 = getContextCreatingBuilder()
                .domain(g0.carrier())
                .codomain(g3.carrier())
                .map(Name.identifier("ID"), Name.identifier("C"))
                .map(Name.identifier("name"), Name.identifier("name"))
                .map(Name.identifier("String"), Name.identifier("String"))
                .morphism("m3")
                .getResult(GraphMorphism.class);

        Star star = new StarImpl(
                Name.identifier("C"),
                g0,
                Arrays.asList(g1, g2, g3),
                Arrays.asList(m1, m2, m3),
                Sets.newHashSet(Name.identifier("ID"), Name.identifier("name"), Name.identifier("String")));

        Set<Triple> expected = new HashSet<>();
        expected.add(Triple.node(Name.identifier("ID")));
        expected.add(Triple.node(Name.identifier("String")));
        expected.add(Triple.node(Name.identifier("F")));
        expected.add(Triple.node(Name.identifier("G")));
        expected.add(Triple.node(Name.identifier("D")));
        expected.add(Triple.node(Name.identifier("E")));
        expected.add(Triple.node(Name.identifier("FG")));
        expected.add(Triple.edge(
                Name.identifier("ID"),
                Name.identifier("name"),
                Name.identifier("String")
        ));
        expected.add(Triple.edge(
                Name.identifier("ID"),
                Name.identifier("x"),
                Name.identifier("F")
        ));
        expected.add(Triple.edge(
                Name.identifier("ID"),
                Name.identifier("y"),
                Name.identifier("G")
        ));
        expected.add(Triple.edge(
                Name.identifier("ID"),
                Name.identifier("e"),
                Name.identifier("E")
        ));
        expected.add(Triple.edge(
                Name.identifier("D"),
                Name.identifier("d"),
                Name.identifier("ID")
        ));
        // linguistic extension
        expected.add(Triple.edge(
                Name.identifier("FG"),
                Name.identifier("m1").appliedTo(Name.identifier("FG")),
                Name.identifier("F")
        ));
        expected.add(Triple.edge(
                Name.identifier("FG"),
                Name.identifier("m2").appliedTo(Name.identifier("FG")),
                Name.identifier("G")
        ));

        Pair<Sketch, List<GraphMorphism>> comprehensiveSystemFull = star.comprehensiveSystem();
        Sketch comprehensiveSystem = comprehensiveSystemFull.getFirst();

        comprehensiveSystem.carrier().elements().forEach(System.out::println);

        assertEquals(expected.size(), comprehensiveSystem.carrier().elements().count());
        assertStreamEquals(expected, comprehensiveSystem.carrier().elements());

        GraphMorphism emb1 = comprehensiveSystemFull.getRight().get(1);
        assertEquals(Name.identifier("ID"), emb1.map(Name.identifier("A")).get());

    }
}
