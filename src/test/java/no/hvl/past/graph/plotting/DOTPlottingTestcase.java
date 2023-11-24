package no.hvl.past.graph.plotting;

import com.google.common.io.Files;
import no.hvl.past.attributes.BuiltinOperations;
import no.hvl.past.attributes.DataOperation;
import no.hvl.past.graph.*;
import no.hvl.past.graph.operations.Invert;
import no.hvl.past.graph.predicates.*;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class DOTPlottingTestcase extends TestWithGraphLib {

    // TODO must possibly be able to disable this

    private static final String BASE_DIR = System.getProperty("user.dir");
    private static final String RESOURCE_OUTPUT_DIR = BASE_DIR + "/src/test/resources/output/";
    private static final String RESOURCE_COMPARISON_DIR = BASE_DIR + "/src/test/resources/comparison/";

    private void runDot(File sourceFile, File targetFile) throws IOException {
        Runtime.getRuntime().exec("dot -Tpng -o" + targetFile.getAbsolutePath() + ".png "+ sourceFile.getAbsolutePath());
    }

    private void assertBinariesEqual(File expected, File actual) throws IOException {
        assertTrue(Files.equal(expected, actual));
    }


    public void testSimpleDrawing() throws IOException, GraphError {

        File generatedDotFile = new File(RESOURCE_OUTPUT_DIR,"test1.dot");
        if (generatedDotFile.exists()) {
            generatedDotFile.delete();
        }

        Plotter writer = new Plotter(false, PrintingStrategy.IGNORE_PREFIX);

        Graph graphResult = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .edge("B", "g", "C")
                .edge("A", "h", "C")
                .graph("G")
                .getResult(Graph.class);
        graphResult.accept(writer);
        writer.writeToFile(generatedDotFile);

        File generatedPngFile = new File(RESOURCE_OUTPUT_DIR, "test1.actual");
        if (generatedPngFile.exists()) {
            generatedPngFile.delete();
        }

        runDot(generatedDotFile, generatedPngFile);
        assertBinariesEqual(new File(RESOURCE_COMPARISON_DIR, "test1.expected.png"), new File(RESOURCE_OUTPUT_DIR, "test1.actual.png"));
    }

    public void testWithAttributes() throws IOException, GraphError {
        File generatedDotFile = new File(RESOURCE_OUTPUT_DIR,"test2.dot");
        if (generatedDotFile.exists()) {
            generatedDotFile.delete();
        }

        Plotter writer = new Plotter(false, PrintingStrategy.IGNORE_PREFIX);

        Graph graphResult = getContextCreatingBuilder()
                .node("p:Person")
                .attribute("p:Person", "name", Name.value("Patrick"))
                .attribute("p:Person", "age", Name.value(28))
                .attribute("p:Person", "isMale", Name.trueValue())
                .node("j:Job")
                .attribute("j:Job", "position", Name.value("PhD research fellow"))
                .attribute("j:Job", "employer", Name.value("Høgskulen på Vestlandet"))
                .edge("p:Person", "worksAt", "j:Job")
                .graph("G")
                .getResult(Graph.class);

        graphResult.accept(writer);
        writer.writeToFile(generatedDotFile);

        File generatedPngFile = new File(RESOURCE_OUTPUT_DIR, "test2.actual");
        if (generatedPngFile.exists()) {
            generatedPngFile.delete();
        }

        runDot(generatedDotFile, generatedPngFile);
        assertBinariesEqual(new File(RESOURCE_COMPARISON_DIR, "test2.expected.png"), new File(RESOURCE_OUTPUT_DIR, "test2.actual.png"));

    }

    public void testPlottingMorphism() throws IOException, GraphError {
        File generatedDotFile = new File(RESOURCE_OUTPUT_DIR,"test3.dot");
        if (generatedDotFile.exists()) {
            generatedDotFile.delete();
        }


        Plotter writer = new Plotter(false, PrintingStrategy.IGNORE_PREFIX);

        GraphMorphism result = getContextCreatingBuilder()
                .node("Person")
                .node("Employer")
                .node("Address")
                .node("String")
                .edge("Person", "worksAt", "Employer")
                .edge(id("Person"), id("has").prefixWith(id("Person")), id("Address"))
                .edge(id("Employer"), id("has").prefixWith(id("Employer")), id("Address"))
                .edge("Person", "name", "String")
                .graph("G")
                .node("Type")
                .node("Value")
                .edge("Type", "attribute", "Value")
                .edge("Type", "reference", "Type")
                .graph("TG")
                .map("Person", "Type")
                .map("Employer", "Type")
                .map("Address", "Type")
                .map("String", "Value")
                .morphism("typing")
                .getResult(GraphMorphism.class);

        result.accept(writer);
        writer.writeToFile(generatedDotFile);

        File generatedPngFile = new File(RESOURCE_OUTPUT_DIR, "test3.actual");
        if (generatedPngFile.exists()) {
            generatedPngFile.delete();
        }

        runDot(generatedDotFile, generatedPngFile);
        assertBinariesEqual(new File(RESOURCE_COMPARISON_DIR, "test3.expected.png"), new File(RESOURCE_OUTPUT_DIR, "test3.actual.png"));

    }

    public void testPlottingSketches() throws GraphError, IOException {
        File generatedDotFile = new File(RESOURCE_OUTPUT_DIR,"test4.dot");
        if (generatedDotFile.exists()) {
            generatedDotFile.delete();
        }

        DataOperation greater18 = new DataOperation() {
            @Override
            public String name() {
                return "age >= 18";
            }

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Value applyImplementation(Value[] arguments) {
                Value[] left = new Value[2];
                left[0] = Name.value(23);
                left[1] = arguments[0];
                Value[] right = new Value[2];
                right[0] = arguments[0];
                right[1] = Name.value(42);
                Value resultLeft = BuiltinOperations.LessOrEqual.getInstance().apply(left);
                Value resultRight = BuiltinOperations.LessOrEqual.getInstance().apply(right);
                Value[] finalArgs = new Value[2];
                finalArgs[0] = resultLeft;
                finalArgs[1] = resultRight;
                return BuiltinOperations.Conjunction.getInstance().apply(finalArgs);
            }

        };


        Plotter writer = new Plotter(false, PrintingStrategy.IGNORE_PREFIX);

        Sketch result = getContextCreatingBuilder()
                .node("Person")
                .node("Job")
                .node("CommunicationChannel")
                .node("PostalAddress")
                .node("Email")
                .node("Gender")
                .node("Repository")
                .edge("Repository", "persons", "Person")
                .edge("Repository", "channels", "CommunicationChannel")
                .edge("Repository", "jobs", "Job")
                .edge("PostalAddress", "street", "String")
                .edge("PostalAddress", "city", "String")
                .edge("PostalAddress", "zip", "String")
                .edge("Email", "address", "String")
                .edge("Person", "name", "String")
                .edge("Person", "age", "Int")
                .edge("Person", "gender", "Gender")
                .edge("Person", "worksAt", "Job")
                .edge("Job", "position", "String")
                .edge("Job", "employer", "String")
                .edge("CommunicationChannel", "owner", "Person")
                .edge("Person", "contacts", "CommunicationChannel")
                .map("PostalAddress", "CommunicationChannel")
                .map("Email", "CommunicationChannel")
                .graph(Name.identifier("Test").absolute())
                .startDiagram(StringDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("String"))
                .endDiagram(Name.identifier("d1"))
                .startDiagram(IntDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("Int"))
                .endDiagram(Name.identifier("d2"))
                .startDiagram(EnumValue.getInstance(Name.identifier("MALE"), Name.identifier("FEMALE")))
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("Gender"))
                .endDiagram(Name.identifier("d3"))
                .startDiagram(TargetMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Person"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("worksAt"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Job"))
                .endDiagram(Name.identifier("d4"))
                .startDiagram(TargetMultiplicity.getInstance(1, -1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("CommunicationChannel"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("owner"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Person"))
                .endDiagram(Name.identifier("d5"))
                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("CommunicationChannel"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("Person"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("owner"))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("contacts"))
                .endDiagram(Name.identifier("d6"))
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("persons"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Person"))
                .endDiagram(Name.identifier("d7"))
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("channels"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("CommunicationChannel"))
                .endDiagram(Name.identifier("d8"))
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("jobs"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Job"))
                .endDiagram(Name.identifier("d9"))
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("persons"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Person"))
                .endDiagram(Name.identifier("d10"))
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("channels"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("CommunicationChannel"))
                .endDiagram(Name.identifier("d11"))
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("jobs"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Job"))
                .endDiagram(Name.identifier("d12"))
                .startDiagram(AttributePredicate.getInstance(greater18))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Person"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("age"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Int"))
                .endDiagram(Name.identifier("Req1: Must be an adult!"))
                .sketch("Test")
                .getResult(Sketch.class);

        // TODO show all features of sketches
        result.accept(writer);
        writer.writeToFile(generatedDotFile);

        File generatedPngFile = new File(RESOURCE_OUTPUT_DIR, "test4.actual");
        if (generatedPngFile.exists()) {
            generatedPngFile.delete();
        }

        runDot(generatedDotFile, generatedPngFile);
    }
}
