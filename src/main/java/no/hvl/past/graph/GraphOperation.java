package no.hvl.past.graph;

import no.hvl.past.ExtensionPoint;
import no.hvl.past.logic.Model;

public interface GraphOperation extends GraphLabelTheory, ExtensionPoint {

    /**
     * A subgraph of the operation's arity (scope) graph,
     * which represents the input to the operation.
     */
    Graph inputArity();

    /**
     * A subgraph of the operation's arity (scope) graph,
     * which represents the overlap of to the operation,
     * i.e. elements shared between in- and output.
     */
    Graph overlapArity();

    /**
     * A subgraph of the operation's arity (scope) graph,
     * which represents the output to the operation.
     */
    Graph outputArity();

    @Override
    default boolean isPredicate() {
        return false;
    }

    @Override
    default boolean isOperation() {
        return true;
    }

    @Override
    default  boolean isInstance(Model<GraphLabelTheory> model) {
        if (model instanceof TypedGraph) {
            return isExecutedCorrectly((TypedGraph) model);
        }
        return false;
    }

    /**
     * Applies this operation to the input if possible,
     * creating new elements freely.
     */
    TypedGraph execute(TypedGraph instance, ExecutionContext context);

    boolean isExecuted(TypedGraph instance);

    boolean isExecutedCorrectly(TypedGraph instance);

    boolean undo(TypedGraph instance);

    default TypedGraph fix(TypedGraph instance, ExecutionContext context) {
        if (!isExecutedCorrectly(instance)) {

        }
        return instance;
    }






}
