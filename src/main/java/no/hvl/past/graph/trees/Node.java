package no.hvl.past.graph.trees;

import no.hvl.past.attributes.VariableAssignment;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import no.hvl.past.util.StreamExt;
import org.checkerframework.checker.nullness.Opt;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a node in a tree.
 * A node may have a collection attributes (= key-value pairs where both are names)
 * A node may have a number of children, which are nodes.
 */
public interface Node {

    static final Name ROOT_NAME = Name.identifier("/");

    @NotNull
    default Stream<Node> childNodesByKey(Name key) {
        return childrenByKey(key).map(ChildrenRelation::child);
    }

    class Builder {

        Builder parentBuilder;
        Name elementName;
        private List<ChildrenRelation.Builder> children;
        private ChildrenRelation.Builder lastAdded;
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

        public Node.Builder beginChild(Name key, Node childNode) {
            handleIndexing(key);
            Builder childBuilder = new Builder(childNode);
            ChildrenRelation.Builder rel = new ChildrenRelation.Builder(key, childBuilder);
            addChild(rel);
            return childBuilder;
        }

        public Node.Builder beginChild(Name key, Name elementName) {
            handleIndexing(key);
            Builder childBuilder = new Builder(this, elementName);
            ChildrenRelation.Builder rel = new ChildrenRelation.Builder(key, childBuilder);
            addChild(rel);
            return childBuilder;
        }

        void addChild(ChildrenRelation.Builder rel) {
            lastAdded = rel;
            this.children.add(rel);
        }

        public int peekNextIndex(Name key) {
            if (lastAdded == null) {
                return 0;
            }
            if (lastAdded.getKey().equals(key)) {
                return currentIdx + 1;
            }
            return 0;
        }

        void handleIndexing(Name key) {
            if (lastAdded != null && lastAdded.getKey().equals(key)) {
                lastAdded.addIndex(currentIdx);
                currentIdx++;
            } else if (lastAdded != null && !lastAdded.getKey().equals(key)){
                lastAdded.addIndex(currentIdx);
                currentIdx = 0;
            }
        }

        public Node.Builder attribute(Name key, Name value) {
            handleIndexing(key);
            ChildrenRelation.Builder c = new ChildrenRelation.Builder(key, value);
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

        Node build(ChildrenRelation parent) {
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
            for (ChildrenRelation.Builder b : this.children) {
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
        private ChildrenRelation parentRelation;
        private final List<ChildrenRelation> children;

        private void addChild(ChildrenRelation rel) {
            this.children.add(rel);
        }

        private void setParent(ChildrenRelation rel) {
            this.parentRelation = rel;
        }

        Impl(Name element) {
            this.elementName = element;
            this.children = new ArrayList<>();
        }

        public Impl(Name elementName, ChildrenRelation parentRelation, List<? extends ChildrenRelation> children) {
            this.elementName = elementName;
            this.parentRelation = parentRelation;
            this.children = new ArrayList<>(children);
        }

        List<ChildrenRelation> getChildren() {
            return children;
        }

        @Override
        public Name elementName() {
            return elementName;
        }

        @Override
        public Optional<ChildrenRelation> parentRelation() {
            return Optional.ofNullable(parentRelation);
        }

        @Override
        public Stream<? extends ChildrenRelation> children() {
            return children.stream();
        }
    }

    Name elementName();

    Optional<ChildrenRelation> parentRelation();

    default Optional<Name> parentName() {
        return parentRelation().map(ChildrenRelation::parent).map(Node::elementName);
    }

    Stream<? extends ChildrenRelation> children();

    default Stream<? extends ChildrenRelation> childrenByKey(Name key) {
        return children().filter(child -> child.key().equals(key));
    }

    default Stream<Triple> outgoing() {
        return children().map(ChildrenRelation::edgeRepresentation);
    }

    default boolean isLeaf() {
        return children().noneMatch(x -> true);
    }

    default boolean isRoot() {
        return !parentRelation().isPresent();
    }

    default Stream<Node> childNodes() {
        return children().map(ChildrenRelation::child);
    }

    default Optional<Node> parent() {
        return parentRelation().map(ChildrenRelation::parent);
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
        return children().map(ChildrenRelation::child).anyMatch(n -> n.contains(nodeName));
    }


    default void aggregateSubtree(Set<Triple> result) {
        result.add(Triple.node(elementName()));
        children().forEach(child -> {
            result.add(child.edgeRepresentation());
            child.child().aggregateSubtree(result);
        });
    }


    default Stream<Triple> subTree() {
        Set<Triple> result = new HashSet<>();
        aggregateSubtree(result);
        return result.stream();
    }

}
