package no.hvl.past.graph.modification;

import no.hvl.past.graph.*;
import no.hvl.past.graph.names.Name;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteDiff extends Diff {

    private final Set<Triple> toDelete;

    public DeleteDiff(Set<Triple> toDelete) {
        this.toDelete = toDelete;
    }

    @Override
    public Graph applyTo(Graph source) throws GraphError {
        Set<Triple> result = new HashSet<>();
        result.addAll(source.getNodes().stream().map(Triple::fromNode).collect(Collectors.toList()));
        result.addAll(source.getEdges());
        result.removeAll(toDelete);
        return Graph.create(source.getName(), result);
    }

    @Override
    public Multispan getSpanRepresentation(Graph source, Name spanName, Name lhsMorphismName, Name rhsMorphismName) throws GraphError {
        Graph result = this.applyTo(source);
        Morphism lhs = Morphism.create(lhsMorphismName, result, source, result.getIdentityMapping());
        Morphism rhs = Morphism.create(rhsMorphismName, result, result, result.getIdentityMapping());
        return Multispan.create(spanName, result, Arrays.asList(source, result), Arrays.asList(lhs, rhs));
    }

    @Override
    public void handle(DiffVisitor visitor) {
        visitor.handle(this);
    }
}
