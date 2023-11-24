package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BinTreeTest {

    private static final Name LEFT = Name.identifier("LEFT");
    private static final Name RIGHT = Name.identifier("RIGHT");

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
        public Optional<Name> nodeType() {
            return Optional.empty();
        }


        public Optional<Name> parentNode() {
            if (parent != null) {
                return Optional.of(parent.elementName());
            }
            return Optional.empty();
        }

        @Override
        public Stream<Branch> children() {
            return Stream.of(
                     new NormalBranch(this.name, LEFT, left, null, null),
                     new NormalBranch(this.name, RIGHT, right, null, null));
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
        public Optional<Name> nodeType() {
            return Optional.empty();
        }

        @Override
        public Optional<Name> parentNode() {
            if (parent != null) {
                return Optional.of(parent.elementName());
            }
            return Optional.empty();
        }

        @Override
        public Stream<Branch> children() {
            return Stream.empty();
        }




    }


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
        Tree t = new NormalTree(root,Name.identifier("BinSearch"));
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
                LEFT.prefixWith( Name.identifier("2")),
                Name.identifier("1")
        ));
        expectedEdges.add(Triple.edge(
                Name.identifier("2"),
                RIGHT.prefixWith( Name.identifier("2")),
                Name.identifier("3")
        ));
        expectedEdges.add(Triple.edge(
                Name.identifier("5"),
                LEFT.prefixWith( Name.identifier("5")),
                Name.identifier("2")
        ));
        expectedEdges.add(Triple.edge(
                Name.identifier("5"),
                RIGHT.prefixWith( Name.identifier("5")),
                Name.identifier("8")
        ));
        assertEquals(expectedEdges, t.edges().collect(Collectors.toSet()));
    }


}

