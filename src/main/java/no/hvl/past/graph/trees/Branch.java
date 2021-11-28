package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public interface Branch {

    class Builder  {
        private final String key;
        private final Name value;
        private final Node.Builder childBuilder;
        private Integer index;

        String getKey() {
            return key;
        }

        public Builder(String key, Name value) {
            this.key = key;
            this.value = value;
            this.childBuilder = null;
            this.index = null;
        }

        public Builder(String key, Node.Builder childBuilder) {
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

        Branch build(Node parent) {
            Branch.Impl result = makeResultObject();
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
        Node.Impl makeValueNode(Branch result) {
            return new Node.Impl(value, result, Collections.emptyList());
        }
    }

    class Impl implements Branch {
        private Node parent;
        private final String key;
        private Node child;
        private Integer index;

        protected Node getChild() {
            return child;
        }

        protected Node getParent() {
            return parent;
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

        Impl(String key) {
            this.key = key;
        }

        public Impl(Node parent, String key, Node child, Integer index) {
            this.parent = parent;
            this.key = key;
            this.child = child;
            this.index = index;
        }

        public Impl(Node parent, String key, Node child) {
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
        public String label() {
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

    String label();

    Node child();

    boolean isCollection();

    // TODO should be long
    int index();

    default boolean isAttribute() {
        return child().isLeaf() && child().elementName().isValue();
    }

    default Triple asEdge() {
            if (isCollection()) {
                return Triple.edge(parent().elementName(), Name.identifier(label()).prefixWith(parent().elementName()).index(index()), child().elementName());
            } else {
                return Triple.edge(parent().elementName(), Name.identifier(label()).prefixWith(parent().elementName()), child().elementName());
            }
    }

}
