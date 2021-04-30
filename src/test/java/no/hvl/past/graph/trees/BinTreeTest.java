package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class BinTreeTest {

    private static final String LEFT = "LEFT";
    private static final String RIGHT = "RIGHT";

    private static class BTBranch implements Node {

        private Node parent;
        private boolean isLeft;
        private final Node left;
        private final Node right;
        private final Name name;

        public BTBranch(Node left, Node right, Name name) {
            this.left = left;
            this.right = right;
            this.name = name;
        }

        public void setParent(Node parent, boolean isLeftChild) {
            this.isLeft = isLeftChild;
            this.parent = parent;
        }

        @Override
        public Name elementName() {
            return name;
        }

        @Override
        public Optional<no.hvl.past.graph.trees.Branch> parentRelation() {
            if (parent != null) {
                return Optional.of(new no.hvl.past.graph.trees.Branch.Impl(parent, isLeft ? LEFT : RIGHT, this));
            }
            return Optional.empty();
        }

        @Override
        public Optional<Name> parentName() {
            return Optional.ofNullable(parent).map(Node::elementName);
        }

        @Override
        public Stream<Branch> children() {
            return Stream.of(
                     new Branch.Impl(this, LEFT, left),
                     new Branch.Impl(this, RIGHT, right));
        }


    }

    private static class Leaf implements Node {
        private final Name name;
        private Node parent;
        private boolean isLeft;

        public Leaf(Name name) {
            this.name = name;
        }

        public void setParent(Node parent, boolean isLeftChild) {
            this.isLeft = isLeftChild;
            this.parent = parent;
        }

        @Override
        public Name elementName() {
            return name;
        }

        @Override
        public Optional<no.hvl.past.graph.trees.Branch> parentRelation() {
            if (parent != null) {
                return Optional.of(new no.hvl.past.graph.trees.Branch.Impl(parent, isLeft ? LEFT : RIGHT, this));
            }
            return Optional.empty();
        }

        @Override
        public Stream<Branch> children() {
            return Stream.empty();
        }


    }

//    public static class BinTreeQueryNode implements QueryNode,QueryTree {
//
//        private final List<QueryNode> next;
//        private final Name filter;
//
//        public BinTreeQueryNode(Name filter) {
//            this.filter = filter;
//            this.next = new ArrayList<>();
//        }
//
//        public void add(BinTreeQueryNode next) {
//            this.next.add(next);
//        }
//
//        @Override
//        public Name filteredElementName() {
//            return filter;
//        }
//
//        @Override
//        public Formula<TypedVariables> filterPredicate() {
//            return Formula.top();
//        }
//
//        @Override
//        public Stream<QueryNode> children() {
//            return next.stream();
//        }
//
//        @Override
//        public QueryNode root() {
//            return this;
//        }
//
//
//        @Override
//        public String textualRepresentation() {
//            return filter.print(PrintingStrategy.IGNORE_PREFIX) + "/" + next.toString();
//        }
//    }

    @Test
    public void testSubTree() {
        Leaf l1 = new Leaf(Name.identifier("1"));
        Leaf l2 = new Leaf(Name.identifier("3"));
        Leaf l3 = new Leaf(Name.identifier("8"));
        BTBranch leftBranch = new BTBranch(l1, l2, Name.identifier("2"));
        BTBranch root = new BTBranch(leftBranch, l3, Name.identifier("5"));
        l1.setParent(leftBranch, true);
        l2.setParent(leftBranch, false);

        leftBranch.setParent(root, true);
        l3.setParent(root, false);
        Tree t = new Tree.Impl(root,Name.identifier("BinSearch"));
        Set<Name> expectedNames = new HashSet<>();
        expectedNames.add(Name.identifier("1"));
        expectedNames.add(Name.identifier("2"));
        expectedNames.add(Name.identifier("3"));
        expectedNames.add(Name.identifier("5"));
        expectedNames.add(Name.identifier("8"));
        assertEquals(expectedNames, t.nodes().collect(Collectors.toSet()));


        Set<Triple> expectedEdges = new HashSet<>();
        expectedEdges.add(Triple.edge(
                Name.identifier("2"),
                Name.identifier(LEFT).prefixWith( Name.identifier("2")),
                Name.identifier("1")
        ));
        expectedEdges.add(Triple.edge(
                Name.identifier("2"),
                Name.identifier(RIGHT).prefixWith( Name.identifier("2")),
                Name.identifier("3")
        ));
        expectedEdges.add(Triple.edge(
                Name.identifier("5"),
                Name.identifier(LEFT).prefixWith( Name.identifier("5")),
                Name.identifier("2")
        ));
        expectedEdges.add(Triple.edge(
                Name.identifier("5"),
                Name.identifier(RIGHT).prefixWith( Name.identifier("5")),
                Name.identifier("8")
        ));
        assertEquals(expectedEdges, t.edges().collect(Collectors.toSet()));
    }


//    @Test
//    public void testQuery() {
//        Leaf l1 = new Leaf(Name.value(1));
//        Leaf l2 = new Leaf(Name.value(3));
//        Leaf l3 = new Leaf(Name.value(8));
//        Branch leftBranch = new Branch(l1, l2, Name.value(2));
//        Branch root = new Branch(leftBranch, l3, Name.value(5));
//        l1.setParent(leftBranch);
//        l2.setParent(leftBranch);
//
//        leftBranch.setParent(root);
//        l3.setParent(root);
//        Tree t = new TreeImpl(Name.identifier("BinSearch"), root);
//
//        BinTreeQueryNode queryRoot = new BinTreeQueryNode(Name.identifier("/"));
//        BinTreeQueryNode q =new BinTreeQueryNode(LEFT);
//        queryRoot.add(q);
//        q.add(new BinTreeQueryNode(LEFT));
//        q.add(new BinTreeQueryNode(RIGHT));
//        Tree result = t.query(queryRoot);
//
//
//        Set<Name> expectedNames = new HashSet<>();
//        expectedNames.add(Name.value(1));
//        expectedNames.add(Name.value(3));
//        expectedNames.add(Name.value(2));
//        expectedNames.add(Name.value(5));
//        assertEquals(expectedNames, result.nodes().collect(Collectors.toSet()));
//
//
//        Set<Triple> expectedEdges = new HashSet<>();
//        expectedEdges.add(Triple.edge(
//                Name.value(2),
//                Name.value(1).childOf(Name.value(2)),
//                Name.value(1)
//        ));
//        expectedEdges.add(Triple.edge(
//                Name.value(2),
//                Name.value(3).childOf(Name.value(2)),
//                Name.value(3)
//        ));
//        expectedEdges.add(Triple.edge(
//                Name.value(5),
//                Name.value(2).childOf(Name.value(5)),
//                Name.value(2)
//        ));
//        assertEquals(expectedEdges, result.edges().collect(Collectors.toSet()));
//    }

}

