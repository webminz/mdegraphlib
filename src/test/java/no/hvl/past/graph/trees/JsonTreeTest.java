package no.hvl.past.graph.trees;

import com.fasterxml.jackson.core.JsonFactory;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.predicates.IntDT;
import no.hvl.past.graph.predicates.StringDT;
import no.hvl.past.names.Name;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class JsonTreeTest extends GraphTest {

    @Test
    public void testReadFHIRPatients() throws IOException {
        JsonParser jsonParser = new JsonParser(new JsonFactory());
        Tree pAtient = jsonParser.parse(new File("src/test/resources/trees/fhir_patient.json"), Name.identifier("FHIR_PAtient"));
        Optional<Node> firstEntry = pAtient.root().childNodesByKey("entry").findFirst();
        assertTrue(firstEntry.isPresent());
        Optional<Node> resource = firstEntry.get().childNodesByKey("resource").findFirst();
        assertTrue(resource.isPresent());
        Optional<Node> id = resource.get().childNodesByKey("id").findFirst();
        assertTrue(id.isPresent());
        assertEquals(Name.identifier("618761"), id.get().elementName());
    }

    @Test
    public void testReadFHIRPatientsTyped() throws GraphError, IOException {
        Sketch schema = getContextCreatingBuilder()
                .node("int")
                .node("string")
                .edge("Bundle", "entry", "Entry")
                .edge("Entry", "resource", "PatientResource")
                .edge("PatientResource", "id", "int")
                .edge("PatientResource", "identifier", "Identifier")
                .edge("Identifier", "value", "string")
                .edge("PatientResource", "name", "Name")
                .edge("Name", "family", "string")
                .edge("Name", "given", "string")
                .graph(Name.anonymousIdentifier())
                .startDiagram(StringDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("string"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(IntDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("int"))
                .endDiagram(Name.anonymousIdentifier())
                .sketch("FHIR_PATIENT")
                .getResult(Sketch.class);
        TreeBuildStrategy strategy = new TreeBuildStrategy.TypedStrategy() {


            @Override
            public Graph getSchemaGraph() {
                return schema.carrier();
            }

            @Override
            public Optional<Name> rootType(String label) {
                return Optional.of(Name.identifier("Bundle"));
            }

            @Override
            public Optional<Triple> lookupType(Name parentType, String field) {
               return schema.carrier().get(parentType).flatMap(t -> schema.carrier().outgoing(t.getTarget()).filter(tt -> tt.getLabel().equals(Name.identifier(field))).findFirst());
            }

            @Override
            public boolean isStringType(Name typeName) {
                return typeName.equals(Name.identifier("string"));
            }

            @Override
            public boolean isBoolType(Name typeName) {
                return false;
            }

            @Override
            public boolean isFloatType(Name typeName) {
                return false;
            }

            @Override
            public boolean isIntType(Name typeName) {
                return typeName.equals(Name.identifier("int"));

            }

            @Override
            public boolean isEnumType(Name typeName) {
                return false;
            }
        };

        JsonParser jsonParser = new JsonParser(new JsonFactory());
        TypedTree typed = jsonParser.parse(
                new File("src/test/resources/trees/fhir_patient.json"),
                strategy
        );
        Optional<Node> firstEntry = typed.root().childNodesByKey("entry").findFirst();
        assertTrue(firstEntry.isPresent());
        Optional<Node> resource = firstEntry.get().childNodesByKey("resource").findFirst();
        assertTrue(resource.isPresent());
        Optional<Node> id = resource.get().childNodesByKey("id").findFirst();
        assertTrue(id.isPresent());
        assertEquals(Name.value(new BigInteger("618761")), id.get().elementName());

        GraphMorphism morphism = typed.typedPartToMorphism();
        List<Name> collect = morphism.allInstances(Triple.edge(Name.identifier("Name"), Name.identifier("given"), Name.identifier("string"))).map(Triple::getTarget).collect(Collectors.toList());
        List<Name> expected = new ArrayList<>();
        expected.add(Name.value("Bob"));
        expected.add(Name.value("Caleb"));
        expected.add(Name.value("Leia"));
        expected.add(Name.value("Leia"));
        expected.add(Name.value("Bob"));
        expected.add(Name.value("Caleb"));
        expected.add(Name.value("Dorothy"));
        assertEquals(expected, collect);
    }

}
