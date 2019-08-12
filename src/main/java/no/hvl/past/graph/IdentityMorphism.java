package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

import java.util.*;
import java.util.stream.Collectors;

public class IdentityMorphism implements AbstractMorphism {

    private final AbstractGraph graph;

    public IdentityMorphism(AbstractGraph graph) {
        this.graph = graph;
    }

    @Override
    public AbstractGraph getDomain() {
        return this.graph;
    }

    @Override
    public AbstractGraph getCodomain() {
        return this.graph;
    }

    @Override
    public boolean definedAt(Name node) {
        return this.graph.contains(node);
    }

    @Override
    public boolean definedAt(Triple t) {
        return this.graph.contains(t);
    }

    @Override
    public Optional<Name> apply(Name node) {
        if (this.definedAt(node)) {
            return Optional.of(node);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Triple> apply(Triple from) {
        if (this.definedAt(from)) {
            return Optional.of(from);
        }
        return Optional.empty();
    }

    @Override
    public Set<Triple> select(Triple to) {
        if (this.definedAt(to)) {
            return Collections.singleton(to);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<Triple> select(Set<Triple> subgraph) {
        return subgraph.stream().flatMap(t -> this.select(t).stream()).collect(Collectors.toSet());
    }

    @Override
    public Multispan pullback(Name spanName, AbstractMorphism other, Name resultGraphName, NamingStrategy strategy) {
        if (!this.getCodomain().getName().equals(other.getCodomain().getName())) {
            throw new Error("invalid input");
        }

        return new Multispan(spanName, other.getDomain(), Arrays.asList(other.getCodomain(),graph), Arrays.asList(other, this));
    }

    @Override
    public Iterator<Tuple> iterator() {
        Iterator<Triple> iterator = this.graph.iterator();
        return new Iterator<Tuple>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Tuple next() {
                Name label = iterator.next().getLabel();
                return new Tuple(label, label);
            }
        };
    }

    @Override
    public Name getName() {
        return graph.getName();
    }


}
