package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public interface TypedChildrenRelation extends ChildrenRelation {

    class Builder extends ChildrenRelation.Builder {

        private Name edgeType;
        private Name returnType;

        public Builder(Name key, Name value, Name edgeType, Name returnType) {
            super(key, value);
            this.edgeType = edgeType;
            this.returnType = returnType;
        }

        public Builder(Name key, TypedNode.Builder childBuilder, Name edgeType) {
            super(key, childBuilder);
            this.edgeType = edgeType;
        }

        @NotNull
        @Override
        ChildrenRelation.Impl makeResultObject() {
            return new Impl(getKey(), edgeType);
        }

        @NotNull
        @Override
        Node.Impl makeValueNode(ChildrenRelation result) {
            return new TypedNode.Impl(getValue(), result, new ArrayList<>(), returnType);
        }
    }

    class Impl extends ChildrenRelation.Impl implements TypedChildrenRelation {

        private Name edgeType;

        Impl(Name key, Name edgeType) {
            super(key);
            this.edgeType = edgeType;
        }

        public Impl(Node parent, Name key, Node child, Name edgeTypeLabel, Integer index) {
            super(parent, key, child, index);
            this.edgeType = edgeTypeLabel;
        }

        public Impl(Node parent, Name key, TypedNode child, Name edgeTypeLabel) {
            super(parent, key, child);
            this.edgeType = edgeTypeLabel;
        }

        @Override
        public TypedNode child() {
            return (TypedNode) getChild();
        }

        @Override
        public Optional<Name> edgeTyping() {
            return Optional.ofNullable(edgeType);
        }
    }


    Optional<Name> edgeTyping();

    TypedNode child();

    default boolean matches(Triple edge) {
        return edgeTyping().isPresent() &&
                edge.getLabel().equals(edgeTyping().get()) &&
                child().nodeType().isPresent() &&
                child().nodeType().get().equals(edge.getTarget());
    }

}
