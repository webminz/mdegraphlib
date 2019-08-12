package no.hvl.past.graph.modification;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.Morphism;
import no.hvl.past.graph.Multispan;
import no.hvl.past.graph.names.Name;
import no.hvl.past.util.Pair;

public abstract class Diff {

    public abstract Graph applyTo(Graph source) throws GraphError;

    public abstract Multispan getSpanRepresentation(Graph source, Name spanName, Name lhsMorphismName, Name rhsMorphismName) throws GraphError;

    public abstract void handle(DiffVisitor visitor);


}
