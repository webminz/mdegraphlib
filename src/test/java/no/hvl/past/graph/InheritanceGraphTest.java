package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.util.ShouldNotHappenException;
import org.junit.Test;

import static org.junit.Assert.*;

public class InheritanceGraphTest extends AbstractGraphTest {


    InheritanceGraph IGRAPH = buildExample();

    private InheritanceGraph buildExample() {
        try {
            return getContextCreatingBuilder()
                    .node("Contract")
                    .node("Position")
                    .node("PermanentPosition")
                    .node("PartTimePosition")
                    .node("Person")
                    .node("NaturalPerson")
                    .node("JuridicalPerson")
                    .node("CommunicationChannel")
                    .node("PhysicalCommunicationChannel")
                    .node("ElectronicalCommunicationChannel")
                    .node("PostalAddress")
                    .node("Postbox")
                    .node("PhoneNumber")
                    .node("Email")
                    .edge("Contract", "issuer", "Person")
                    .edge("Contract", "signee", "Person")
                    .edge("Contract", "about", "Position")
                    .edge("Person", "contact", "CommunicationChannel")
                    .edge("NaturalPerson", "familyName", "String")
                    .edge("PartTimePosition", "terminatesOn", "Date")
                    .edge("Email", "value", "String")
                    .map("PermanentPosition", "Position")
                    .map("PartTimePosition", "Position")
                    .map("NaturalPerson", "Person")
                    .map("JuridicalPerson", "Person")
                    .map("PhysicalCommunicationChannel", "CommunicationChannel")
                    .map("ElectronicalCommunicationChannel", "CommunicationChannel")
                    .map("PostalAddress", "PhysicalCommunicationChannel")
                    .map("Postbox", "PhysicalCommunicationChannel")
                    .map("PhoneNumber", "ElectronicalCommunicationChannel")
                    .map("Email", "ElectronicalCommunicationChannel")
                    .inheritanceGraph("test")
                    .getResult(InheritanceGraph.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(getClass(), error.getMessage());
        }
    }

    @Test
    public void testInheritance() {
        assertTrue(IGRAPH.contains(Triple.node(id("Person"))));
        assertFalse(IGRAPH.contains(Triple.node(id("Car"))));
        assertTrue(IGRAPH.contains(Triple.edge(id("Person"), id("contact"), id("CommunicationChannel"))));
        assertFalse(IGRAPH.contains(Triple.edge(id("NaturalPerson"), id("NaturalPerson").subTypeOf(id("Person")), id("Person"))));

        // reflexivity
        assertTrue(IGRAPH.isUnder(id("Person"), id("Person")));
        // but not for edges
        assertFalse(IGRAPH.isUnder(id("signee"), id("signee")));
        // neither for elements that are not part of the graph.
        assertFalse(IGRAPH.isUnder(id("Car"), id("Car")));

        // normal
        assertFalse(IGRAPH.isUnder(id("PermanentPosition"), id("Person")));
        assertTrue(IGRAPH.isUnder(id("NaturalPerson"), id("Person")));
        assertFalse(IGRAPH.isUnder(id("Person"), id("NaturalPerson")));
        assertTrue(IGRAPH.isUnder(id("JuridicalPerson"), id("Person")));
        assertTrue(IGRAPH.isUnder(id("ElectronicalCommunicationChannel"), id("CommunicationChannel")));
        assertTrue(IGRAPH.isUnder(id("Email"), id("ElectronicalCommunicationChannel")));

        // transitivity
        assertTrue(IGRAPH.isUnder(id("Email"), id("CommunicationChannel")));
    }


    @Test
    public void testAdmissableBuilders() throws GraphError {
        Graph result = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .map("A", "B")
                .inheritanceGraph("N")
                .getResult(Graph.class);
        assertTrue(result instanceof InheritanceGraph);

        try {
            getContextCreatingBuilder()
                    .edge("A", "f", "B")
                    .map("A", "B")
                    .graph("N")
                    .getResult(InheritanceGraph.class);
            fail();
        } catch (GraphError error) {
        }
    }


}
