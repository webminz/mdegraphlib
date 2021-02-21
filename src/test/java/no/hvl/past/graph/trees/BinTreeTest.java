package no.hvl.past.graph.trees;

import no.hvl.past.attributes.TypedVariables;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class BinTreeTest {

    private static final Name LEFT = Name.identifier("LEFT");
    private static final Name RIGHT = Name.identifier("RIGHT");

    private static class Branch implements Node {

        private Node parent;
        private final Node left;
        private final Node right;
        private final Name name;

        public Branch(Node left, Node right, Name name) {
            this.left = left;
            this.right = right;
            this.name = name;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        @Override
        public Name elementName() {
            return name;
        }

        @Override
        public Optional<Name> parentName() {
            return Optional.ofNullable(parent).map(Node::elementName);
        }

        @Override
        public Optional<Name> attribute(Name attributeName) {
            return Optional.empty();
        }

        @Override
        public Stream<Name> attributeNames() {
            return Stream.empty();
        }

        @Override
        public Stream<Node> children(Name childBranchName) {
            if (childBranchName.equals(LEFT)) {
                return Stream.of(left);
            } else if (childBranchName.equals(RIGHT)) {
                return Stream.of(right);
            } else {
                return Stream.empty();
            }
        }

        @Override
        public Stream<Name> childBranchNames() {
            return Stream.of(LEFT, RIGHT);
        }


    }

    private static class Leaf implements Node {
        private final Name name;
        private Node parent;

        public Leaf(Name name) {
            this.name = name;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }


        @Override
        public Name elementName() {
            return name;
        }

        @Override
        public Optional<Name> parentName() {
            return Optional.ofNullable(parent).map(Node::elementName);
        }

        @Override
        public Optional<Name> attribute(Name attributeName) {
            return Optional.empty();
        }

        @Override
        public Stream<Name> attributeNames() {
            return Stream.empty();
        }

        @Override
        public Stream<Node> children(Name childBranchName) {
            return Stream.empty();
        }

        @Override
        public Stream<Name> childBranchNames() {
            return Stream.empty();
        }


    }

    public static class BinTreeQueryNode implements QueryNode,QueryTree {

        private final List<QueryNode> next;
        private final Name filter;

        public BinTreeQueryNode(Name filter) {
            this.filter = filter;
            this.next = new ArrayList<>();
        }

        public void add(BinTreeQueryNode next) {
            this.next.add(next);
        }

        @Override
        public Name filteredElementName() {
            return filter;
        }

        @Override
        public Formula<TypedVariables> filterPredicate() {
            return Formula.top();
        }

        @Override
        public Stream<QueryNode> children() {
            return next.stream();
        }

        @Override
        public QueryNode root() {
            return this;
        }


        @Override
        public String textualRepresentation() {
            return filter.print(PrintingStrategy.IGNORE_PREFIX) + "/" + next.toString();
        }
    }

    @Test
    public void testSubTree() {
        Leaf l1 = new Leaf(Name.value(1));
        Leaf l2 = new Leaf(Name.value(3));
        Leaf l3 = new Leaf(Name.value(8));
        Branch leftBranch = new Branch(l1, l2, Name.value(2));
        Branch root = new Branch(leftBranch, l3, Name.value(5));
        l1.setParent(leftBranch);
        l2.setParent(leftBranch);

        leftBranch.setParent(root);
        l3.setParent(root);
        Tree t = new TreeImpl(Name.identifier("BinSearch"), root);
        Set<Name> expectedNames = new HashSet<>();
        expectedNames.add(Name.value(1));
        expectedNames.add(Name.value(3));
        expectedNames.add(Name.value(2));
        expectedNames.add(Name.value(5));
        expectedNames.add(Name.value(8));
        assertEquals(expectedNames, t.nodes().collect(Collectors.toSet()));


        Set<Triple> expectedEdges = new HashSet<>();
        expectedEdges.add(Triple.edge(
                Name.value(2),
                Name.value(1).childOf(Name.value(2)),
                Name.value(1)
        ));
        expectedEdges.add(Triple.edge(
                Name.value(2),
                Name.value(3).childOf(Name.value(2)),
                Name.value(3)
        ));
        expectedEdges.add(Triple.edge(
                Name.value(5),
                Name.value(2).childOf(Name.value(5)),
                Name.value(2)
        ));
        expectedEdges.add(Triple.edge(
                Name.value(5),
                Name.value(8).childOf(Name.value(5)),
                Name.value(8)
        ));
        assertEquals(expectedEdges, t.edges().collect(Collectors.toSet()));
    }


    @Test
    public void testQuery() {
        Leaf l1 = new Leaf(Name.value(1));
        Leaf l2 = new Leaf(Name.value(3));
        Leaf l3 = new Leaf(Name.value(8));
        Branch leftBranch = new Branch(l1, l2, Name.value(2));
        Branch root = new Branch(leftBranch, l3, Name.value(5));
        l1.setParent(leftBranch);
        l2.setParent(leftBranch);

        leftBranch.setParent(root);
        l3.setParent(root);
        Tree t = new TreeImpl(Name.identifier("BinSearch"), root);

        BinTreeQueryNode queryRoot = new BinTreeQueryNode(Name.identifier("/"));
        BinTreeQueryNode q =new BinTreeQueryNode(LEFT);
        queryRoot.add(q);
        q.add(new BinTreeQueryNode(LEFT));
        q.add(new BinTreeQueryNode(RIGHT));
        Tree result = t.query(queryRoot);


        Set<Name> expectedNames = new HashSet<>();
        expectedNames.add(Name.value(1));
        expectedNames.add(Name.value(3));
        expectedNames.add(Name.value(2));
        expectedNames.add(Name.value(5));
        assertEquals(expectedNames, result.nodes().collect(Collectors.toSet()));


        Set<Triple> expectedEdges = new HashSet<>();
        expectedEdges.add(Triple.edge(
                Name.value(2),
                Name.value(1).childOf(Name.value(2)),
                Name.value(1)
        ));
        expectedEdges.add(Triple.edge(
                Name.value(2),
                Name.value(3).childOf(Name.value(2)),
                Name.value(3)
        ));
        expectedEdges.add(Triple.edge(
                Name.value(5),
                Name.value(2).childOf(Name.value(5)),
                Name.value(2)
        ));
        assertEquals(expectedEdges, result.edges().collect(Collectors.toSet()));
    }

}

