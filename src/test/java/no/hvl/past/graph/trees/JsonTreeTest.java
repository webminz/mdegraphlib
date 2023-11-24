package no.hvl.past.graph.trees;

import no.hvl.past.graph.*;

import no.hvl.past.names.Name;
import no.hvl.past.util.Holder;
import org.junit.jupiter.api.Test;


import java.io.*;
import java.util.Collections;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JsonTreeTest extends TestWithGraphLib {


    @Test
    public void testReadSimpleJSON() throws Exception {
        String input = "{\n" +
                "  \"name\" : \"Hans Hansen\",\n" +
                "  \"address\" : {\n" +
                "    \"street\" : \"Hansavegen 23\",\n" +
                "    \"postalCode\" : 5000\n" +
                "  },\n" +
                "  \"languages\" : [\n" +
                "    \"GERMAN\",\n" +
                "    \"ENGLISH\",\n" +
                "    \"NORWEGIAN\"\n" +
                "  ]\n" +
                "}";

        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startTree(Name.identifier("test"));
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(id("name"), false);
        expected.valueLeaf(Name.value("Hans Hansen"));
        expected.endBranch();
        expected.startBranch(id("address"), false);
        expected.startComplexNode();
        expected.startBranch(id("street"), false);
        expected.valueLeaf(Name.value("Hansavegen 23"));
        expected.endBranch();
        expected.startBranch(id("postalCode"), false);
        expected.valueLeaf(Name.value(5000));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(id("languages"), true);
        expected.valueLeaf(Name.value("GERMAN"));
        expected.valueLeaf(Name.value("ENGLISH"));
        expected.valueLeaf(Name.value("NORWEGIAN"));
        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        JsonLibrary.getInstance().reader().treeName(Name.identifier("test")).input(input).emitEvents(actual);

        assertEquals(expected, actual);


    }


    @Test
    public void testReadBooksJson() throws Exception {
        Holder<Tree> resultHolder = new Holder<>();
        TreeReceiver creator = TreeBuilder.builderHandler(
                Collections.singletonList(new TreeElementNamingStrategy.ParentBased(
                        (parent, child) -> child.childOf(parent))),
                resultHolder::set);
        JsonLibrary.getInstance().reader().input(new File("src/test/resources/trees/books.json")).emitEvents(creator);
        assertTrue(resultHolder.hasValue());
        Tree result = resultHolder.unsafeGet();
        assertEquals(3, result.root().depth());
        assertEquals(5, result.root().childNodesByKey(id("books")).count());
        Node book = result.root().childNodesByKey(id("books")).collect(Collectors.toList()).get(1);
        assertTrue( book.childNodesByKey(id("id")).findFirst().isPresent());
        assertEquals(Name.value("bk102"), book.childNodesByKey(id("id")).findFirst().get().elementName());

    }

    // TODO reactivate
//    @Test
//    public void testReadFHIRPatients() throws IOException {
//        OldJsonParser jsonParser = new OldJsonParser(new JsonFactory());
//        Tree pAtient = jsonParser.parse(new File("src/test/resources/trees/fhir_patient.json"), Name.identifier("FHIR_PAtient"));
//        Optional<Node> firstEntry = pAtient.root().childNodesByKey("entry").findFirst();
//        assertTrue(firstEntry.isPresent());
//        Optional<Node> resource = firstEntry.get().childNodesByKey("resource").findFirst();
//        assertTrue(resource.isPresent());
//        Optional<Node> id = resource.get().childNodesByKey("id").findFirst();
//        assertTrue(id.isPresent());
//        assertEquals(Name.identifier("618761"), id.get().elementName());
//    }
//
//    @Test
//    public void testReadFHIRPatientsTyped() throws GraphError, IOException {
//        Sketch schema = getContextCreatingBuilder()
//                .node("int")
//                .node("string")
//                .edge("Bundle", "entry", "Entry")
//                .edge("Entry", "resource", "PatientResource")
//                .edge("PatientResource", "id", "int")
//                .edge("PatientResource", "identifier", "Identifier")
//                .edge("Identifier", "value", "string")
//                .edge("PatientResource", "name", "Name")
//                .edge("Name", "family", "string")
//                .edge("Name", "given", "string")
//                .graph(Name.anonymousIdentifier())
//                .startDiagram(StringDT.getInstance())
//                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("string"))
//                .endDiagram(Name.anonymousIdentifier())
//                .startDiagram(IntDT.getInstance())
//                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("int"))
//                .endDiagram(Name.anonymousIdentifier())
//                .sketch("FHIR_PATIENT")
//                .getResult(Sketch.class);
//        TreeBuildStrategy strategy = new TreeBuildStrategy.TypedStrategy() {
//
//
//            @Override
//            public Graph getSchemaGraph() {
//                return schema.carrier();
//            }
//
//            @Override
//            public Optional<Name> rootType(String label) {
//                return Optional.of(Name.identifier("Bundle"));
//            }
//
//            @Override
//            public Optional<Triple> lookupType(Name parentType, String field) {
//               return schema.carrier().get(parentType).flatMap(t -> schema.carrier().outgoing(t.getTarget()).filter(tt -> tt.getLabel().equals(Name.identifier(field))).findFirst());
//            }
//
//            @Override
//            public boolean isStringType(Name typeName) {
//                return typeName.equals(Name.identifier("string"));
//            }
//
//            @Override
//            public boolean isBoolType(Name typeName) {
//                return false;
//            }
//
//            @Override
//            public boolean isFloatType(Name typeName) {
//                return false;
//            }
//
//            @Override
//            public boolean isIntType(Name typeName) {
//                return typeName.equals(Name.identifier("int"));
//
//            }
//
//            @Override
//            public boolean isEnumType(Name typeName) {
//                return false;
//            }
//        };
//
//        OldJsonParser jsonParser = new OldJsonParser(new JsonFactory());
//        TypedTree typed = jsonParser.parse(
//                new File("src/test/resources/trees/fhir_patient.json"),
//                strategy
//        );
//        Optional<Node> firstEntry = typed.root().childNodesByKey("entry").findFirst();
//        assertTrue(firstEntry.isPresent());
//        Optional<Node> resource = firstEntry.get().childNodesByKey("resource").findFirst();
//        assertTrue(resource.isPresent());
//        Optional<Node> id = resource.get().childNodesByKey("id").findFirst();
//        assertTrue(id.isPresent());
//        assertEquals(Name.value(new BigInteger("618761")), id.get().elementName());
//
//        GraphMorphism morphism = typed.typedPartToMorphism();
//        List<Name> collect = morphism.allInstances(Triple.edge(Name.identifier("Name"), Name.identifier("given"), Name.identifier("string"))).map(Triple::getTarget).collect(Collectors.toList());
//        List<Name> expected = new ArrayList<>();
//        expected.add(Name.value("Bob"));
//        expected.add(Name.value("Caleb"));
//        expected.add(Name.value("Leia"));
//        expected.add(Name.value("Leia"));
//        expected.add(Name.value("Bob"));
//        expected.add(Name.value("Caleb"));
//        expected.add(Name.value("Dorothy"));
//        assertEquals(expected, collect);
//    }

}
