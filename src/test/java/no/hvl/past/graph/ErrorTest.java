package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ErrorTest {

    @Test
    public void testHomPropReporting() {
        GraphError.HomPropertypViolated error = new GraphError.HomPropertypViolated(
                Triple.edge(Name.identifier("0"), Name.identifier("01"), Name.identifier("1")),
                Triple.edge(Name.identifier("A"), Name.identifier("f"), Name.identifier("B")),
                new Tuple(Name.identifier("0"), Name.identifier("A")),
                new Tuple(Name.identifier("01"), Name.identifier("f")),
                new Tuple(Name.identifier("1"), Name.identifier("C"))
        );
        StringBuilder builder = new StringBuilder();
        error.report(builder);
        assertEquals("[GRAPH_ERROR] The following elements violate the homomorphism property (node-edge incidence):\n" +
                "(0)------[01]----->(1)\n" +
                " |         |         |\n" +
                " |         |         |\n" +
                "«0=>A»  «01=>f»  «1=>C»\n" +
                " |         |         |\n" +
                " |         |         |\n" +
                " V         V         V\n" +
                "(A)------[f]------>(B)\n", builder.toString());
    }
}
