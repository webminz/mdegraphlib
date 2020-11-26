package no.hvl.past.graph.builtin.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;
import no.hvl.past.util.ShouldNotHappenException;

/**
 * Adds an attribute (edge to a base type node) with a constant value to all nodes in the instance fibre.
 */
public class ConstantValue implements GraphOperation {

    private final Value value;

    private ConstantValue(Value value) {
        this.value = value;
    }

    @Override
    public GraphImpl outputArity() {
        return Universe.ARROW;
    }

    @Override
    public TypedGraph execute(TypedGraph instance, ExecutionContext context) {
        GraphBuilders builders = new GraphBuilders().importGraph(instance.domain());
        instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .map(s -> new Triple(
                        s.getLabel(),
                        getName().appliedTo(s.getLabel()),
                        value)
                ).forEach(triple -> {
            builders.edge(triple.getSource(), triple.getLabel(), triple.getTarget());
            builders.map(triple.getLabel(), Universe.ARROW_LBL_NAME);
            builders.map(triple.getTarget(), Universe.ARROW_TRG_NAME);
        });
        builders.graph(getName().appliedTo(instance.domain().getName()));
        builders.codomain(Universe.ARROW);
        builders.morphism(getName().appliedTo(instance.getName()));
        try {
            return TypedGraph.interpret(builders.fetchResultMorphism());
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
        return "<constant("+ value.print(PrintingStrategy.IGNORE_PREFIX) +")>";
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
        return Universe.ARROW;
    }
}
