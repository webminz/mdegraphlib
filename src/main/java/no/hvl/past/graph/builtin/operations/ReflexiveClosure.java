package no.hvl.past.graph.builtin.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.util.ShouldNotHappenException;

/**
 * Computes the transitive close of a loop edge.
 */
public class ReflexiveClosure implements GraphOperation {

    @Override
    public Graph outputArity() {
        return Universe.LOOP;
    }

    @Override
    public TypedGraph execute(TypedGraph instance, ExecutionContext context) {
        GraphBuilders b = new GraphBuilders().importGraph(instance.domain());
        instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .map(n -> Triple.edge(n.getLabel(), getName().appliedTo(n.getLabel()), n.getLabel()))
                .forEach(edge -> {
                    b.edge(edge.getSource(), edge.getLabel(), edge.getTarget());
                    b.map(edge.getLabel(), Universe.LOOP_THE_LOOP.getLabel());
                });
        b.graph(getName().appliedTo(instance.domain().getName()));
        b.codomain(Universe.LOOP);
        b.morphism(getName().appliedTo(instance.getName()));
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
        return "<reflClosure>";
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
        return Universe.LOOP;
    }
}
