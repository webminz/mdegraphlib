package no.hvl.past.graph.trees;


import no.hvl.past.names.Name;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class YamlTest {


    @Test
    public void testReadingYaml() throws Exception {
        File file = new File("src/test/resources/trees/people.yaml");
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        YamlLibrary.getInstance()
                .reader()
                .rootName(Node.ROOT_NAME)
                .enableBundleWrapper(Name.identifier("bundle"))
                .read(file)
                .emitEvents(actual);

        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        fillInContent(Name.uri(file.toURI().toASCIIString()), expected);

        assertEquals(expected, actual);
    }

    private void fillInContent(Name treeName, TreeReceiver expected) throws Exception {
        expected.startTree(treeName);
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(Name.identifier("bundle"),true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("firstname"), false);
        expected.valueLeaf(Name.value("Hans"));
        expected.endBranch();
        expected.startBranch(Name.identifier("lastname"), false);
        expected.valueLeaf(Name.value("Hummel"));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), false);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Hauptstraße 12"));
        expected.valueLeaf(Name.value("12345 Berlin"));
        expected.valueLeaf(Name.value("GERMANY"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("DE"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("workedAt"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("name"), false);
        expected.valueLeaf(Name.value("Tante Emma Laden"));
        expected.endBranch();
        expected.startBranch(Name.identifier("durationInYears"), false);
        expected.valueLeaf(Name.value(2.7));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("DE"));
        expected.endBranch();
        expected.endComplexNode();

        expected.startComplexNode();
        expected.startBranch(Name.identifier("name"), false);
        expected.valueLeaf(Name.value("Die Ausbeuter AG"));
        expected.endBranch();
        expected.startBranch(Name.identifier("durationInYears"), false);
        expected.valueLeaf(Name.value(4));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("DE"));
        expected.endBranch();
        expected.endComplexNode();

        expected.startComplexNode();
        expected.startBranch(Name.identifier("name"), false);
        expected.valueLeaf(Name.value("Ausbeuter Investments SE"));
        expected.endBranch();
        expected.startBranch(Name.identifier("durationInYears"), false);
        expected.valueLeaf(Name.value(0.9));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("BE"));
        expected.endBranch();
        expected.endComplexNode();

        expected.endBranch();
        expected.endComplexNode();

        expected.startComplexNode();
        expected.startBranch(Name.identifier("firstname"), false);
        expected.valueLeaf(Name.value("Ola"));
        expected.endBranch();
        expected.startBranch(Name.identifier("lastname"), false);
        expected.valueLeaf(Name.value("Nordmann"));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), false);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Ygstebydgvegen 23"));
        expected.valueLeaf(Name.value("6969 Ørnøy"));
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NO"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("workedAt"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("name"), false);
        expected.valueLeaf(Name.value("Drikk og Drikk AS"));
        expected.endBranch();
        expected.startBranch(Name.identifier("durationInYears"), false);
        expected.valueLeaf(Name.value(33));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NO"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.endComplexNode();


        expected.endBranch();
        expected.endRoot();
        expected.endTree();
    }

    @Test
    public void testWriting() throws Exception {
        File out = new File("src/test/resources/output/people.yaml");
        if (out.exists()) {
            out.delete();
        }
        TreeReceiver receiver = YamlLibrary.getInstance()
                .writer()
                .write(new FileOutputStream(out));

        fillInContent(Name.anonymousIdentifier(), receiver);
        //TODO : file compare

    }

}
