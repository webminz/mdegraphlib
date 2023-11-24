package no.hvl.past.graph.trees;

import no.hvl.past.graph.*;
import no.hvl.past.names.Name;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TreeBuilderTest extends TestWithGraphLib {

    @Test
    public void testSimpleUntyped() throws IOException {
        TreeBuilder treeBuilder = new TreeBuilder();
        Tree t = treeBuilder.root(Node.ROOT_NAME)
                        .branch("right")
                            .complexChild(Name.anonymousIdentifier())
                                .branch("attribute")
                                    .simpleChild(Name.identifier("value"))
                                .endBranch()
                            .endNode()
                        .endBranch()
                        .branch("left")
                            .simpleChild(Name.value(23))
                        .endBranch()
                    .endRoot()
                .build();
        assertEquals(3, t.root().depth());

        Node root = t.root();
        assertEquals(2, root.children().count());
        assertTrue(root.childNodesByKey(id("right")).findFirst().isPresent());
        Node right = root.childNodesByKey(id("right")).findFirst().get();
        assertEquals(2, right.depth());
        assertEquals(1, right.children().count());
        assertTrue(right.isComplex());
        assertEquals(Optional.of(Name.identifier("value")), right.attribute(id("attribute")));
        assertTrue(root.childNodesByKey(id("left")).findFirst().isPresent());
        Node left = root.childNodesByKey(id("left")).findFirst().get();
        assertEquals(0, left.children().count());
        assertEquals(1, left.depth());
        assertTrue(left.isSimple());
        assertEquals(Name.value(23), left.elementName());

        Node rightAttributeLeaf = new NormalNode(Name.identifier("value"));
        NormalNode rightNode = new NormalNode(Name.anonymousIdentifier());
        rightNode.addBranch(new NormalBranch(rightNode.elementName(), id("attribute"), rightAttributeLeaf, null, null));
        Node leftLeaf = new NormalNode(Name.value(23));
        NormalNode root2 = new NormalNode(Node.ROOT_NAME);
        root2.addBranch(new NormalBranch(root2.elementName(), id("right"), rightNode, null, null));
        root2.addBranch(new NormalBranch(root2.elementName(), id("left"), leftLeaf, null, null));
        assertTrue(t.root().structurallyEquivalent(root2));
    }


    @Test
    public void testTyped() {
        Graph result = getContextCreatingBuilder()
                .node("O")
                .node("V")
                .edge("O", "r", "O")
                .edge("O", "a", "V")
                .graph("Typ")
                .getResult(Graph.class);

        Tree t = new TreeBuilder()
                .typedOver(result)
                .root(Node.ROOT_NAME)
                .typedOver(id("O"))
                .branch("name")
                .typedOver(t("O", "a", "V"))
                .simpleChild(Name.value("Patrick"))
                .endBranch()
                .branch("age")
                .typedOver(t("O", "a", "V"))
                .simpleChild(Name.value(29))
                .endBranch()
                .branch("countries")
                .typedOver(t("O", "r", "O"))
                .complexChild(Name.anonymousIdentifier())
                .branch("name")
                .typedOver(t("O", "a", "V"))
                .simpleChild(Name.value("Germany"))
                .simpleChild(Name.value("Deutschland"))
                .endBranch()
                .endNode()
                .complexChild(Name.anonymousIdentifier())
                .branch("name")
                .typedOver(t("O", "a", "V"))
                .simpleChild(Name.value("Norway"))
                .simpleChild(Name.value("Norge"))
                .endBranch()
                .endNode()
                .endBranch()
                .endRoot()
                .build();

        Node root = t.root();
        assertEquals(4, root.children().count());
        assertEquals(2, root.childrenByType(t("O", "a", "V")).count());
        assertEquals(2, root.childrenByType(id("a")).count());
        assertTrue(root.childNodesByKey(id("age")).findFirst().isPresent());
        Node age = root.childNodesByKey(id("age")).findFirst().get();
        assertEquals(Optional.of(id("V")), age.nodeType());
        assertEquals(Name.value(29), age.elementName());

        assertEquals(2, root.childrenByKey(id("countries")).count());
        assertTrue(root.childrenByKey(id("countries")).allMatch(b -> b.type().isPresent() && b.type().get().equals(t("O", "r", "O"))));
        assertEquals(4, root.childNodesByKey(id("countries")).flatMap(n -> n.childNodesByKey(id("name"))).count());
        assertTrue(root.childNodesByKey(id("countries")).flatMap(n -> n.childNodesByKey(id("name"))).allMatch(n -> n.nodeType().isPresent() && n.nodeType().get().equals(id("V"))));


    }

    @Test
    public void testFromEventStream() {

    }
}
