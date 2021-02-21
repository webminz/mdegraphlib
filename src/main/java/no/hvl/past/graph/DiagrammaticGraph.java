package no.hvl.past.graph;

import no.hvl.past.names.Name;

import java.util.*;
import java.util.stream.Stream;

/**
 * A simple implementation of a sketch
 * comprising a carrier graph and set of diagrams on that graph.
 */
public class DiagrammaticGraph implements Sketch {

    private final Name name;
    private final Graph carrier;
    private final List<Diagram> diagrams;

    public DiagrammaticGraph(Name name, Graph carrier, List<Diagram> diagrams) {
        this.name = name;
        this.carrier = carrier;
        this.diagrams = diagrams;
    }

    public Name getName() {
        return this.name;
    }

    @Override
    public Graph carrier() {
        return carrier;
    }

    @Override
    public Stream<Diagram> diagrams() {
        return diagrams.stream();
    }

}
