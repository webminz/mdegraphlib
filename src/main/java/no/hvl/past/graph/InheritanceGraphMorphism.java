package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;

public interface InheritanceGraphMorphism extends GraphMorphism {

    @Override
    InheritanceGraph domain();

    @Override
    InheritanceGraph codomain();

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

