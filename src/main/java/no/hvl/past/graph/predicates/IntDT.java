package no.hvl.past.graph.predicates;

import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.graph.*;

/**
 * Holds if all elements in the instance fibre are integer values.
 */
public class IntDT implements GraphPredicate {

    private static final String INT_PRED_NAME = "[int]";

    private static IntDT instance;
    @Override
    public String nameAsString() {
        return INT_PRED_NAME;
    }


    @Override
    public GraphImpl arity() {
        return Universe.ONE_NODE;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .allMatch(t -> t.getLabel().isValue() && (t.getLabel() instanceof IntegerValue));
    }

    public static IntDT getInstance() {
        if (instance == null) {
            instance = new IntDT();
        }
        return instance;
    }
}
