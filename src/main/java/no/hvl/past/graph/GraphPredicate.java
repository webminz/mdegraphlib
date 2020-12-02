package no.hvl.past.graph;


import no.hvl.past.ExtensionPoint;
import no.hvl.past.logic.Model;

public interface GraphPredicate extends GraphLabelTheory, ExtensionPoint {

    /**
     * Verifies whether an instance typed over the inputArity graph fulfills this predicate.
     */
    boolean check(GraphMorphism instance);


    @Override
    default boolean isSatisfied(Model<Graph> model) {
        if (model instanceof GraphMorphism) {
            GraphMorphism instance = (GraphMorphism) model;
            if (instance.codomain().equals(arity())) {
                check(instance);
            }
        }
        return false;
    }

    @Override
    default boolean isPredicate() {
        return true;
    }

    @Override
    default boolean isOperation() {
        return false;
    }
}
