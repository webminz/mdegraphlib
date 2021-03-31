package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public interface ChildrenRelation {

    class Builder  {
        private final Name key;
        private final Name value;
        private final Node.Builder childBuilder;
        private Integer index;

        Name getKey() {
            return key;
        }

        public Builder(Name key, Name value) {
            this.key = key;
            this.value = value;
            this.childBuilder = null;
            this.index = null;
        }

        public Builder(Name key, Node.Builder childBuilder) {
            this.key = key;
            this.childBuilder = childBuilder;
            this.value = null;
            this.index = null;
        }

        Name getValue() {
            return value;
        }

        public void addIndex(int idx) {
            this.index = idx;
        }

        private boolean isAtt() {
            return this.value != null && this.childBuilder == null;
        }

        ChildrenRelation build(Node parent) {
            ChildrenRelation.Impl result = makeResultObject();
            result.setParent(parent);
            result.setIndex(index);
            if (isAtt()) {
                result.setChild(makeValueNode(result));
            } else {
                result.setChild(childBuilder.build(result));
            }
            return result;
        }

        @NotNull
        Impl makeResultObject() {
            return new Impl(key);
        }

        @NotNull
        Node.Impl makeValueNode(ChildrenRelation result) {
            return new Node.Impl(value, result, Collections.emptyList());
        }
    }

    class Impl implements ChildrenRelation {
        private Node parent;
        private final Name key;
        private Node child;
        private Integer index;

        Node getChild() {
            return child;
        }

        private void setParent(Node parent) {
            this.parent = parent;
        }

        private void setChild(Node child) {
            this.child = child;
        }

        private void setIndex(Integer index) {
            this.index = index;
        }

        Impl(Name key) {
            this.key = key;
        }

        public Impl(Node parent, Name key, Node child, Integer index) {
            this.parent = parent;
            this.key = key;
            this.child = child;
            this.index = index;
        }

        public Impl(Node parent, Name key, Node child) {
            this.parent = parent;
            this.key = key;
            this.child = child;
            this.index = null;
        }


        @Override
        public Node parent() {
            return parent;
        }

        @Override
        public Name key() {
            return key;
        }

        @Override
        public Node child() {
            return child;
        }

        @Override
        public boolean isCollection() {
            return index != null;
        }

        @Override
        public int index() {
            return index == null ? 0 : index;
        }

    }

    Node parent();

    Name key();

    Node child();

    boolean isCollection();

    int index();

    default boolean isAttribute() {
        return child().isLeaf() && child().elementName().isValue();
    }

    default Triple edgeRepresentation() {
        if (!isAttribute()) {
            if (isCollection()) {
                return Triple.edge(parent().elementName(), key().index(index()).childOf(parent().elementName()), child().elementName());
            } else {
                return Triple.edge(parent().elementName(), key().childOf(parent().elementName()), child().elementName());
            }
        } else {
            if (isCollection()) {
                return Triple.edge(parent().elementName(), key().prefixWith(parent().elementName()).index(index()), child().elementName());
            } else {
                return Triple.edge(parent().elementName(), key().prefixWith(parent().elementName()), child().elementName());
            }
        }
    }

}
