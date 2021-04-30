package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;

public interface TypedBranch extends Branch {

    class Builder extends Branch.Builder {

        private final Name edgeType;
        private Name returnType;
        private Name inverseEdgeType;

        public Builder(String key, Name value, Name edgeType, Name returnType) {
            super(key, value);
            this.edgeType = edgeType;
            this.returnType = returnType;
        }

        public Builder(String key, TypedNode.Builder childBuilder, Name edgeType) {
            super(key, childBuilder);
            this.edgeType = edgeType;
        }

        public void setInverseEdgeType(Name inverseEdgeType) {
            this.inverseEdgeType = inverseEdgeType;
        }

        @NotNull
        @Override
        Branch.Impl makeResultObject() {
            return new Impl(getKey(), edgeType, inverseEdgeType);
        }

        @NotNull
        @Override
        Node.Impl makeValueNode(Branch result) {
            return new TypedNode.Impl(getValue(), result, new ArrayList<>(), returnType);
        }
    }

    class Impl extends Branch.Impl implements TypedBranch {

        private Name edgeType;
        private Name inverseEdgeType;

        Impl(String key, Name edgeType, Name inverseEdgeType) {
            super(key);
            this.edgeType = edgeType;
            this.inverseEdgeType = inverseEdgeType;
        }

        public Impl(TypedNode parent, String key, TypedNode child, Name edgeTypeLabel, int index) {
            super(parent, key, child, index);
            this.edgeType = edgeTypeLabel;
            this.inverseEdgeType = null;

        }

        public Impl(TypedNode parent, String key, TypedNode child, Name edgeTypeLabel) {
            super(parent, key, child);
            this.edgeType = edgeTypeLabel;
            this.inverseEdgeType = null;

        }

        @Override
        public Optional<Name> inverseEdgeTyping() {
            return Optional.ofNullable(inverseEdgeType);
        }

        @Override
        public TypedNode parent() {
            return (TypedNode) getParent();
        }

        @Override
        public TypedNode child() {
            return (TypedNode) getChild();
        }

        @Override
        public Name edgeTyping() {
            return edgeType;
        }
    }

    TypedNode parent();

    Name edgeTyping();

    TypedNode child();

    default Optional<Name> inverseEdgeTyping() {
        return Optional.empty();
    }

    default Triple typeFeature() {
        return Triple.edge(parent().nodeType(), edgeTyping(), child().nodeType());
    }

    default Optional<Triple> inverseTypeFeature() {
        return inverseEdgeTyping().map(lbl -> Triple.edge(child().nodeType(), lbl, parent().nodeType()));
    }

    default boolean matches(Triple edge) {
        return edge.getLabel().equals(edgeTyping()) &&
                child().nodeType().equals(edge.getTarget());
    }

}
