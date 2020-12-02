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
    default boolean isSatisfied(Model<Graph> model) {
        if (model instanceof GraphMorphism) {
            GraphMorphism instance = (GraphMorphism) model;
            if (instance.codomain().equals(arity())) {
                return isExecutedCorrectly(instance);
            }
        }
        return false;
    }

    /**
     * Applies this operation to the input if possible,
     * creating new elements freely, i.e. the semantics of a graph
     * operation are encoded in a free-construction (epi-reflective subcategory).
     */
    GraphMorphism execute(GraphMorphism instance, ExecutionContext context);

    /**
     * Every operation can likewise be interpreted as a predicate,
     * which simply checks whether input and output are correctly related.
     * E.g. the operation + : Nat Nat -> Nat is also a predicate + : (Nat Nat) Nat
     * where  +((x,y),z) holds iff. x + y = z.
     */
    boolean isExecutedCorrectly(GraphMorphism instance);

    /**
     * Reverts an applied operation on an instance graph, by simply deleting all elements
     * that are typed over elements from the output graph.
     */
    GraphMorphism undo(GraphMorphism instance, ExecutionContext context);

    /**
     * Since a graph operation is a free construction, they
     * provide a unique way of `fixing' wrongly executed by operations
     * by first reverting the wrong elements and the applying the epi-reflector.
     */
    default GraphMorphism fix(GraphMorphism instance, ExecutionContext context) {
        if (!isExecutedCorrectly(instance)) {
            return execute(undo(instance, context), context);
        }
        return instance;
    }



}
