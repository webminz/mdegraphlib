package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface InheritanceGraphMorphism extends GraphMorphism {

    class Impl extends GraphMorphismImpl implements  InheritanceGraphMorphism{

        public Impl(Name name, Graph domain, InheritanceGraph codomain, Map<Name, Name> mapping) {
            super(name, domain, codomain, mapping);
        }

        public Impl(Name name, Graph domain, InheritanceGraph codomain, Set<Tuple> tuples) {
            super(name, domain, codomain, tuples);
        }

        @Override
        public InheritanceGraph codomain() {
            return (InheritanceGraph) super.codomain();
        }
    }

    @Override
    Graph domain();

    @Override
    InheritanceGraph codomain();

    @Override
    default Stream<Triple> allInstances(Name type) {
        return domain().nodes().filter(n -> map(n).map(mapped -> codomain().isUnder(mapped, type)).orElse(false)).map(Triple::node);
    }

    @Override
    default Stream<Triple> allInstances(Triple type) {
        if (type.isNode()) {
            return allInstances(type.getLabel());
        }
        return GraphMorphism.super.allInstances(type);
    }

    @Override
    default boolean definedAt(Triple t) {
        if (t.isNode()) {
            return this.definedAt(t.getLabel());
        }
        return this.map(t.getLabel()).map(n -> codomain().get(n).isPresent()).orElse(false);
    }

    @Override
    default Optional<Triple> apply(Triple from) {
        if (from.isNode()) {
            return this.map(from.getLabel()).map(Triple::node);
        } else {
            return this.map(from.getLabel()).flatMap(n -> codomain().get(n));
        }
    }

    @Override
    default boolean verify() {
        return
                // The mapping targets of nodes actually exist.
                domain().nodes()
                    .map(this::map)
                    .filter(Optional::isPresent)
                    .allMatch(n -> codomain()
                            .contains(Triple.node(n.get()))) &&
                // The hom-property modulo subtyping is valid
                domain().edges()
                        .filter(this::definedAt)
                        .allMatch(t -> {
                            Triple t_mapped = this.apply(t).get();
                            return t_mapped.isEddge() &&
                                    codomain().isUnder(this.map(t.getSource()).get(), t_mapped.getSource()) &&
                                    codomain().isUnder(this.map(t.getTarget()).get(), t_mapped.getTarget());
                        });
    }
}

