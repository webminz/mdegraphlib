package no.hvl.past.graph.trees;

import no.hvl.past.graph.TestWithGraphLib;
import no.hvl.past.names.Name;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ReceiverTests extends TestWithGraphLib {


    @Test
    public void testFilters() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeReceiver.DeactivationFilter f0 = new TreeReceiver.DeactivationFilter(id("private"), true, actual);
        TreeReceiver.ActivationFilter f1 = new TreeReceiver.ActivationFilter(id("ob"), false, true, f0);
        TreeReceiver.ActivationFilter f2 = new TreeReceiver.ActivationFilter(id("wrap"), true, true, f1);

        f2.startRoot(Node.ROOT_NAME);
        f2.startBranch(id("wrap"), false);
        f2.startComplexNode();
        f2.startBranch(id("ob"), false);
        f2.startComplexNode();
        f2.startBranch(id("public"), false);
        f2.valueLeaf(Name.value("1"));
        f2.endBranch();
        f2.startBranch(id("private"), false);
        f2.valueLeaf(Name.value("2"));
        f2.endBranch();
        f2.endComplexNode();
        f2.endBranch();
        f2.endComplexNode();
        f2.endBranch();
        f2.endRoot();

        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(id("ob"), false);
        expected.startComplexNode();
        expected.startBranch(id("public"), false);
        expected.valueLeaf(Name.value("1"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.endRoot();

        assertEquals(expected, actual);
    }

}
