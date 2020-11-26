package no.hvl.past.graph;

import no.hvl.past.names.Name;

import java.util.Optional;

public class DiagramImpl implements Diagram {

    private final Name name;
    private final GraphLabelTheory label;
    private final GraphMorphism binding;
    private boolean isValid = true;


    public DiagramImpl(Name name, GraphLabelTheory label, GraphMorphism binding) {
        this.name = name;
        this.label = label;
        this.binding = binding;
    }

    @Override
    public Optional<Label> label() {
        return Optional.of(label);
    }

    @Override
    public GraphMorphism binding() {
        return binding;
    }

    @Override
    public Graph colimit() {
        return null; // TODO think about
    }

    @Override
    public Graph limit() {
        return null; // TODO think about
    }

    @Override
    public Graph flatten() {
        return null; // TODO think about
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.beginDiagram();
        visitor.handleName(name);
        binding().accept(visitor);
        visitor.endDiagram();
    }

    @Override
    public boolean verify() {
        return isValid;
    }
}
