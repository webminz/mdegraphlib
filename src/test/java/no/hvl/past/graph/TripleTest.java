package no.hvl.past.graph;

import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class TripleTest extends TestWithGraphLib {

    @Test
    public void testRenaming() {
        Triple edge = t("S", "l", "T");
        assertEquals(Triple.edge(
                id("S").prefixWith(id("G")), id("l").prefixWith(id("G")), id("T").prefixWith(id("G"))),
                edge.mapName(n -> n.prefixWith(id("G"))));
        assertEquals(Optional.empty(), edge.map(n -> {
            if (n.equals(id("l"))) {
                return Optional.empty();
            } else {
                return Optional.of(n);
            }
        }));
        assertEquals(Optional.of(edge.mapName(n -> n.typeBy(id("T")))), edge.map(n -> Optional.of(n.typeBy(id("T")))));
    }

    @Test
    public void testComposition() {
        Triple f = t("A", "f", "B");
        Triple g = t("B", "g", "C");
        Triple h = t("A", "h", "C");

        assertEquals(Optional.of(Triple.edge(id("A"), id("f").composeSequentially(id("g")), id("C"))), f.compose(g));
        assertEquals(Optional.empty(), h.compose(g));
        assertEquals(Optional.of(Triple.edge(id("A"), id("h").composeSequentially(id("g").inverse()), id("B"))), h.compose(g.inverse()));
    }

    @Test
    public void testProperties() {
        Triple edge = t("S", "l", "T");
        Triple f = t("A", "f", "B");
        Triple g = t("B", "g", "C");
        Triple h = t("A", "h", "C");
        Triple prefixed = edge.prefix(id("G"));
        Triple renamed = edge.mapName(n -> n.pair(id("p")));

        assertTrue(f.isIncident(g));
        assertTrue(g.isIncident(f));
        assertFalse(f.isIncident(h));
        assertFalse(g.isIncident(h));
        assertTrue(f.isAdjacent(h));
        assertTrue(g.isAdjacent(h));
        assertFalse(f.isAdjacent(g));

        assertFalse(edge.isAdjacent(f));
        assertFalse(edge.isIncident(f));

        assertFalse(edge.isDerived());
        assertTrue(f.compose(g).get().isDerived());
        assertTrue(f.inverse().isDerived());
        assertFalse(edge.isDerived());
        assertTrue(renamed.isDerived());
        assertFalse(prefixed.isDerived());

        assertTrue(prefixed.hasPrefix(id("G")));
        assertFalse(edge.hasPrefix(id("G")));
        assertTrue(prefixed.namesHaveProperty(n -> n.hasPrefix(id("G"))));
        assertTrue(renamed.namesHaveProperty(n -> n.isMultipart()));
    }

    @Test
    public void testAttributes() {
        Triple edge = t("S", "l", "T");
        Triple attr = Triple.intAttribute(id("Person"), id("age"), 23);
        assertTrue(attr.isAttribute());
        assertFalse(edge.isAttribute());

        assertEquals(Optional.of(IntegerValue.value(23)), attr.getValue());
        assertEquals(Optional.empty(), edge.getValue());
    }


    @Test
    public void testSortingViaIndexing() {
        Triple t1 = Triple.edge(id("A"), id("f").index(42), Name.value(3));
        Triple t2 = Triple.edge(id("A"), id("f").index(1), Name.value(2));
        Triple t3 = Triple.edge(id("A"), id("f").index(23), Name.value(1));
        List<Triple> list = new ArrayList<>();
        list.add(t1);
        list.add(t2);
        list.add(t3);

        assertTrue(t2.compareTo(t3) < 0);
        assertTrue(t3.compareTo(t1) < 0);
        assertTrue(t2.compareTo(t1) < 0);

        List<Triple> expected = new ArrayList<>();
        expected.add(t2);
        expected.add(t3);
        expected.add(t1);

        assertEquals(expected, list.stream().sorted().collect(Collectors.toList()));

    }
}
