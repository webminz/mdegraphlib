package no.hvl.past.graph.plotting;

import com.google.common.io.Files;
import no.hvl.past.graph.*;
import no.hvl.past.graph.predicates.BoolDT;
import no.hvl.past.graph.predicates.IntDT;
import no.hvl.past.graph.predicates.SourceMultiplicity;
import no.hvl.past.graph.predicates.StringDT;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assume.assumeTrue;

public class DOTPlottingTestcase extends AbstractGraphTest {

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

    @Test
    public void testSimpleDrawing() throws IOException, GraphError {
        assumeTrue(integrationTestsAllowed());

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

    @Test
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

    @Test
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

    @Test
    public void testPlottingSketches() throws GraphError, IOException {
        File generatedDotFile = new File(RESOURCE_OUTPUT_DIR,"test4.dot");
        if (generatedDotFile.exists()) {
            generatedDotFile.delete();
        }


        Plotter writer = new Plotter(false, PrintingStrategy.IGNORE_PREFIX);

        Sketch result = getContextCreatingBuilder()
                .node("Person")
                .node("Job")
                .edge("Person", "name", "String")
                .edge("Person", "age", "Int")
                .edge("Person", "isMale", "Bool")
                .edge("Person", "worksAt", "Job")
                .edge("Job", "position", "String")
                .edge("Job", "employer", "String")
                .graph(Name.identifier("Test").absolute())
                .startDiagram(StringDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("String"))
                .endDiagram(Name.identifier("d1"))
                .startDiagram(IntDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("Int"))
                .endDiagram(Name.identifier("d2"))
                .startDiagram(BoolDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("Bool"))
                .endDiagram(Name.identifier("d3"))
                .startDiagram(SourceMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Person"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("worksAt"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Job"))
                .endDiagram(Name.identifier("d4"))
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
