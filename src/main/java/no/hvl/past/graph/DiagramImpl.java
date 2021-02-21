package no.hvl.past.graph;

import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

import java.util.Optional;

public class DiagramImpl implements Diagram {

    private final Name name;
    private final Formula<Graph> label;
    private final GraphMorphism binding;

    public DiagramImpl(Name name,
                       Formula<Graph>  label,
                       GraphMorphism binding) {
        this.name = name;
        this.label = label;
        this.binding = binding;
    }

    @Override
    public Formula<Graph> label() {
        return label;
    }

    @Override
    public GraphMorphism binding() {
        return binding;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public boolean verify() {
        return binding.verify();
    }
}
