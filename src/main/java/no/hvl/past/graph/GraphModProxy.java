package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.stream.Stream;

/**
 * A graph that is derived from a structural modification,
 * which is represented through a morphism.
 */
public class GraphModProxy implements Graph {

    private final Name name;
    private final AbstractModification resultingFrom;

    GraphModProxy(Name name, AbstractModification resultingFrom) {
        this.name = name;
        this.resultingFrom = resultingFrom;
    }

    @Override
    public Stream<Triple> elements() {
        return resultingFrom.elements();
    }

    @Override
    public boolean contains(Triple triple) {
        return resultingFrom.contains(triple);
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public boolean isInfinite() {
        return resultingFrom.getBase().isInfinite();
    }

    @Override
    public boolean mentions(Name name) {
        return resultingFrom.mentions(name);
    }
}
