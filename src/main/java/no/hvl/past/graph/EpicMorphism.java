package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents an epic graph morphism.
 * Epimorphisms can be used to represent a merge of elements.
 * Every epimorphism induces a partition on the graph it is defined on.
 */
public abstract class EpicMorphism extends AbstractModification {

    public EpicMorphism(Name name, Graph base, Graph result) {
        super(name, base, result, true);
    }

    public EpicMorphism(Name morphismName, Graph base, Name resultName) {
        super(morphismName, base, resultName, true);
    }

    public abstract Triple assign(Triple element);

    public Stream<Triple> clazz(Triple representative) {
        return getBase().elements().filter(t -> representative.equals(assign(t)));
    }

    @Override
    public Stream<Triple> preimage(Triple to) {
        return clazz(to);
    }

    @Override
    public boolean isSurjective() {
        return true;
    }

    @Override
    public Optional<Name> map(Name name) {
        Optional<Triple> byLabel = getBase().get(name);
        if (byLabel.isPresent()) {
            return byLabel.map(this::assign).map(Triple::getLabel);
        }
        return Optional.empty();
    }

    @Override
    public Stream<Triple> elements() {
        return getBase().elements().map(this::assign).distinct();
    }

    @Override
    public boolean contains(Triple element) {
        return getBase().elements().anyMatch(t -> assign(t).equals(element));
    }

    @Override
    public boolean mentions(Name name) {
        return this.getBase().elements().anyMatch(t -> assign(t).contains(name));
    }
}
