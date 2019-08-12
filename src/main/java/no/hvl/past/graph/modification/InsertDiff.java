package no.hvl.past.graph.modification;

import no.hvl.past.graph.*;
import no.hvl.past.graph.names.Name;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class InsertDiff extends Diff {

    private final Set<Triple> toInsert;

    public InsertDiff(Set<Triple> toInsert) {
        this.toInsert = toInsert;
    }

    @Override
    public Graph applyTo(Graph source) throws GraphError {
        Set<Triple> result = new HashSet<>();
        result.addAll(toInsert);
        result.addAll(source.getNodes().stream().map(Triple::fromNode).collect(Collectors.toList()));
        result.addAll(source.getEdges());
        return Graph.create(source.getName(), result);
    }

    @Override
    public Multispan getSpanRepresentation(Graph source, Name spanName, Name lhsMorphismName, Name rhsMorphismName) throws GraphError {
        Morphism lhs = Morphism.create(lhsMorphismName, source, source, source.getIdentityMapping());
        Graph result = this.applyTo(source);
        Morphism rhs = Morphism.create(rhsMorphismName, source, result, source.getIdentityMapping());
        return Multispan.create(spanName, source, Arrays.asList(source, result), Arrays.asList(lhs, rhs));
    }


    @Override
    public void handle(DiffVisitor visitor) {
        visitor.handle(this);
    }
}
