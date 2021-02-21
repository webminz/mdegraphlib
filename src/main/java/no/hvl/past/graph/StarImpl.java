package no.hvl.past.graph;

import com.google.common.collect.Sets;
import no.hvl.past.graph.elements.EquivalenceClass;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Basically this construction represents a relation among graph elements,
 * which is encoded by means of category theory, i.e. morphisms.
 * As graphs are usually abstract representations of models,
 * this construction can also be referred to as a multimodel, i.e.
 * coordinating multiple models and common elements among them.
 *
 */
public class StarImpl implements Star {

    private final Name name;
    private final Sketch apex;
    private final List<Sketch> components;
    private final List<GraphMorphism> projections;
    private final Set<Name> identities;


    public StarImpl(Name name,
                    Sketch apex,
                    List<Sketch> components,
                    List<GraphMorphism> projections,
                    Set<Name> identities) {
        this.name = name;
        this.apex = apex;
        this.components = components;
        this.projections = projections;
        this.identities = identities;
    }

    @Override
    public int size() {
        return components.size();
    }

    @Override
    public Sketch apex() {
        return apex;
    }

    @Override
    public Optional<Sketch> component(int i) {
        return Optional.of(components.get(i - 1));
    }

    @Override
    public Optional<GraphMorphism> projection(int i) {
        return Optional.of(projections.get(i - 1));
    }

    @Override
    public Stream<Name> identities() {
        return identities.stream();
    }

    @Override
    public Name getName() {
        return name;
    }
}
