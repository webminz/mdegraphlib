package no.hvl.past.graph.modification;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.Multispan;
import no.hvl.past.graph.Triple;
import no.hvl.past.graph.names.Name;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MergeDiff extends Diff {

    private final Map<Triple, Triple> mergeDefinition;

    public MergeDiff(Map<Triple, Triple> mergeDefinition) {
        this.mergeDefinition = mergeDefinition;
    }

    @Override
    public Graph applyTo(Graph source) throws GraphError {
        Set<Triple> result = new HashSet<>();
        Set<Name> mergedNodes = source.getNodes().stream().map(Triple::fromNode).filter(mergeDefinition::containsKey).map(Triple::getLabel).collect(Collectors.toSet());
        result.addAll(source.getNodes().stream().filter(n -> !mergedNodes.contains(n)).map(Triple::fromNode).collect(Collectors.toSet()));
        result.addAll(source.getNodes().stream().filter(mergedNodes::contains).map(Triple::fromNode).map(mergeDefinition::get).collect(Collectors.toSet()));
        // TODO finish

        return Graph.create(source.getName(), result);
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
