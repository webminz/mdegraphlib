package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Identifier;
import no.hvl.past.names.Name;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface TypedNode extends Node {

    static final Name BUNDLE_TYPE = Name.identifier("$BUNDLE");

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

        public Builder beginChild(Name key, Name elementName, Triple childRelationType) {
            handleIndexing(key);
            Builder childBuilder = new Builder(this, elementName, childRelationType != null ? childRelationType.getTarget() : null);
            ChildrenRelation.Builder rel = new TypedChildrenRelation.Builder(key, childBuilder, childRelationType != null ? childRelationType.getLabel() : null);
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
        TypedNode build(ChildrenRelation parent) {
            return (TypedNode) super.build(parent);
        }

        public void attribute(Name key, Name value, Triple triple) {
            handleIndexing(key);
            ChildrenRelation.Builder rel = new TypedChildrenRelation.Builder(key, value,triple != null? triple.getLabel() : null, triple != null ? triple.getTarget() : null);
            addChild(rel);
        }
    }

    class Impl extends Node.Impl implements TypedNode {

        private final Name type;

        public Impl(Name elementName, Name type) {
            super(elementName);
            this.type = type;
        }

        public Impl(Name elementName, ChildrenRelation parentRelation, List<TypedChildrenRelation> children, Name type) {
            super(elementName, parentRelation, children);
            this.type = type;
        }

        @Override
        public Optional<Name> nodeType() {
            return Optional.ofNullable(type);
        }

        public Stream<TypedChildrenRelation> children() {
            return getChildren().stream().filter(c -> c instanceof TypedChildrenRelation).map(c -> (TypedChildrenRelation)c);
        }
    }

    Optional<Name> nodeType();

    Stream<? extends TypedChildrenRelation> children();

    default void aggregateTypedPart(Set<Triple> elements, Set<Tuple> mappings) {
        if (nodeType().isPresent()) {
            elements.add(Triple.node(elementName()));
            mappings.add(new Tuple(elementName(), nodeType().get()));
        }
        children().filter(c -> c.edgeTyping().isPresent() && c.child().nodeType().isPresent()).forEach(rel -> {
            elements.add(rel.edgeRepresentation());
            mappings.add(new Tuple(rel.edgeRepresentation().getLabel(), rel.edgeTyping().get()));
        });
        children().map(ChildrenRelation::child).forEach(n -> ((TypedNode) n).aggregateTypedPart(elements, mappings));
    }

    default Optional<Name> lookupTyping(Name name) {
        if (name.equals(elementName())) {
            return nodeType();
        }
        Optional<? extends TypedChildrenRelation> edge = children().filter(c -> {
            return (c.edgeRepresentation().getLabel().equals(name));
        }).findFirst();
        if (edge.isPresent()) {
            return edge.get().edgeTyping();
        } else {
            return children().map(c -> c.child().lookupTyping(name)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
        }
    }

    default void findChildRelationByType(Triple edgeType, Set<TypedChildrenRelation> aggregator) {
        if (nodeType().isPresent() && nodeType().get().equals(edgeType.getSource())) {
            children().filter(c -> c.matches(edgeType))
                    .forEach(aggregator::add);
        }
        children().forEach(c -> c.child().findChildRelationByType(edgeType, aggregator));
    }

    default void findNodeByType(Name typeName, Set<TypedNode> aggregator) {
        if (nodeType().isPresent() && nodeType().get().equals(typeName)) {
            aggregator.add(this);
        }
        children().forEach(c -> c.child().findNodeByType(typeName, aggregator));
    }



}
