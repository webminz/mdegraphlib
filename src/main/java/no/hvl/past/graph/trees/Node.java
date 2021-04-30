package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a node in a tree.
 * A node may have a collection attributes (= key-value pairs where both are names)
 * A node may have a number of children, which are nodes.
 */
public interface Node {

    Name ROOT_NAME = Name.identifier("/");

    class Builder {

        Builder parentBuilder;
        Name elementName;
        private List<Branch.Builder> children;
        private Branch.Builder lastAdded;
        private int currentIdx = 0;
        private Node result;

        private Builder(Node node) {
            this.result = node;
        }

        public Builder() {
            this.elementName = ROOT_NAME;
            this.children = new ArrayList<>();
        }

        public Builder(Name elementName) {
            this.elementName = elementName;
            this.children = new ArrayList<>();
        }

        public Builder(Builder parentBuilder, Name elementName) {
            this.parentBuilder = parentBuilder;
            this.elementName = elementName;
            this.children = new ArrayList<>();
        }

        public Name getElementName() {
            return elementName;
        }

        public void changeElementName(Name elementName) {
            this.elementName = elementName;
        }

        public Node.Builder beginChild(String key, Node childNode) {
            handleIndexing(key);
            Builder childBuilder = new Builder(childNode);
            Branch.Builder rel = new Branch.Builder(key, childBuilder);
            addChild(rel);
            return childBuilder;
        }

        public Node.Builder beginChild(String key, Name elementName) {
            handleIndexing(key);
            Builder childBuilder = new Builder(this, elementName);
            Branch.Builder rel = new Branch.Builder(key, childBuilder);
            addChild(rel);
            return childBuilder;
        }

        void addChild(Branch.Builder rel) {
            lastAdded = rel;
            this.children.add(rel);
        }

        public int peekNextIndex(String key) {
            if (lastAdded == null) {
                return 0;
            }
            if (lastAdded.getKey().equals(key)) {
                return currentIdx + 1;
            }
            return 0;
        }

        void handleIndexing(String key) {
            if (lastAdded != null && lastAdded.getKey().equals(key)) {
                lastAdded.addIndex(currentIdx);
                currentIdx++;
            } else if (lastAdded != null && !lastAdded.getKey().equals(key)){
                if (currentIdx != 0) {
                    lastAdded.addIndex(currentIdx);
                }
                currentIdx = 0;
            }
        }

        public Node.Builder attribute(String key, Name value) {
            handleIndexing(key);
            Branch.Builder c = new Branch.Builder(key, value);
            addChild(c);
            return this;
        }

        public Node.Builder endChild() {
            if (parentBuilder != null) {
                return parentBuilder;
            } else {
                return this;
            }
        }

        public Node build() {
            return build(null);
        }

        Node build(Branch parent) {
            if (lastAdded != null && currentIdx != 0) {
                lastAdded.addIndex(currentIdx);
            }
            if (result != null) {
                if (result instanceof Impl) {
                    Impl result = (Impl) this.result;
                    result.setParent(parent);
                }
                return result;
            }
            Impl result = createImpl();
            if (parent != null) {
                result.setParent(parent);
            }
            result.setParent(parent);
            for (Branch.Builder b : this.children) {
                result.addChild(b.build(result));
            }
            return result;
        }

        @NotNull
        Impl createImpl() {
            return new Impl(elementName);
        }
    }

    class Impl implements Node {
        private final Name elementName;
        private Branch parentRelation;
        private final List<Branch> children;

        private void addChild(Branch rel) {
            this.children.add(rel);
        }

        private void setParent(Branch rel) {
            this.parentRelation = rel;
        }

        Impl(Name element) {
            this.elementName = element;
            this.children = new ArrayList<>();
        }

        public Impl(Name elementName, Branch parentRelation, List<? extends Branch> children) {
            this.elementName = elementName;
            this.parentRelation = parentRelation;
            this.children = new ArrayList<>(children);
        }

        List<Branch> getChildren() {
            return children;
        }

        @Override
        public Name elementName() {
            return elementName;
        }

        @Override
        public Optional<Branch> parentRelation() {
            return Optional.ofNullable(parentRelation);
        }

        @Override
        public Stream<Branch> children() {
            return children.stream();
        }
    }

    Name elementName();

    Optional<Branch> parentRelation();

    Stream<Branch> children();

    default Optional<Name> parentName() {
        return parentRelation().map(Branch::parent).map(Node::elementName);
    }

    default boolean isCycleFree() {
        return this.hasCycle(new HashSet<>());
    }

    default boolean hasCycle(Set<Name> visitedNodeNames) {
        if (elementName() instanceof Value) {
            return false;
        }
        if (visitedNodeNames.contains(elementName())) {
            return true;
        }
        visitedNodeNames.add(elementName());
        return children().reduce(false, (agg,branch) -> agg || branch.child().hasCycle(visitedNodeNames), (l,r) -> l || r);
    }

    @NotNull
    default Stream<Node> childNodesByKey(String key) {
        return childrenByKey(key).map(Branch::child);
    }


    default Stream<Branch> childrenByKey(String key) {
        return children().filter(child -> child.label().equals(key));
    }

    default Stream<Triple> outgoing() {
        return children().map(Branch::asEdge);
    }

    default boolean isLeaf() {
        return children().noneMatch(x -> true);
    }

    default boolean isRoot() {
        return !parentRelation().isPresent();
    }

    default Stream<Node> childNodes() {
        return children().map(Branch::child);
    }

    default Optional<Node> parent() {
        return parentRelation().map(Branch::parent);
    }


    default Stream<Node> siblings() {
        if (!parentRelation().isPresent()) {
            return Stream.empty();
        }
        return parentRelation().get().parent().childNodes().filter(n -> !n.equals(this));
    }

    default Optional<Node> findByName(Name elementName) {
        if (elementName().equals(elementName)) {
            return Optional.of(this);
        }
        return children().map(child -> child.child().findByName(elementName)).filter(Optional::isPresent).findFirst().map(Optional::get);
    }

    default boolean contains(Name nodeName) {
        if (elementName().equals(nodeName)) {
            return true;
        }
        if (isLeaf()) {
            return false;
        }
        return children().map(Branch::child).anyMatch(n -> n.contains(nodeName));
    }


    default void aggregateSubtree(Set<Triple> result) {
        result.add(Triple.node(elementName()));
        children().forEach(child -> {
            result.add(child.asEdge());
            child.child().aggregateSubtree(result);
        });
    }


    default Stream<Triple> subTree() {
        Set<Triple> result = new HashSet<>();
        aggregateSubtree(result);
        return result.stream();
    }

}
