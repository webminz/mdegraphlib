package no.hvl.past.graph.trees;

import no.hvl.past.graph.TestWithGraphLib;
import no.hvl.past.names.Name;
import no.hvl.past.util.Holder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class XMLTreeTest extends TestWithGraphLib {


    @Test
    public void testIsolatedCaseOnlyRootNoContent() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        XmlLibrary.XmlReaderConfig reader = XmlLibrary.getInstance().reader().treeName(id("test"));
        String onlyRootNoContent = "<a />";
        reader.read(onlyRootNoContent).emitEvents(actual);

        expected.startTree(id("test"));
        expected.startRoot(id("a"));
        expected.endRoot();
        expected.endTree();

        assertEquals(expected, actual);
    }

    @Test
    public void testIsolatedCaseOnlyRootTextContent() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        XmlLibrary.XmlReaderConfig reader = XmlLibrary.getInstance().reader().treeName(id("test"));
        String onlyRootTextContent = "<a>Hei!</a>";
        reader.read(onlyRootTextContent).emitEvents(actual);

        expected.startTree(id("test"));
        expected.startRoot(id("a"));
        expected.valueLeaf(Name.value("Hei!"));
        expected.endRoot();
        expected.endTree();

        assertEquals(expected, actual);
    }


    @Test
    public void testIsolatedCaseOnlyRootWithAtt() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        XmlLibrary.XmlReaderConfig reader = XmlLibrary.getInstance().reader().treeName(id("test"));
        String onlyRootOneAtt = "<a att=\"value\" />";
        reader.read(onlyRootOneAtt).emitEvents(actual);

        expected.startTree(id("test"));
        expected.startRoot(id("a"));
        expected.startBranch(id("att"), false);
        expected.valueLeaf(Name.value("value"));
        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        assertEquals(expected, actual);
    }

    @Test
    public void testIsolatedCaseTwoChildren() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        XmlLibrary.XmlReaderConfig reader = XmlLibrary.getInstance().reader().treeName(id("test"));
        String rootTwoChildren = "<a><b>x</b><c val=\"y\" /></a>";
        reader.read(rootTwoChildren).emitEvents(actual);

        expected.startTree(id("test"));
        expected.startRoot(id("a"));
        expected.startBranch(id("b"), true);
        expected.valueLeaf(Name.value("x"));
        expected.endBranch();
        expected.startBranch(id("c"), true);
        expected.startComplexNode();
        expected.startBranch(id("val"), false);
        expected.valueLeaf(Name.value("y"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        assertEquals(expected, actual);
    }

    @Test
    public void testIsolatedRootTwoChildrenWithSameName() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        XmlLibrary.XmlReaderConfig reader = XmlLibrary.getInstance().reader().treeName(id("test"));

        String rootTwoChildrenSameName = "<a><b>x</b><b>y</b></a>";
        reader.read(rootTwoChildrenSameName).emitEvents(actual);


        expected.startTree(id("test"));
        expected.startRoot(id("a"));
        expected.startBranch(id("b"), true);
        expected.valueLeaf(Name.value("x"));
        expected.valueLeaf(Name.value("y"));
        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        assertEquals(expected, actual);
    }

    @Test
    public void testIsolatedABitDeeperTree() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        XmlLibrary.XmlReaderConfig reader = XmlLibrary.getInstance().reader().treeName(id("test"));

        String aBitDeeper = "<a><b><c>x</c><d>y</d></b><b><e>z</e></b></a>";
        reader.read(aBitDeeper).emitEvents(actual);


        expected.startTree(id("test"));
        expected.startRoot(id("a"));
        expected.startBranch(id("b"), true);

        expected.startComplexNode();
        expected.startBranch(id("c"), true);
        expected.valueLeaf(Name.value("x"));
        expected.endBranch();
        expected.startBranch(id("d"), true);
        expected.valueLeaf(Name.value("y"));
        expected.endBranch();
        expected.endComplexNode();

        expected.startComplexNode();
        expected.startBranch(id("e"), true);
        expected.valueLeaf(Name.value("z"));
        expected.endBranch();
        expected.endComplexNode();

        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        assertEquals(expected, actual);
    }

    @Test
    public void testIsolatedLikeABitDeeperButWithAtts() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        XmlLibrary.XmlReaderConfig reader = XmlLibrary.getInstance().reader().treeName(id("test"));

        String likeABitDeeperButAtts = "<a><b c=\"x\" d=\"y\" /><b e=\"z\" /></a>";
        reader.read(likeABitDeeperButAtts).emitEvents(actual);

        expected.startTree(id("test"));
        expected.startRoot(id("a"));
        expected.startBranch(id("b"), true);

        expected.startComplexNode();
        expected.startBranch(id("c"), false);
        expected.valueLeaf(Name.value("x"));
        expected.endBranch();
        expected.startBranch(id("d"), false);
        expected.valueLeaf(Name.value("y"));
        expected.endBranch();
        expected.endComplexNode();

        expected.startComplexNode();
        expected.startBranch(id("e"), false);
        expected.valueLeaf(Name.value("z"));
        expected.endBranch();
        expected.endComplexNode();

        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        assertEquals(expected, actual);
    }

    @Test
    public void testIsolatedSameTagNested() throws Exception {
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        XmlLibrary.XmlReaderConfig reader = XmlLibrary.getInstance().reader().treeName(id("test"));

        String input = "<root><nested id=\"1\"><nested id=\"2\"><nested id=\"3\" /></nested><nested id=\"4\" /></nested></root>";

        reader.read(input).emitEvents(actual);

        expected.startTree(id("test"));
        expected.startRoot(id("root"));
            expected.startBranch(id("nested"), true);

                expected.startComplexNode();
                    expected.startBranch(id("id"), false);
                        expected.valueLeaf(Name.value("1"));
                    expected.endBranch();
                    expected.startBranch(id("nested"), true);

                        expected.startComplexNode();
                            expected.startBranch(id("id"),false);
                                expected.valueLeaf(Name.value("2"));
                            expected.endBranch();
                            expected.startBranch(id("nested"), true);
                                expected.startComplexNode();
                                    expected.startBranch(id("id"), false);
                                        expected.valueLeaf(Name.value("3"));
                                    expected.endBranch();
                                expected.endComplexNode();
                            expected.endBranch();
                        expected.endComplexNode();

                        expected.startComplexNode();
                            expected.startBranch(id("id"), false);
                                expected.valueLeaf(Name.value("4"));
                            expected.endBranch();
                        expected.endComplexNode();

                    expected.endBranch();
                expected.endComplexNode();


            expected.endBranch();
        expected.endRoot();
        expected.endTree();

        assertEquals(expected, actual);
    }


    @Test
    public void testSimpleXML() throws Exception {
        String input = "<a>\n" +
                "\t<b>\n" +
                "\t\t<c>foo</c>\n" +
                "\t</b>\n" +
                "\t<b e=\"bar\" />\n" +
                "\t<d>\n" +
                "\t\t<c>baz</c>\n" +
                "\t</d>\n" +
                "</a>";

        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();
        expected.startTree(id("test"));
        expected.startRoot(Name.identifier("a"));
        expected.startBranch(id("b"), true);
        expected.startComplexNode();
        expected.startBranch(id("c"), true);
        expected.valueLeaf(Name.value("foo"));
        expected.endBranch();
        expected.endComplexNode();
        expected.startComplexNode();
        expected.startBranch(id("e"), false);
        expected.valueLeaf(Name.value("bar"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.startBranch(id("d"), true);
        expected.startComplexNode();
        expected.startBranch(id("c"), true);
        expected.valueLeaf(Name.value("baz"));
        expected.endBranch();
        expected.endComplexNode();
        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeReceiver.Multiplexer handler = new TreeReceiver.Multiplexer(Collections.singletonList(actual));

        XmlLibrary.getInstance().reader().treeName(id("test")).read(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))).emitEvents(handler);

        assertEquals(expected, actual);
    }

    @Test
    public void testReadBooksIntoTree() throws Exception {
        Holder<Tree> resultHolder = new Holder<>();
        TreeReceiver creator = TreeBuilder.builderHandler(
                Collections.singletonList(new TreeElementNamingStrategy.ParentBased(
                        (parent, child) -> child.childOf(parent))),
                resultHolder::set);
        XmlLibrary.getInstance().reader().read(new File("src/test/resources/trees/books.xml")).emitEvents(creator);
        assertTrue(resultHolder.hasValue());
        Tree result = resultHolder.unsafeGet();
        assertEquals(3, result.root().depth());
        assertEquals(5, result.root().childNodesByKey(id("book").prefixWith(Name.uri("https://www.hvl.no/250/books"))).count());
        Node book = result.root().childNodesByKey(id("book").prefixWith(Name.uri("https://www.hvl.no/250/books"))).collect(Collectors.toList()).get(1);
        assertTrue( book.childNodesByKey(id("id")).findFirst().isPresent());
        assertEquals(Name.value("bk102"), book.childNodesByKey(id("id")).findFirst().get().elementName());
    }


    @Test
    public void testReadTablesIntoTree() throws Exception {
        Holder<Tree> resultHolder = new Holder<>();
        TreeReceiver creator = TreeBuilder.builderHandler(
                Collections.singletonList(new TreeElementNamingStrategy.ParentBased(
                        (parent, child) -> child.childOf(parent))),
                resultHolder::set);
        XmlLibrary.getInstance().reader().read(new File("src/test/resources/trees/marks.tables")).emitEvents(creator);
        assertTrue(resultHolder.hasValue());
        Tree marks = resultHolder.unsafeGet();

        Node root = marks.root();
        assertEquals(1, root.children().filter(Branch::isAttribute).count());

        List<Node> columnGroups = root.childNodesByKey(id("columnGroups")).collect(Collectors.toList());
        List<Node> rows = root.childNodesByKey(id("rows")).collect(Collectors.toList());
        assertEquals(4, columnGroups.size());
        Node firstCG = columnGroups.get(0);
        assertEquals(1, firstCG.keys().count());
        assertEquals(1, firstCG.childNodesByKey(id("columns")).count());
        assertEquals(Name.value("Student No"), firstCG.childNodesByKey(id("columns")).findFirst().get().attribute(id("title")).get());
        assertEquals(3, rows.size());
    }


}
