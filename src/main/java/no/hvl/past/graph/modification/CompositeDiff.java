package no.hvl.past.graph.modification;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.Multispan;
import no.hvl.past.graph.names.Name;

import java.util.Set;

public class CompositeDiff extends Diff {

    private final Set<Diff> diffs;

    public CompositeDiff(Set<Diff> diffs) {
        this.diffs = diffs;
    }

    @Override
    public Graph applyTo(Graph source) throws GraphError {
        Graph result = source;
        for (Diff d : diffs) {
            result = d.applyTo(result);
        }
        return result;
    }

    @Override
    public Multispan getSpanRepresentation(Graph source, Name spanName, Name lhsMorphismName, Name rhsMorphismName) throws GraphError {
        return null; // TODO implement
    }

    @Override
    public void handle(DiffVisitor visitor) {
        for (Diff diff : diffs) {
            diff.handle(visitor);
        }
    }
}
