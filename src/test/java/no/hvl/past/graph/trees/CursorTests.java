package no.hvl.past.graph.trees;

import no.hvl.past.graph.TestWithGraphLib;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.expressions.*;
import no.hvl.past.names.Name;
import no.hvl.past.util.Multiplicity;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;


import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CursorTests extends TestWithGraphLib {

    @Test
    public void testConstantCursor() throws Exception {
        TreeCollectCursor cursor = CursorConfig.rootConfig(Node.ROOT_NAME)
                .constant(Name.identifier("data"))
                .add(TreeEvent.startComplexNode())
                .add(TreeEvent.startBranch(Name.identifier("name"), false))
                .add(TreeEvent.valueLeaf(Name.value("Ole")))
                .add(TreeEvent.endBranch())
                .add(TreeEvent.startBranch(Name.identifier("age"), false))
                .add(TreeEvent.valueLeaf(Name.value(33)))
                .add(TreeEvent.endBranch())
                .add(TreeEvent.endComplexNode())
                .endCurrent()
                .create();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        cursor.emitEvents(actual);

        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(Name.identifier("data"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("name"), false);
        expected.valueLeaf(Name.value("Ole"));
        expected.endBranch();
        expected.startBranch(Name.identifier("age"), false);
        expected.valueLeaf(Name.value(33));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.endRoot();

        assertEquals(expected, actual);

    }

    @Test
    public void testCollectValueList() throws Exception {

        TreeCollectCursor cursor = CursorConfig.rootConfig(Node.ROOT_NAME)
                .channel("main", conf -> {})
                .leaf(Name.identifier("data"), true).endCurrent()
                .create();


        TreeReceiver leftReceiver = cursor.getReceiverForChannel("main");
        leftReceiver.startRoot(Node.ROOT_NAME);
        leftReceiver.startBranch(Name.identifier("data"), true);
        leftReceiver.valueLeaf(Name.value("a"));
        leftReceiver.valueLeaf(Name.value("b"));
        leftReceiver.valueLeaf(Name.value("c"));
        leftReceiver.endBranch();
        leftReceiver.endRoot();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        cursor.emitEvents(actual);

        TreeEmitter.SimpleCollector expted = new TreeEmitter.SimpleCollector();
        expted.startRoot(Node.ROOT_NAME);
        expted.startBranch(Name.identifier("data"), true);
        expted.valueLeaf(Name.value("a"));
        expted.valueLeaf(Name.value("b"));
        expted.valueLeaf(Name.value("c"));
        expted.endBranch();
        expted.endRoot();

        assertEquals(expted, actual);
    }

    @Test
    public void testFiltering() throws Exception {
        TreeCollectCursor cursor = CursorConfig.rootConfig(Node.ROOT_NAME)
                .channel("left", conf ->
                        conf.configRename(Name.identifier("objects"), Name.identifier("data"))
                                .enableLogging(Level.INFO)
                                .ingressFilter()
                                .waitForAnd(Name.identifier("bundle"))
                                .waitForAnd(Name.identifier("page"))
                                .allow(Name.identifier("objects"))
                                .ignore(Name.identifier("private"))
                )
                .channel("right", conf ->
                        conf.configRename(Name.identifier("branch1"), Name.identifier("data"))
                                .configRename(Name.identifier("branch3"), Name.identifier("data"))
                                .enableLogging(Level.INFO)
                                .ingressFilter()
                                .allow(Name.identifier("branch1")).endFilterBranch()
                                .allow(Name.identifier("branch3")))
                .leaf(Name.identifier("data"), true).endCurrent()
                .create();
        TreeReceiver left = cursor.getReceiverForChannel("left");
        left.startRoot(Node.ROOT_NAME);
        left.startBranch(id("bundle"), false);
        left.startComplexNode();
            left.startBranch(id("irrelevantMetaInfo"), false);
                left.valueLeaf(Name.value(42));
            left.endBranch();
            left.startBranch(id("page"), true);
                left.startComplexNode();
                    left.startBranch(id("pageNo"), false);
                    left.valueLeaf(Name.value(1));
                    left.endBranch();
                    left.startBranch(id("objects"), true);
                        left.startComplexNode();
                            left.startBranch(id("name"), false);
                            left.valueLeaf(Name.value("foo"));
                            left.endBranch();
                            left.startBranch(id("price"), false);
                            left.valueLeaf(Name.value(23.0));
                            left.endBranch();
                            left.startBranch(id("private"), false);
                            left.valueLeaf(Name.value("secret"));
                            left.endBranch();
                        left.endComplexNode();
                    left.endBranch();
                left.endComplexNode();
                left.startComplexNode();
                    left.startBranch(id("pageNo"), false);
                    left.valueLeaf(Name.value(2));
                    left.endBranch();
                    left.startBranch(id("objects"), true);
                        left.startComplexNode();
                        left.startBranch(id("name"), false);
                        left.valueLeaf(Name.value("bar"));
                        left.endBranch();
                        left.startBranch(id("price"), false);
                        left.valueLeaf(Name.value(3.141));
                        left.endBranch();
                        left.startBranch(id("private"), false);
                        left.valueLeaf(Name.value("porn"));
                        left.endBranch();
                        left.endComplexNode();
                    left.endBranch();
                left.endComplexNode();
            left.endBranch();
        left.endComplexNode();
        left.endBranch();
        left.endRoot();


        TreeReceiver right = cursor.getReceiverForChannel("right");

        right.startRoot(Node.ROOT_NAME);

        right.startBranch(id("branch1"), false);
            right.startComplexNode();
                right.startBranch(id("name"), false);
                right.valueLeaf(Name.value("baz"));
                right.endBranch();
                right.startBranch(id("price"), false);
                right.valueLeaf(Name.value(1.3333334));
                right.endBranch();
            right.endComplexNode();
        right.endBranch();

        right.startBranch(id("branch2"), false);
            right.startComplexNode();
                right.startBranch(id("name"), false);
                right.valueLeaf(Name.value("younoseemee"));
                right.endBranch();
                right.startBranch(id("price"), false);
                right.emptyLeaf();
                right.endBranch();
            right.endComplexNode();
        right.endBranch();

        right.startBranch(id("branch3"), false);
            right.startComplexNode();
                right.startBranch(id("name"), false);
                right.valueLeaf(Name.value("spam"));
                right.endBranch();
                right.startBranch(id("price"), false);
                right.valueLeaf(Name.value(6969));
                right.endBranch();
            right.endComplexNode();
        right.endBranch();

        right.endRoot();


        TreeReceiver expected = new TreeEmitter.SimpleCollector();
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(Name.identifier("data"), true);
            expected.startComplexNode();
                expected.startBranch(id("name"), false);
                expected.valueLeaf(Name.value("foo"));
                expected.endBranch();
                expected.startBranch(id("price"), false);
                expected.valueLeaf(Name.value(23.0));
                expected.endBranch();
            expected.endComplexNode();
            expected.startComplexNode();
                expected.startBranch(id("name"), false);
                expected.valueLeaf(Name.value("bar"));
                expected.endBranch();
                expected.startBranch(id("price"), false);
                expected.valueLeaf(Name.value(3.141));
                expected.endBranch();
            expected.endComplexNode();
            expected.startComplexNode();
                expected.startBranch(id("name"), false);
                expected.valueLeaf(Name.value("baz"));
                expected.endBranch();
                expected.startBranch(id("price"), false);
                expected.valueLeaf(Name.value(1.3333334));
                expected.endBranch();
            expected.endComplexNode();
                expected.startComplexNode();
                expected.startBranch(id("name"), false);
                expected.valueLeaf(Name.value("spam"));
                expected.endBranch();
                expected.startBranch(id("price"), false);
                expected.valueLeaf(Name.value(6969));
                expected.endBranch();
            expected.endComplexNode();
        expected.endBranch();
        expected.endRoot();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        cursor.emitEvents(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void testLeafCollectorWithValidtor() throws Exception {
        TreeCollectCursor cursor = CursorConfig.rootConfig(Node.ROOT_NAME)
                .channel("main", channelConfiguration -> channelConfiguration.errorStream("err"))
                .leaf(Name.identifier("data"), false).validate(Multiplicity.of(0,1)).endCurrent()
                .erros(Name.identifier("errors"), "err").asObject().messageName(Name.identifier("description")).location(Name.identifier("location")).endCurrent() // TODO include location
                .create();


        TreeReceiver receiver = cursor.getReceiverForChannel("main");

        receiver.startRoot(Node.ROOT_NAME);
            receiver.startBranch(Name.identifier("data"), false);
                receiver.startComplexNode();
                    receiver.startBranch(Name.identifier("id"), false);
                        receiver.valueLeaf(Name.value(1));
                    receiver.endBranch();
                receiver.endComplexNode();
                receiver.startComplexNode();
                    receiver.startBranch(Name.identifier("id"), false);
                        receiver.valueLeaf(Name.value(2));
                    receiver.endBranch();
                receiver.endComplexNode();
            receiver.endBranch();
        receiver.endRoot();

        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startRoot(Node.ROOT_NAME);
            expected.startBranch(Name.identifier("data"), false);
                expected.startComplexNode();
                    expected.startBranch(Name.identifier("id"), false);
                        expected.valueLeaf(Name.value(1));
                    expected.endBranch();
                expected.endComplexNode();
                expected.startComplexNode();
                    expected.startBranch(Name.identifier("id"), false);
                        expected.valueLeaf(Name.value(2));
                    expected.endBranch();
                expected.endComplexNode();
            expected.endBranch();
            expected.startBranch(Name.identifier("errors"), true);
                expected.startComplexNode();
                    expected.startBranch(Name.identifier("description"), false);
                        expected.valueLeaf(Name.value(new Multiplicity.MultiplicityViolation(Multiplicity.of(0, 1), 2).getMessage()));
                    expected.endBranch();
                    expected.startBranch(Name.identifier("location"), true);
                        expected.valueLeaf(Name.value("data"));
                        expected.valueLeaf(Name.value(2));
                    expected.endBranch();
                expected.endComplexNode();
            expected.endBranch();
        expected.endRoot();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        cursor.emitEvents(actual);

        assertEquals(expected, actual);

    }

    @Test
    public void testOnlyCollectingWithRenamingAndTwoChannels() throws Exception {

        TreeCollectCursor rootCursor =  CursorConfig.rootConfig(Node.ROOT_NAME)
                .setTreeName(Name.identifier("TEST"))
                .channel("left", conf -> conf
                                .configRename(Name.identifier("identifier"), Name.identifier("id"))
                                .configRename(Name.identifier("familyName"), Name.identifier("lastName")))
                .channel("right", conf -> conf
                                .configRename(Name.identifier("key"), Name.identifier("id")))
                .leaf(Name.identifier("data"),true).endCurrent()
                .create();


        TreeReceiver leftReceiver = rootCursor.getReceiverForChannel("left");
        leftReceiver.startRoot(Node.ROOT_NAME);
            leftReceiver.startBranch(Name.identifier("data"), true);
                leftReceiver.startComplexNode();
                    leftReceiver.startBranch(Name.identifier("identifier"), false);
                        leftReceiver.valueLeaf(Name.value(12345));
                    leftReceiver.endBranch();
                    leftReceiver.startBranch(Name.identifier("name"), false);
                        leftReceiver.startComplexNode();
                            leftReceiver.startBranch(Name.identifier("familyName"), false);
                                leftReceiver.valueLeaf(Name.value("Strudel"));
                            leftReceiver.endBranch();
                        leftReceiver.endComplexNode();
                    leftReceiver.endBranch();
                leftReceiver.endComplexNode();
            leftReceiver.endBranch();
        leftReceiver.endRoot();


        TreeReceiver rightReceiver = rootCursor.getReceiverForChannel("right");
        rightReceiver.startRoot(Node.ROOT_NAME);
            rightReceiver.startBranch(Name.identifier("data"), true);
                rightReceiver.startComplexNode();
                    rightReceiver.startBranch(Name.identifier("key"),false);
                        rightReceiver.valueLeaf(Name.value(6969));
                    rightReceiver.endBranch();
                    rightReceiver.startBranch(Name.identifier("isFemale"), false);
                        rightReceiver.valueLeaf(Name.falseValue());
                    rightReceiver.endBranch();
                rightReceiver.endComplexNode();
            rightReceiver.endBranch();
        rightReceiver.endRoot();

        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startTree(Name.identifier("TEST"));
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(Name.identifier("data"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("id"), false);
        expected.valueLeaf(Name.value(12345));
        expected.endBranch();
        expected.startBranch(Name.identifier("name"), false);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lastName"), false);
        expected.valueLeaf(Name.value("Strudel"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.endComplexNode();
        expected.startComplexNode();
        expected.startBranch(Name.identifier("id"),false);
        expected.valueLeaf(Name.value(6969));
        expected.endBranch();
        expected.startBranch(Name.identifier("isFemale"), false);
        expected.valueLeaf(Name.falseValue());
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        rootCursor.emitEvents(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void testOneObjectCollector() throws Exception {

        TreeCollectCursor rootCursor = CursorConfig.rootConfig(Node.ROOT_NAME)
                                .channel("purchases", conf -> conf.configRename(Name.identifier("customerId"), Name.identifier("partnerId")))
                                .channel("invoices", conf -> conf.configRename(Name.identifier("clientId"), Name.identifier("partnerId")).configWhitelist(Name.identifier("invoices")))
                                .oneObject(Name.identifier("data"))
                                        .leaf(Name.identifier("partnerId"), false).enableCompacting().endCurrent()
                                        .leaf(Name.identifier("address"), false).endCurrent()
                                        .leaf(Name.identifier("orders"), true).endCurrent()
                                        .leaf(Name.identifier("invoices"), true).endCurrent()
                                .endCurrent()
                .create();


        TreeReceiver purchasesReceiver = rootCursor.getReceiverForChannel("purchases");
        purchasesReceiver.startRoot(Node.ROOT_NAME);
            purchasesReceiver.startBranch(Name.identifier("data"), false);
                purchasesReceiver.startComplexNode();
                    purchasesReceiver.startBranch(Name.identifier("customerId"), false);
                        purchasesReceiver.valueLeaf(Name.value(42));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("address"), false);
                        purchasesReceiver.startComplexNode();
                            purchasesReceiver.startBranch(Name.identifier("lines"), true);
                                purchasesReceiver.valueLeaf(Name.value("Gjernesvegen 103"));
                                purchasesReceiver.valueLeaf(Name.value("5700 VOSS"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("country"), false);
                                purchasesReceiver.valueLeaf(Name.value("NORWAY"));
                            purchasesReceiver.endBranch();
                        purchasesReceiver.endComplexNode();
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("orders"), true);
                        purchasesReceiver.valueLeaf(Name.value("order1"));
                        purchasesReceiver.valueLeaf(Name.value("order2"));
                        purchasesReceiver.valueLeaf(Name.value("order3"));
                    purchasesReceiver.endBranch();
                purchasesReceiver.endComplexNode();
            purchasesReceiver.endBranch();
        purchasesReceiver.endRoot();

        TreeReceiver invoicesReceiver = rootCursor.getReceiverForChannel("invoices");
        invoicesReceiver.startRoot(Node.ROOT_NAME);
            invoicesReceiver.startBranch(Name.identifier("data"), false);
                invoicesReceiver.startComplexNode();
                    invoicesReceiver.startBranch(Name.identifier("clientId"), false);
                        invoicesReceiver.valueLeaf(Name.value(42));
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("invoices"), true);
                        invoicesReceiver.valueLeaf(Name.value("invoiceA"));
                        invoicesReceiver.valueLeaf(Name.value("invoiceB"));
                    invoicesReceiver.endBranch();
                invoicesReceiver.endComplexNode();
            invoicesReceiver.endBranch();
        invoicesReceiver.endRoot();

        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(Name.identifier("data"), false);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("partnerId"), false);
        expected.valueLeaf(Name.value(42));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), false);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Gjernesvegen 103"));
        expected.valueLeaf(Name.value("5700 VOSS"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.valueLeaf(Name.value("order1"));
        expected.valueLeaf(Name.value("order2"));
        expected.valueLeaf(Name.value("order3"));
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.valueLeaf(Name.value("invoiceA"));
        expected.valueLeaf(Name.value("invoiceB"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.endRoot();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        rootCursor.emitEvents(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testConcatenatingCursor() throws Exception {

        TreeCollectCursor rootCursor = CursorConfig.rootConfig(Node.ROOT_NAME)
                        .channel("purchases", conf -> conf.configRename(Name.identifier("customerId"), Name.identifier("partnerId")))
                        .channel("invoices", conf -> conf.configRename(Name.identifier("clientId"), Name.identifier("partnerId")))
                        .objects(Name.identifier("data"))
                            .leaf(Name.identifier("partnerId"), false).endCurrent()
                            .leaf(Name.identifier("address"), true).endCurrent()
                            .leaf(Name.identifier("orders"), true).endCurrent()
                            .leaf(Name.identifier("invoices"), true).endCurrent()
                        .endCurrent()
                .create();


        // sending events
        TreeReceiver purchasesReceiver = rootCursor.getReceiverForChannel("purchases");
        purchasesReceiver.startRoot(Node.ROOT_NAME);
            purchasesReceiver.startBranch(Name.identifier("data"), true);
                // ob1
                purchasesReceiver.startComplexNode();
                    purchasesReceiver.startBranch(Name.identifier("customerId"), false);
                    purchasesReceiver.valueLeaf(Name.value(42));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("address"), false);
                        purchasesReceiver.startComplexNode();
                            purchasesReceiver.startBranch(Name.identifier("lines"), true);
                                purchasesReceiver.valueLeaf(Name.value("Gjernesvegen 103"));
                                purchasesReceiver.valueLeaf(Name.value("5700 VOSS"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("country"), false);
                                purchasesReceiver.valueLeaf(Name.value("NORWAY"));
                            purchasesReceiver.endBranch();
                        purchasesReceiver.endComplexNode();
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("orders"), true);
                        purchasesReceiver.valueLeaf(Name.value("order1"));
                        purchasesReceiver.valueLeaf(Name.value("order2"));
                        purchasesReceiver.valueLeaf(Name.value("order3"));
                    purchasesReceiver.endBranch();
                purchasesReceiver.endComplexNode();
                // ob2
                purchasesReceiver.startComplexNode();
                    purchasesReceiver.startBranch(Name.identifier("customerId"), false);
                        purchasesReceiver.valueLeaf(Name.value(23));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("address"), false);
                        purchasesReceiver.startComplexNode();
                            purchasesReceiver.startBranch(Name.identifier("lines"), true);
                                purchasesReceiver.valueLeaf(Name.value("Nøstegaten 81"));
                                purchasesReceiver.valueLeaf(Name.value("5080 BERGEN"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("country"), false);
                                purchasesReceiver.valueLeaf(Name.value("NORWAY"));
                            purchasesReceiver.endBranch();
                        purchasesReceiver.endComplexNode();
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("orders"), true);
                        purchasesReceiver.valueLeaf(Name.value("order4"));
                        purchasesReceiver.valueLeaf(Name.value("order5"));
                    purchasesReceiver.endBranch();
                purchasesReceiver.endComplexNode();
            purchasesReceiver.endBranch();
        purchasesReceiver.endRoot();

        TreeReceiver invoicesReceiver = rootCursor.getReceiverForChannel("invoices");
        invoicesReceiver.startRoot(Node.ROOT_NAME);
            invoicesReceiver.startBranch(Name.identifier("data"), true);
                invoicesReceiver.startComplexNode();
                    invoicesReceiver.startBranch(Name.identifier("clientId"), false);
                        invoicesReceiver.valueLeaf(Name.value(42));
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("address"), false);
                        invoicesReceiver.startComplexNode();
                            invoicesReceiver.startBranch(Name.identifier("lines"), true);
                                invoicesReceiver.valueLeaf(Name.value("Nøstegaten 81"));
                                invoicesReceiver.valueLeaf(Name.value("5080 BERGEN"));
                            invoicesReceiver.endBranch();
                            invoicesReceiver.startBranch(Name.identifier("country"), false);
                                invoicesReceiver.valueLeaf(Name.value("NORWAY"));
                            invoicesReceiver.endBranch();
                        invoicesReceiver.endComplexNode();
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("invoices"), true);
                        invoicesReceiver.valueLeaf(Name.value("invoiceA"));
                        invoicesReceiver.valueLeaf(Name.value("invoiceB"));
                    invoicesReceiver.endBranch();
                invoicesReceiver.endComplexNode();
            invoicesReceiver.endBranch();
        invoicesReceiver.endRoot();


        // expected events
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(Name.identifier("data"), true);

        expected.startComplexNode();
        expected.startBranch(Name.identifier("partnerId"), false);
        expected.valueLeaf(Name.value(42));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Gjernesvegen 103"));
        expected.valueLeaf(Name.value("5700 VOSS"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.valueLeaf(Name.value("order1"));
        expected.valueLeaf(Name.value("order2"));
        expected.valueLeaf(Name.value("order3"));
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.endBranch();
        expected.endComplexNode();


        expected.startComplexNode();
        expected.startBranch(Name.identifier("partnerId"), false);
        expected.valueLeaf(Name.value(23));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Nøstegaten 81"));
        expected.valueLeaf(Name.value("5080 BERGEN"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.valueLeaf(Name.value("order4"));
        expected.valueLeaf(Name.value("order5"));
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.endBranch();
        expected.endComplexNode();

        expected.startComplexNode();
        expected.startBranch(Name.identifier("partnerId"), false);
        expected.valueLeaf(Name.value(42));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Nøstegaten 81"));
        expected.valueLeaf(Name.value("5080 BERGEN"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.valueLeaf(Name.value("invoiceA"));
        expected.valueLeaf(Name.value("invoiceB"));
        expected.endBranch();
        expected.endComplexNode();

        expected.endBranch();
        expected.endRoot();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        rootCursor.emitEvents(actual);
        assertEquals(expected, actual);

    }

    @Test
    public void testMergingCursor() throws Exception {
        PropertyExpression expression = new PropertyExpression(Triple.edge(Name.identifier("Partner"), Name.identifier("partnerId"), Name.identifier("String")));

        TreeCollectCursor rootCursor = CursorConfig.rootConfig(Node.ROOT_NAME)
                .channel("purchases", conf -> conf.configRename(Name.identifier("customerId"), Name.identifier("partnerId")))
                .channel("invoices", conf -> conf.configRename(Name.identifier("clientId"), Name.identifier("partnerId")))
                .objects(Name.identifier("data"))
                                .configureLocalCache(cacheConfig -> cacheConfig
                                                .configureKeyExpression("purchases", expression)
                                                .configureKeyExpression("invoices", expression))
                    .leaf(Name.identifier("partnerId"), false).enableCompacting().endCurrent()
                    .leaf(Name.identifier("address"), true).endCurrent()
                    .leaf(Name.identifier("orders"), true).endCurrent()
                    .leaf(Name.identifier("invoices"), true).endCurrent()
                .endCurrent()
                .create();



        TreeReceiver purchasesReceiver = rootCursor.getReceiverForChannel("purchases");
        purchasesReceiver.startRoot(Node.ROOT_NAME);
            purchasesReceiver.startBranch(Name.identifier("data"), true);
                // ob1
                purchasesReceiver.startComplexNode();
                    purchasesReceiver.startBranch(Name.identifier("customerId"), false);
                        purchasesReceiver.valueLeaf(Name.value(42));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("address"), false);
                        purchasesReceiver.startComplexNode();
                            purchasesReceiver.startBranch(Name.identifier("lines"), true);
                                purchasesReceiver.valueLeaf(Name.value("Gjernesvegen 103"));
                                purchasesReceiver.valueLeaf(Name.value("5700 VOSS"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("country"), false);
                                purchasesReceiver.valueLeaf(Name.value("NORWAY"));
                            purchasesReceiver.endBranch();
                        purchasesReceiver.endComplexNode();
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("orders"), true);
                        purchasesReceiver.valueLeaf(Name.value("order1"));
                        purchasesReceiver.valueLeaf(Name.value("order2"));
                        purchasesReceiver.valueLeaf(Name.value("order3"));
                    purchasesReceiver.endBranch();
                purchasesReceiver.endComplexNode();
                // ob2
                purchasesReceiver.startComplexNode();
                    purchasesReceiver.startBranch(Name.identifier("customerId"), false);
                        purchasesReceiver.valueLeaf(Name.value(23));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("address"), false);
                        purchasesReceiver.startComplexNode();
                            purchasesReceiver.startBranch(Name.identifier("lines"), true);
                                purchasesReceiver.valueLeaf(Name.value("Nøstegaten 81"));
                                purchasesReceiver.valueLeaf(Name.value("5080 BERGEN"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("country"), false);
                                purchasesReceiver.valueLeaf(Name.value("NORWAY"));
                            purchasesReceiver.endBranch();
                        purchasesReceiver.endComplexNode();
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("orders"), true);
                        purchasesReceiver.valueLeaf(Name.value("order4"));
                        purchasesReceiver.valueLeaf(Name.value("order5"));
                    purchasesReceiver.endBranch();
                purchasesReceiver.endComplexNode();
            purchasesReceiver.endBranch();
        purchasesReceiver.endRoot();

        TreeReceiver invoicesReceiver = rootCursor.getReceiverForChannel("invoices");
        invoicesReceiver.startRoot(Node.ROOT_NAME);
            invoicesReceiver.startBranch(Name.identifier("data"),true);
                invoicesReceiver.startComplexNode();
                    invoicesReceiver.startBranch(Name.identifier("clientId"), false);
                        invoicesReceiver.valueLeaf(Name.value(42));
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("address"), false);
                        invoicesReceiver.startComplexNode();
                        invoicesReceiver.startBranch(Name.identifier("lines"), true);
                            invoicesReceiver.valueLeaf(Name.value("Gjernesvegen 103"));
                            invoicesReceiver.valueLeaf(Name.value("5700 VOSS"));
                        invoicesReceiver.endBranch();
                        invoicesReceiver.startBranch(Name.identifier("country"), false);
                            invoicesReceiver.valueLeaf(Name.value("NORWAY"));
                        invoicesReceiver.endBranch();
                        invoicesReceiver.endComplexNode();
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("invoices"), true);
                        invoicesReceiver.valueLeaf(Name.value("invoiceA"));
                        invoicesReceiver.valueLeaf(Name.value("invoiceB"));
                    invoicesReceiver.endBranch();
                invoicesReceiver.endComplexNode();
            invoicesReceiver.endBranch();
        invoicesReceiver.endRoot();


        // expected events
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(Name.identifier("data"), true);

        expected.startComplexNode();
        expected.startBranch(Name.identifier("partnerId"), false);
        expected.valueLeaf(Name.value(42));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Gjernesvegen 103"));
        expected.valueLeaf(Name.value("5700 VOSS"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Gjernesvegen 103"));
        expected.valueLeaf(Name.value("5700 VOSS"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.valueLeaf(Name.value("order1"));
        expected.valueLeaf(Name.value("order2"));
        expected.valueLeaf(Name.value("order3"));
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.valueLeaf(Name.value("invoiceA"));
        expected.valueLeaf(Name.value("invoiceB"));
        expected.endBranch();
        expected.endComplexNode();


        expected.startComplexNode();
        expected.startBranch(Name.identifier("partnerId"), false);
        expected.valueLeaf(Name.value(23));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("lines"), true);
        expected.valueLeaf(Name.value("Nøstegaten 81"));
        expected.valueLeaf(Name.value("5080 BERGEN"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.valueLeaf(Name.value("order4"));
        expected.valueLeaf(Name.value("order5"));
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.endBranch();
        expected.endComplexNode();

        expected.endBranch();
        expected.endRoot();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        rootCursor.emitEvents(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testPurchasesInvoicesHRSimulation() throws Exception {
        PropertyExpression idExpr = new PropertyExpression(Triple.edge(Name.identifier("Partner"), Name.identifier("id"), Name.identifier("ID")));
        PropertyExpression firstrNameExpr = new PropertyExpression(Triple.edge(Name.identifier("Partner"), Name.identifier("firstname"), Name.identifier("String")));
        PropertyExpression lastNameExpr = new PropertyExpression(Triple.edge(Name.identifier("Partner"), Name.identifier("lastname"), Name.identifier("String")));
        PropertyExpression fullNameExpr = new PropertyExpression(Triple.edge(Name.identifier("Partner"), Name.identifier("fullname"), Name.identifier("String")));
        ConstantExpression fillerExpr = new ConstantExpression(Collections.singletonList(Name.value(" ")));
        ToStringConcatExpression concat = new ToStringConcatExpression(Arrays.asList(firstrNameExpr, fillerExpr, lastNameExpr));


        PropertyExpression streetExpr = new PropertyExpression(Triple.edge(Name.identifier("Address"), Name.identifier("street"), Name.identifier("String")));
        PropertyExpression countryExpr = new PropertyExpression(Triple.edge(Name.identifier("Address"), Name.identifier("country"), Name.identifier("String")));
        PropertyExpression zipExpr = new PropertyExpression(Triple.edge(Name.identifier("Address"), Name.identifier("zip"), Name.identifier("String")));
        InternalAndConcatExpression andExpr = new InternalAndConcatExpression(Arrays.asList(streetExpr, countryExpr, zipExpr));


        TreeCollectCursor rootCursor = CursorConfig.rootConfig(Node.ROOT_NAME)
                .channel("purchases", config -> config.configRename(Name.identifier("name"), Name.identifier("fullname")))
                .channel("invoices", conf -> {})
                .channel("hr", conf -> conf.configBlacklist(Name.identifier("address")))
                .objects(Name.identifier("data"))
                .configureLocalCache(cacheConfig -> cacheConfig
                        .configureKeyExpression("purchases", new OrExpression(Arrays.asList(idExpr, fullNameExpr)))
                        .configureKeyExpression("invoices", idExpr)
                        .configureKeyExpression("hr", concat))
                .leaf(Name.identifier("id"), true).enableCompacting().endCurrent()
                .leaf(Name.identifier("fullname"), false).endCurrent()
                .leaf(Name.identifier("firstname"), false).endCurrent()
                .leaf(Name.identifier("lastname"), false).endCurrent()
                .objects(Name.identifier("address"))
                    .configureLocalCache(cacheConfig -> cacheConfig.configureKeyExpression("purchases", andExpr).configureKeyExpression("invoices", andExpr))
                    .leaf(Name.identifier("street"), false).enableCompacting().endCurrent()
                    .leaf(Name.identifier("zip"), false).enableCompacting().endCurrent()
                    .leaf(Name.identifier("country"), false).enableCompacting().endCurrent()
                .endCurrent()
                .leaf(Name.identifier("orders"), true).endCurrent()
                .leaf(Name.identifier("invoices"), true).endCurrent()
                .leaf(Name.identifier("worksAt"), false).endCurrent()
                .endCurrent()
                .create();






        TreeReceiver purchasesReceiver = rootCursor.getReceiverForChannel("purchases");


        purchasesReceiver.startRoot(Node.ROOT_NAME);
            purchasesReceiver.startBranch(Name.identifier("data"), true);
                // ob1
                purchasesReceiver.startComplexNode();
                    purchasesReceiver.startBranch(Name.identifier("id"), false);
                        purchasesReceiver.valueLeaf(Name.value(1));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("name"), false);
                        purchasesReceiver.valueLeaf(Name.value("Adrian Abel"));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("address"), false);
                        purchasesReceiver.startComplexNode();
                            purchasesReceiver.startBranch(Name.identifier("street"), true);
                                purchasesReceiver.valueLeaf(Name.value("Corner Street 13a"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("zip"), true);
                                purchasesReceiver.valueLeaf(Name.value("123-456"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("country"), false);
                                purchasesReceiver.valueLeaf(Name.value("UNITED STATES"));
                            purchasesReceiver.endBranch();
                        purchasesReceiver.endComplexNode();
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("orders"), true);
                        purchasesReceiver.valueLeaf(Name.value("order1"));
                        purchasesReceiver.valueLeaf(Name.value("order2"));
                        purchasesReceiver.valueLeaf(Name.value("order3"));
                    purchasesReceiver.endBranch();
                purchasesReceiver.endComplexNode();
                // ob2
                purchasesReceiver.startComplexNode();
                    purchasesReceiver.startBranch(Name.identifier("id"), false);
                        purchasesReceiver.valueLeaf(Name.value(2));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("name"), false);
                        purchasesReceiver.valueLeaf(Name.value("Bert Bertsen"));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("address"), false);
                        purchasesReceiver.startComplexNode();
                            purchasesReceiver.startBranch(Name.identifier("street"), true);
                                purchasesReceiver.valueLeaf(Name.value("Am Bahnhof 2"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("zip"), true);
                                purchasesReceiver.valueLeaf(Name.value("78912"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("country"), false);
                                purchasesReceiver.valueLeaf(Name.value("GERMANY"));
                            purchasesReceiver.endBranch();
                        purchasesReceiver.endComplexNode();
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("orders"), true);
                        purchasesReceiver.valueLeaf(Name.value("order4"));
                        purchasesReceiver.valueLeaf(Name.value("order5"));
                    purchasesReceiver.endBranch();
                purchasesReceiver.endComplexNode();
                // ob 3
                purchasesReceiver.startComplexNode();
                    purchasesReceiver.startBranch(Name.identifier("id"), false);
                    purchasesReceiver.valueLeaf(Name.value(3));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("name"), false);
                    purchasesReceiver.valueLeaf(Name.value("Claire Claroux"));
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("address"), false);
                        purchasesReceiver.startComplexNode();
                            purchasesReceiver.startBranch(Name.identifier("street"), true);
                                purchasesReceiver.valueLeaf(Name.value("Dalsvegen 3208"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("zip"), true);
                                purchasesReceiver.valueLeaf(Name.value("6969"));
                            purchasesReceiver.endBranch();
                            purchasesReceiver.startBranch(Name.identifier("country"), false);
                                purchasesReceiver.valueLeaf(Name.value("NORWAY"));
                            purchasesReceiver.endBranch();
                        purchasesReceiver.endComplexNode();
                    purchasesReceiver.endBranch();
                    purchasesReceiver.startBranch(Name.identifier("orders"), true);
                        purchasesReceiver.valueLeaf(Name.value("order6"));
                        purchasesReceiver.valueLeaf(Name.value("order7"));
                        purchasesReceiver.valueLeaf(Name.value("order8"));
                    purchasesReceiver.endBranch();
                purchasesReceiver.endComplexNode();
            // end
            purchasesReceiver.endBranch();
        purchasesReceiver.endRoot();


        TreeReceiver invoicesReceiver = rootCursor.getReceiverForChannel("invoices");
        invoicesReceiver.startRoot(Node.ROOT_NAME);
            invoicesReceiver.startBranch(Name.identifier("data"), true);
                // ob1
                invoicesReceiver.startComplexNode();
                    invoicesReceiver.startBranch(Name.identifier("id"), false);
                        invoicesReceiver.valueLeaf(Name.value(1));
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("address"), false);
                        invoicesReceiver.startComplexNode();
                            invoicesReceiver.startBranch(Name.identifier("street"), true);
                                invoicesReceiver.valueLeaf(Name.value("Corner Street 13a"));
                            invoicesReceiver.endBranch();
                            invoicesReceiver.startBranch(Name.identifier("zip"), true);
                                invoicesReceiver.valueLeaf(Name.value("123-456"));
                            invoicesReceiver.endBranch();
                            invoicesReceiver.startBranch(Name.identifier("country"), false);
                                invoicesReceiver.valueLeaf(Name.value("UNITED STATES"));
                            invoicesReceiver.endBranch();
                        invoicesReceiver.endComplexNode();
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("invoices"), true);
                        invoicesReceiver.valueLeaf(Name.value("invoiceA"));
                        invoicesReceiver.valueLeaf(Name.value("invoiceB"));
                    invoicesReceiver.endBranch();
                invoicesReceiver.endComplexNode();
                // ob2
                invoicesReceiver.startComplexNode();
                    invoicesReceiver.startBranch(Name.identifier("id"), false);
                        invoicesReceiver.valueLeaf(Name.value(2));
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("address"), false);
                        invoicesReceiver.startComplexNode();
                            invoicesReceiver.startBranch(Name.identifier("street"), true);
                                invoicesReceiver.valueLeaf(Name.value("Am Bahnhof 2"));
                            invoicesReceiver.endBranch();
                            invoicesReceiver.startBranch(Name.identifier("zip"), true);
                                invoicesReceiver.valueLeaf(Name.value("78912"));
                            invoicesReceiver.endBranch();
                            invoicesReceiver.startBranch(Name.identifier("country"), false);
                                invoicesReceiver.valueLeaf(Name.value("GERMANY"));
                            invoicesReceiver.endBranch();
                        invoicesReceiver.endComplexNode();
                    invoicesReceiver.endBranch();
                    invoicesReceiver.startBranch(Name.identifier("invoices"), true);
                        invoicesReceiver.valueLeaf(Name.value("invoiceC"));
                    invoicesReceiver.endBranch();
                invoicesReceiver.endComplexNode();
                // end
            invoicesReceiver.endBranch();
        invoicesReceiver.endRoot();


        TreeReceiver hrReceiver = rootCursor.getReceiverForChannel("hr");
        hrReceiver.startRoot(Node.ROOT_NAME);
            hrReceiver.startBranch(Name.identifier("data"), true);
                // ob1
                hrReceiver.startComplexNode();
                    hrReceiver.startBranch(Name.identifier("id"), false);
                        hrReceiver.valueLeaf(Name.value(1));
                    hrReceiver.endBranch();
                    hrReceiver.startBranch(Name.identifier("firstname"), false);
                        hrReceiver.valueLeaf(Name.value("Dalton"));
                    hrReceiver.endBranch();
                    hrReceiver.startBranch(Name.identifier("lastname"), false);
                        hrReceiver.valueLeaf(Name.value("Dixon"));
                    hrReceiver.endBranch();
                    hrReceiver.startBranch(Name.identifier("worksAt"), false);
                        hrReceiver.valueLeaf(Name.value("Sales"));
                    hrReceiver.endBranch();
                hrReceiver.endComplexNode();
                // ob2
                hrReceiver.startComplexNode();
                    hrReceiver.startBranch(Name.identifier("id"), false);
                        hrReceiver.valueLeaf(Name.value(2));
                    hrReceiver.endBranch();
                    hrReceiver.startBranch(Name.identifier("firstname"), false);
                        hrReceiver.valueLeaf(Name.value("Adrian"));
                    hrReceiver.endBranch();
                    hrReceiver.startBranch(Name.identifier("lastname"), false);
                        hrReceiver.valueLeaf(Name.value("Abel"));
                    hrReceiver.endBranch();
                    hrReceiver.startBranch(Name.identifier("worksAt"), false);
                        hrReceiver.valueLeaf(Name.value("Support"));
                    hrReceiver.endBranch();
                hrReceiver.endComplexNode();
                // end
            hrReceiver.endBranch();
        hrReceiver.endRoot();


        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startRoot(Node.ROOT_NAME);
        expected.startBranch(Name.identifier("data"), true);

        // ob 1 = AA
        expected.startComplexNode();
        expected.startBranch(Name.identifier("id"), true);
        expected.valueLeaf(Name.value(1));
        expected.valueLeaf(Name.value(2));
        expected.endBranch();
        expected.startBranch(Name.identifier("fullname"), false);
        expected.valueLeaf(Name.value("Adrian Abel"));
        expected.endBranch();
        expected.startBranch(Name.identifier("firstname"), false);
        expected.valueLeaf(Name.value("Adrian"));
        expected.endBranch();
        expected.startBranch(Name.identifier("lastname"), false);
        expected.valueLeaf(Name.value("Abel"));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("street"), false);
        expected.valueLeaf(Name.value("Corner Street 13a"));
        expected.endBranch();
        expected.startBranch(Name.identifier("zip"), false);
        expected.valueLeaf(Name.value("123-456"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("UNITED STATES"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.valueLeaf(Name.value("order1"));
        expected.valueLeaf(Name.value("order2"));
        expected.valueLeaf(Name.value("order3"));
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.valueLeaf(Name.value("invoiceA"));
        expected.valueLeaf(Name.value("invoiceB"));
        expected.endBranch();
        expected.startBranch(Name.identifier("worksAt"), false);
        expected.valueLeaf(Name.value("Support"));
        expected.endBranch();
        expected.endComplexNode();
        // ob 2 = BB
        expected.startComplexNode();
        expected.startBranch(Name.identifier("id"), true);
        expected.valueLeaf(Name.value(2));
        expected.endBranch();
        expected.startBranch(Name.identifier("fullname"), false);
        expected.valueLeaf(Name.value("Bert Bertsen"));
        expected.endBranch();
        expected.startBranch(Name.identifier("firstname"), false);
        expected.emptyLeaf();
        expected.endBranch();
        expected.startBranch(Name.identifier("lastname"), false);
        expected.emptyLeaf();
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("street"), false);
        expected.valueLeaf(Name.value("Am Bahnhof 2"));
        expected.endBranch();
        expected.startBranch(Name.identifier("zip"), false);
        expected.valueLeaf(Name.value("78912"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("GERMANY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.valueLeaf(Name.value("order4"));
        expected.valueLeaf(Name.value("order5"));
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.valueLeaf(Name.value("invoiceC"));
        expected.endBranch();
        expected.startBranch(Name.identifier("worksAt"), false);
        expected.emptyLeaf();
        expected.endBranch();
        expected.endComplexNode();
        // ob 3 = CC
        expected.startComplexNode();
        expected.startBranch(Name.identifier("id"), true);
        expected.valueLeaf(Name.value(3));
        expected.endBranch();
        expected.startBranch(Name.identifier("fullname"), false);
        expected.valueLeaf(Name.value("Claire Claroux"));
        expected.endBranch();
        expected.startBranch(Name.identifier("firstname"), false);
        expected.emptyLeaf();
        expected.endBranch();
        expected.startBranch(Name.identifier("lastname"), false);
        expected.emptyLeaf();
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.startComplexNode();
        expected.startBranch(Name.identifier("street"), false);
        expected.valueLeaf(Name.value("Dalsvegen 3208"));
        expected.endBranch();
        expected.startBranch(Name.identifier("zip"), false);
        expected.valueLeaf(Name.value("6969"));
        expected.endBranch();
        expected.startBranch(Name.identifier("country"), false);
        expected.valueLeaf(Name.value("NORWAY"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.valueLeaf(Name.value("order6"));
        expected.valueLeaf(Name.value("order7"));
        expected.valueLeaf(Name.value("order8"));
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.endBranch();
        expected.startBranch(Name.identifier("worksAt"), false);
        expected.emptyLeaf();
        expected.endBranch();
        expected.endComplexNode();
        // ob4 = DD
        expected.startComplexNode();
        expected.startBranch(Name.identifier("id"), true);
        expected.valueLeaf(Name.value(1));
        expected.endBranch();
        expected.startBranch(Name.identifier("fullname"), false);
        expected.emptyLeaf();
        expected.endBranch();
        expected.startBranch(Name.identifier("firstname"), false);
        expected.valueLeaf(Name.value("Dalton"));
        expected.endBranch();
        expected.startBranch(Name.identifier("lastname"), false);
        expected.valueLeaf(Name.value("Dixon"));
        expected.endBranch();
        expected.startBranch(Name.identifier("address"), true);
        expected.endBranch();
        expected.startBranch(Name.identifier("orders"), true);
        expected.endBranch();
        expected.startBranch(Name.identifier("invoices"), true);
        expected.endBranch();
        expected.startBranch(Name.identifier("worksAt"), false);
        expected.valueLeaf(Name.value("Sales"));
        expected.endBranch();
        expected.endComplexNode();


        expected.endBranch();
        expected.endRoot();
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        rootCursor.emitEvents(actual);
        assertEquals(expected, actual);

    }






    // TODO later: with injectors

}
