package no.hvl.past.graph;

import no.hvl.past.util.ShouldNotHappenException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InheritanceGraphTest extends AbstractTest {


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
            throw new ShouldNotHappenException(getClass(), "buildExample", error);
        }
    }

    @Test
    public void testGraph() {
        assertFalse(InheritanceGraph.class.isAssignableFrom(Graph.class));
        assertTrue(Graph.class.isAssignableFrom(InheritanceGraph.class));
    }


}
