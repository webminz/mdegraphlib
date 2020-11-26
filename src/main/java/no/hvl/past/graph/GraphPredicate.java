package no.hvl.past.graph;


import no.hvl.past.ExtensionPoint;
import no.hvl.past.logic.Model;

public interface GraphPredicate extends GraphLabelTheory, ExtensionPoint {

    /**
     * Verifies whether an instance typed over the inputArity graph fulfills this predicate.
     */
    boolean check(TypedGraph instance);

    @Override
    default boolean isInstance(Model<GraphLabelTheory> model) {
        if (model instanceof TypedGraph) {
            return check((TypedGraph) model);
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
