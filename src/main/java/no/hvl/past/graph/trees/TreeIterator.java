package no.hvl.past.graph.trees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TreeIterator  {

    private final List<Node> nexts = new ArrayList<>();

    private final boolean depthFirst;
    private final boolean onlyComplex;

    public TreeIterator(Node root, boolean depthFirst, boolean onlyComplex) {
        nexts.add(root);
        this.depthFirst = depthFirst;
        this.onlyComplex = onlyComplex;
    }

    public boolean hasNext() {
        return !nexts.isEmpty();
    }

    public Node next() {
        Node result = nexts.get(0);
        nexts.remove(0);
        if (depthFirst) {
            nexts.addAll(0, calculateNexts(result));
        } else {
            nexts.addAll(calculateNexts(result));
        }
        return result;
    }

    protected abstract boolean customFilter(Branch branch);

    private List<? extends Node> calculateNexts(Node node) {
        List<Node> result = new ArrayList<>();
        node.children().forEach(b -> {
            if (!onlyComplex || !b.child().elementName().isValue()) {
                if (customFilter(b)) {
                    result.add(b.child());
                }
            }
        });
        return result;
    }

    public static Iterator<Node> depthFirstUntypedAll(Node root) {
        TreeIterator treeIterator = new TreeIterator(root, true, false) {

            @Override
            protected boolean customFilter(Branch branch) {
                return true;
            }
        };
        return new Iterator<Node>() {
            @Override
            public boolean hasNext() {
                return treeIterator.hasNext();
            }

            @Override
            public Node next() {
                return treeIterator.next();
            }
        };
    }

    public static Iterator<Node> depthFirstTypedComplex(Node root) {
        TreeIterator treeIterator = new TreeIterator(root, true, true) {

            @Override
            protected boolean customFilter(Branch branch) {
                return branch.type().isPresent();
            }
        };
        return new Iterator<Node>() {
            @Override
            public boolean hasNext() {
                return treeIterator.hasNext();
            }

            @Override
            public Node next() {
                return treeIterator.next();
            }
        };
    }

}
