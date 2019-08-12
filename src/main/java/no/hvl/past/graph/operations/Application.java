package no.hvl.past.graph.operations;

import no.hvl.past.graph.*;

public class Application {

    private final GraphDiagram type;

    private final Morphism binding;

    public Application(GraphDiagram type, Morphism binding) {
        this.type = type;
        this.binding = binding;
    }

    public GraphDiagram getType() {
        return type;
    }

    public Morphism getBinding() {
        return binding;
    }
}
