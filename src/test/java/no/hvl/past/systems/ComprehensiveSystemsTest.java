package no.hvl.past.systems;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Identifier;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.hvl.past.systems.ComprSys.qname;
import static org.junit.Assert.assertEquals;

public class ComprehensiveSystemsTest extends GraphTest {


    @Test
    public void testSmallComprehensiveSystem() throws GraphError {

        Sys g1 = new Sys.Builder("http:/1", getContextCreatingBuilder()
                .edge("A", "x", "F")
                .edge("A", "name", "String")
                .graph(Name.identifier("G1").absolute())
                .sketch("G1")
                .getResult(Sketch.class)).build();

        Sys g2 = new Sys.Builder("http:/2", getContextCreatingBuilder()
                .edge("B", "y", "G")
                .edge("B", "name", "String")
                .graph(Name.identifier("G2").absolute())
                .sketch("G2")
                .getResult(Sketch.class)).build();

        Sys g3 = new Sys.Builder("http:/3", getContextCreatingBuilder()
                .edge("D", "d", "C")
                .edge("C", "e", "E")
                .edge("C", "name", "String")
                .graph(Name.identifier("G3").absolute())
                .sketch("G3")
                .getResult(Sketch.class)).build();


        Set<Triple> expected = new HashSet<>();
        expected.add(Triple.node(Name.identifier("ID")).prefix(Name.identifier("GG")));
        expected.add(Triple.node(Name.identifier("String")).prefix(Name.identifier("GG")));
        expected.add(Triple.node(Name.identifier("F")).prefix(Name.identifier("G1")));
        expected.add(Triple.node(Name.identifier("G")).prefix(Name.identifier("G2")));
        expected.add(Triple.node(Name.identifier("D")).prefix(Name.identifier("G3")));
        expected.add(Triple.node(Name.identifier("E")).prefix(Name.identifier("G3")));
        expected.add(Triple.node(Name.identifier("FG")).prefix(Name.identifier("GG")));
        expected.add(Triple.edge(
                Name.identifier("ID").prefixWith(Name.identifier("GG")),
                Name.identifier("name").prefixWith(Name.identifier("GG")),
                Name.identifier("String").prefixWith(Name.identifier("GG"))
        ));
        expected.add(Triple.edge(
                Name.identifier("ID").prefixWith(Name.identifier("GG")),
                Name.identifier("x").prefixWith(Name.identifier("G1")),
                Name.identifier("F").prefixWith(Name.identifier("G1"))
        ));
        expected.add(Triple.edge(
                Name.identifier("ID").prefixWith(Name.identifier("GG")),
                Name.identifier("y").prefixWith(Name.identifier("G2")),
                Name.identifier("G").prefixWith(Name.identifier("G2"))
        ));
        expected.add(Triple.edge(
                Name.identifier("ID").prefixWith(Name.identifier("GG")),
                Name.identifier("e").prefixWith(Name.identifier("G3")),
                Name.identifier("E").prefixWith(Name.identifier("G3"))
        ));
        expected.add(Triple.edge(
                Name.identifier("D").prefixWith(Name.identifier("G3")),
                Name.identifier("d").prefixWith(Name.identifier("G3")),
                Name.identifier("ID").prefixWith(Name.identifier("GG"))
        ));
        // linguistic extension
        expected.add(Triple.edge(
                Name.identifier("FG").prefixWith(Name.identifier("GG")),
                Name.identifier("GG").projectionOn(Name.identifier("G1")).appliedTo(Name.identifier("FG")),
                Name.identifier("F").prefixWith(Name.identifier("G1"))
        ));
        expected.add(Triple.edge(
                Name.identifier("FG").prefixWith(Name.identifier("GG")),
                Name.identifier("GG").projectionOn(Name.identifier("G2")).appliedTo(Name.identifier("FG")),
                Name.identifier("G").prefixWith(Name.identifier("G2"))
        ));

        ComprSys comprSys = new ComprSys.Builder(Name.identifier("GG"), universe)
                .addSystem(g1)
                .addSystem(g2)
                .addSystem(g3)
                .nodeCommonality(Name.identifier("ID"), qname(g1, Name.identifier("A")), qname(g2, Name.identifier("B")), qname(g3, Name.identifier("C")))
                .nodeCommonality(Name.identifier("String"), qname(g1, Name.identifier("String")), qname(g2, Name.identifier("String")), qname(g3, Name.identifier("String")))
                .nodeCommonality(Name.identifier("FG"), qname(g1, Name.identifier("F")), qname(g2, Name.identifier("G")))
                .edgeCommonality(Name.identifier("ID"), Name.identifier("name"), Name.identifier("String"), qname(g1, Name.identifier("name")), qname(g2, Name.identifier("name")), qname(g3, Name.identifier("name")))
                .identification(Name.identifier("ID"))
                .identification(Name.identifier("String"))
                .identification(Name.identifier("name"))
                .build();

        Sketch comprehensiveSystem = comprSys.schema();

        comprehensiveSystem.carrier().elements().forEach(System.out::println);

        assertEquals(expected.size(), comprehensiveSystem.carrier().elements().count());
        assertStreamEquals(expected, comprehensiveSystem.carrier().elements());

        GraphMorphism emb1 = comprSys.embeddingOf(g1);
        assertEquals(Name.identifier("ID").prefixWith(Name.identifier("GG")), emb1.map(Name.identifier("A")).get());
    }
}
