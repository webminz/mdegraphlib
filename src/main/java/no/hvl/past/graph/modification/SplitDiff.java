package no.hvl.past.graph.modification;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.Multispan;
import no.hvl.past.graph.Triple;
import no.hvl.past.graph.names.Name;
import no.hvl.past.util.Pair;

import java.util.Set;

public class SplitDiff extends Diff {

    private final Set<Pair<Triple, Set<Triple>>> toSplit;

    public SplitDiff(Set<Pair<Triple, Set<Triple>>> toSplit) {
        this.toSplit = toSplit;
    }

    @Override
    public Graph applyTo(Graph source) throws GraphError {
        return null; // TODO implement
    }

    @Override
    public Multispan getSpanRepresentation(Graph source, Name spanName, Name lhsMorphismName, Name rhsMorphismName) throws GraphError {
        return null; // TODO implement
    }

    @Override
    public void handle(DiffVisitor visitor) {
        visitor.handle(this);
    }
}
