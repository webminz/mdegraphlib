package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

public class GraphMorphismComposition implements GraphMorphism {

    private final GraphMorphism first;
    private final GraphMorphism second;


    GraphMorphismComposition(GraphMorphism first, GraphMorphism second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Graph domain() {
        return first.domain();
    }

    @Override
    public Graph codomain() {
        return second.codomain();
    }

    @Override
    public Optional<Name> map(Name name) {
        return first.map(name).flatMap(second::map);
    }

    @Override
    public boolean definedAt(Name node) {
        return first.applyOnNode(node).map(second::definedAt).orElse(false);
    }

    @Override
    public boolean definedAt(Triple t) {
        return definedAt(t.getSource()) && definedAt(t.getLabel()) && definedAt(t.getTarget());
    }

    @Override
    public Optional<Triple> apply(Triple from) {
        return first.apply(from).flatMap(second::apply);
    }

    @Override
    public Stream<Triple> select(Triple to) {
        return second.select(to).flatMap(first::select);
    }

    @Override
    public Stream<Triple> selectByLabel(Name toLabelName) {
        return second.selectByLabel(toLabelName).map(Triple::getLabel).flatMap(first::selectByLabel);
    }

    @Override
    public Name getName() {
        return first.getName().composeSequentially(second.getName());
    }
}
