package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface TypedNode extends Node {

    static final Name BUNDLE_TYPE = Name.identifier("$BUNDLE");

    default void nodesWithType(Name type, Set<TypedNode> aggregator) {
        if (nodeType().equals(type)) {
            aggregator.add(this);
        } else {
            typedChildren().map(TypedBranch::child).forEach(n -> n.nodesWithType(type,aggregator));
        }
    }

    class Builder extends Node.Builder {

        private Name type;

        public Builder(Name elementName, Name type) {
            super(elementName);
            this.type = type;
        }

        public Builder(Node.Builder parentBuilder, Name elementName, Name type) {
            super(parentBuilder, elementName);
            this.type = type;
        }

        public Name getType() {
            return type;
        }

        public Builder chandeTyping(Name newType) {
            this.type = newType;
            return this;
        }

        public Builder beginChild(String key, Name elementName, Triple childRelationType) {
            handleIndexing(key);
            Builder childBuilder = new Builder(this, elementName, childRelationType != null ? childRelationType.getTarget() : null);
            Branch.Builder rel = new TypedBranch.Builder(key, childBuilder, childRelationType != null ? childRelationType.getLabel() : null);
            addChild(rel);
            return childBuilder;
        }

        @NotNull
        @Override
        Node.Impl createImpl() {
            return new TypedNode.Impl(elementName, type);
        }


        @Override
        public TypedNode build() {
            return (TypedNode) super.build();
        }

        @Override
        TypedNode build(Branch parent) {
            return (TypedNode) super.build(parent);
        }

        public void attribute(String key, Name value, Triple triple) {
            handleIndexing(key);
            Branch.Builder rel = new TypedBranch.Builder(key, value,triple != null? triple.getLabel() : null, triple != null ? triple.getTarget() : null);
            addChild(rel);
        }

        public Node.Builder beginChild(String fieldName, Name makeOID, Triple childRelationType, Triple inverseChildRelationType) {
            handleIndexing(fieldName);
            Builder childBuilder = new Builder(this, makeOID, childRelationType != null ? childRelationType.getTarget() : null);
            TypedBranch.Builder rel = new TypedBranch.Builder(fieldName, childBuilder, childRelationType != null ? childRelationType.getLabel() : null);
            rel.setInverseEdgeType(inverseChildRelationType.getLabel());
            addChild(rel);
            return childBuilder;
        }
    }

    class Impl extends Node.Impl implements TypedNode {

        private final Name type;

        public Impl(Name elementName, Name type) {
            super(elementName);
            this.type = type;
        }

        public Impl(Name elementName, Branch parentRelation, List<TypedBranch> children, Name type) {
            super(elementName, parentRelation, children);
            this.type = type;
        }

        @Override
        public Name nodeType() {
            return type;
        }

        public Stream<Branch> children() {
            return getChildren().stream();
        }
    }

    Name nodeType();

    Stream<Branch> children();

    default Stream<TypedNode> feature(Triple type) {
        Optional<TypedBranch> result = parentRelation().filter(b -> b instanceof TypedBranch)
                .map(b -> (TypedBranch) b)
                .filter(tb -> tb.inverseTypeFeature().map(type::equals).orElse(false));
        return result.map(TypedBranch::parent).map(Stream::of).orElseGet(() -> typedChildren().filter(tb -> tb.edgeTyping().equals(type.getLabel())).map(TypedBranch::child));
    }

    default Stream<TypedBranch> typedChildren() {
        return children().filter(b -> b instanceof TypedBranch).map(b -> (TypedBranch) b);
    }

    default Stream<TypedBranch> typedChildrenByLabel(String label) {
        return childrenByKey(label).filter(b -> b instanceof TypedBranch).map(b -> (TypedBranch) b);
    }


    default Stream<TypedNode> typedNodesByLabel(String label) {
        return typedChildrenByLabel(label).map(TypedBranch::child);
    }

    default void aggregateTypedPart(Set<Triple> elements, Set<Tuple> mappings) {
        elements.add(Triple.node(elementName()));
        mappings.add(new Tuple(elementName(), nodeType()));
        typedChildren().forEach(rel -> {
            elements.add(rel.asEdge());
            mappings.add(new Tuple(rel.asEdge().getLabel(), rel.edgeTyping()));
        });
        typedChildren().map(TypedBranch::child).forEach(n -> n.aggregateTypedPart(elements, mappings));
    }

    default Optional<Name> lookupTyping(Name name) {
        if (name.equals(elementName())) {
            return Optional.of(nodeType());
        }
        Optional<? extends TypedBranch> edge = typedChildren().filter(c -> {
            return (c.asEdge().getLabel().equals(name));
        }).findFirst();
        return edge
                .map(typedBranch -> Optional.of(typedBranch.edgeTyping()))
                .orElseGet(() -> typedChildren().map(c -> c.child().lookupTyping(name)).filter(Optional::isPresent).findFirst().orElse(Optional.empty()));
    }

    default void findChildRelationByType(Triple edgeType, Set<TypedBranch> aggregator) {
        if (nodeType().equals(edgeType.getSource())) {
            typedChildren().filter(c -> c.matches(edgeType))
                    .forEach(aggregator::add);
        }
        typedChildren().forEach(c -> c.child().findChildRelationByType(edgeType, aggregator));
    }

    default void findNodeByType(Name typeName, Set<TypedNode> aggregator) {
        if (nodeType().equals(typeName)) {
            aggregator.add(this);
        }
        typedChildren().forEach(c -> c.child().findNodeByType(typeName, aggregator));
    }

    default Optional<TypedNode> byName(Name elementName) {
        return findByName(elementName).filter(n -> n instanceof TypedNode).map(n -> (TypedNode) n);
    }



}
