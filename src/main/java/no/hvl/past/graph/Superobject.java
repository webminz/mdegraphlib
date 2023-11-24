package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a superobject of a given graph,
 * i.e. it adds more elements to a given graph.
 */
public abstract class Superobject extends MonicMorphism {

    protected abstract Stream<Triple> inserts();

    public Superobject(Name inclusionName, Graph subobject, Name superobjectName) {
        super(inclusionName, subobject, superobjectName, true);
    }

    @Override
    public Stream<Triple> elements() {
        return Stream.concat(getBase().elements(), inserts());
    }

    @Override
    public boolean contains(Triple triple) {
        return inserts().anyMatch(triple::equals) || getBase().contains(triple);
    }

    @Override
    public Graph domain() {
        return getBase();
    }

    @Override
    public Graph codomain() {
        return getResult();
    }

    @Override
    public Optional<Name> map(Name name) {
        if(getBase().mentions(name)) {
            return Optional.of(name);
        }
        return Optional.empty();
    }

    @Override
    public boolean mentions(Name name) {
        return this.inserts().anyMatch(t -> t.contains(name)) || getBase().mentions(name);
    }
}
