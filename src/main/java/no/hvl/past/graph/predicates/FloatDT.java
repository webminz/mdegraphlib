package no.hvl.past.graph.predicates;

import no.hvl.past.attributes.FloatValue;
import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.graph.*;

/**
 * Holds if all elements in the instance fibre are float values.
 */
public class FloatDT implements GraphPredicate {
    private static final String FLOAT_PRED_NAME = "[float]";

    private static FloatDT instance;
    @Override
    public String nameAsString() {
        return FLOAT_PRED_NAME;
    }

    @Override
    public GraphImpl arity() {
        return Universe.ONE_NODE;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .allMatch(t -> t.getLabel().isValue() && (t.getLabel() instanceof FloatValue || t.getLabel() instanceof IntegerValue)); // integers are contained in the floats
    }

    public static FloatDT getInstance() {
        if (instance == null) {
            instance = new FloatDT();
        }
        return instance;
    }
}
