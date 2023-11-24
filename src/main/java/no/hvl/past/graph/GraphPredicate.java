package no.hvl.past.graph;


import no.hvl.past.logic.Formula;
import no.hvl.past.ExtensionPoint;
import no.hvl.past.logic.Model;

import java.util.stream.Stream;

public interface GraphPredicate extends GraphTheory, ExtensionPoint {

    /**
     * Verifies whether an instance typed over the inputArity graph fulfills this predicate.
     */
    boolean check(GraphMorphism instance);


    @Override
    default boolean isSatisfied(Model<Graph> model) {
        if (model instanceof GraphMorphism) {
            GraphMorphism instance = (GraphMorphism) model;
            if (instance.codomain().equals(arity())) {
                return check(instance);
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

    default boolean diagramIsOfType(Diagram diagram) {
        if (diagram.label() instanceof GraphPredicate) {
            return labelIsEquivalent((GraphPredicate) diagram.label());
        }
        return false;
    }

    default Stream<Diagram> diagramsWithType(Sketch sketch) {
        return sketch.diagrams().filter(this::diagramIsOfType);
    }

    default boolean labelIsEquivalent(GraphPredicate graphPredicate) {
        return graphPredicate.getClass().isAssignableFrom(getClass());
    }

    @Override
    default Formula<Graph> iff(Formula<Graph> other) {
        if (other instanceof GraphPredicate) {
            return ((GraphPredicate) other).labelIsEquivalent(this) ? Formula.top() : Formula.bot();
        }
        return GraphTheory.super.iff(other);
    }
}
