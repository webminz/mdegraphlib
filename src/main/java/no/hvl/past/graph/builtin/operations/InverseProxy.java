package no.hvl.past.graph.builtin.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.ShouldNotHappenException;

/**
 * Creates a proxy for a node, i.e. turns a single node to an edge (to a copy of its own).
 */
public class InverseProxy implements GraphOperation {

    private final Graph arity;

    private InverseProxy() {
        try {
            this.arity = new GraphBuilders()
                    .edge("1", "10", "0")
                    .graph("ARROW_INV")
                    .getResult(Graph.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "constructor", graphError);
        }
    }


    @Override
    public Graph outputArity() {
        return arity;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) {
        GraphBuilders b = new GraphBuilders().importGraph(instance.domain());
        instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .map(Triple::getLabel)
                .map(n -> Triple.edge(
                        getName().appliedTo(n).addSuffix(Name.identifier("1")),
                        getName().appliedTo(n).addSuffix(Name.identifier("10")),
                        n
                )).forEach(edge -> {
            b.edge(edge.getSource(), edge.getLabel(), edge.getTarget())
                    .map(edge.getSource(), Name.identifier("1"))
                    .map(edge.getTarget(), Name.identifier("10"));
        });
        b.graph(getName().appliedTo(instance.domain().getName()))
                .codomain(arity)
                .morphism(getName().appliedTo(instance.getName()));
        try {
            return b.getResult(GraphMorphism.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "execute", graphError);
        }
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return false;
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) {
        return instance;
    }

    @Override
    public String nameAsString() {
        return "<inverseProxy>";
    }

    @Override
    public GraphImpl inputArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph overlapArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph arity() {
        return arity;
    }
}
