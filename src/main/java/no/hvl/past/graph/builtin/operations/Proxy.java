package no.hvl.past.graph.builtin.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.util.ShouldNotHappenException;

/**
 * Creates a proxy for a node, i.e. turns a single node to an edge (to a copy of its own).
 */
public class Proxy implements GraphOperation {

    @Override
    public GraphImpl outputArity() {
        return Universe.ARROW;
    }

    @Override
    public TypedGraph execute(TypedGraph instance, ExecutionContext context) {
        GraphBuilders b = new GraphBuilders().importGraph(instance.domain());
        instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .map(node -> Triple.edge(
                        node.getLabel(),
                        getName().appliedTo(node.getLabel()).addSuffix(Universe.ARROW_LBL_NAME),
                        getName().appliedTo(node.getLabel()).addSuffix(Universe.ARROW_TRG_NAME))
                ).forEach(edge -> {
            b.edge(edge.getSource(), edge.getLabel(), edge.getTarget())
                    .map(edge.getLabel(), Universe.ARROW_LBL_NAME)
                    .map(edge.getTarget(), Universe.ARROW_TRG_NAME);
        });
        b.graph(getName().appliedTo(instance.domain().getName()))
        .codomain(Universe.ARROW)
        .morphism(getName().appliedTo(instance.getName()));
        try {
            return TypedGraph.interpret(b.fetchResultMorphism());
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "execute", graphError);
        }
    }

    @Override
    public boolean isExecuted(TypedGraph instance) {
        return false;
    }

    @Override
    public boolean isExecutedCorrectly(TypedGraph instance) {
        return false;
    }

    @Override
    public boolean undo(TypedGraph instance) {
        return false;
    }

    @Override
    public String nameAsString() {
        return "<proxy>";
    }

    @Override
    public Graph inputArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph overlapArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph arity() {
        return Universe.ARROW;
    }
}
