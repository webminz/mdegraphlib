package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents an abstract subobject of a given base graph,
 * i.e. by removing some elemnts from a base graph.
 */
public abstract class Subobject extends MonicMorphism {


    /**
     * Return true if the given edge label is removed, i.e.
     * the edge with the given label or node with given name was deleted.
     * @return
     */
    public abstract boolean deletes(Name name);

    public Subobject(Name morphismName,
                     Graph superobject,
                     Name subobjectName) {
        super(morphismName, superobject, subobjectName, false);
    }

    @Override
    public Stream<Triple> elements() {
        return getBase().elements()
                .filter(t -> !deletes(t.getLabel()))
                .filter(t -> !deletes(t.getSource())) // No dangling
                .filter(t -> !deletes(t.getTarget())); // No dangling
    }

    @Override
    public boolean contains(Triple triple) {
        if (deletes(triple.getLabel())) {
            return false;
        }
        return getBase().contains(triple);
    }

    @Override
    public Graph domain() {
        return getResult();
    }

    @Override
    public Graph codomain() {
        return getBase();
    }

    @Override
    public Optional<Name> map(Name name) {
        if (deletes(name)) {
            return Optional.empty();
        }
        return Optional.of(name);
    }

    @Override
    public boolean mentions(Name name) {
        return !this.deletes(name) && getBase().mentions(name);
    }
}
