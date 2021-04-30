package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;

import java.util.Set;
import java.util.stream.Stream;

public class InheritanceAugmentedGraph implements InheritanceGraph {

    private final Graph graph;
    private final Set<Tuple> inheritanceEdges;
    private final Set<Tuple> directInheritanceEdges;


    public InheritanceAugmentedGraph(Graph graph, Set<Tuple> inheritanceEdges) {
        this.graph = graph;
        this.directInheritanceEdges = inheritanceEdges;
        this.inheritanceEdges = Tuple.transitiveClosure(inheritanceEdges);
    }

    @Override
    public boolean isUnder(Name subnode, Name supernode) {
        // Reflexivity
        if (subnode.equals(supernode)) {
            return isNode(subnode);
        }
        return inheritanceEdges.stream().anyMatch(t -> t.getDomain().equals(subnode) && t.getCodomain().equals(supernode));
    }

    @Override
    public Stream<Tuple> directInheritances() {
        return inheritanceEdges.stream();
    }


    @Override
    public Stream<Triple> elements() {
        return graph.elements();
    }

    @Override
    public Name getName() {
        return graph.getName();
    }

    @Override
    public boolean isInfinite() {
        return graph.isInfinite();
    }
}
