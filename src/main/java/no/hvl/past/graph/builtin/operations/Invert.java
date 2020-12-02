package no.hvl.past.graph.builtin.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.util.ShouldNotHappenException;

/**
 * Inverts an edge.
 */
public class Invert implements GraphOperation {

    @Override
    public Graph outputArity() {
        try {
            return new GraphBuilders().edge("1", "10", "0").graph("ARROW_INV").getResult(Graph.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "outputArity", graphError);
        }
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) {
        GraphBuilders b = new GraphBuilders().importGraph(instance.domain());
        instance.allInstances(Universe.CYCLE_FWD)
                .map(Triple::inverse)
                .forEach(edge -> {
                    b.edge(edge.getSource(), edge.getLabel(), edge.getTarget())
                            .map(edge.getLabel(), Universe.CYCLE_BWD.getLabel());
                });
        b.graph(getName().appliedTo(instance.domain().getName()))
                .codomain(Universe.CYCLE)
                .morphism(getName().appliedTo(instance.getName()));
        try {
            return TypedGraph.interpret(b.getResult(GraphMorphism.class));
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
        return "<invert>";
    }

    @Override
    public Graph inputArity() {
        return Universe.ARROW;
    }

    @Override
    public Graph overlapArity() {
        return Universe.PAIR;
    }

    @Override
    public Graph arity() {
        return Universe.CYCLE;
    }
}
