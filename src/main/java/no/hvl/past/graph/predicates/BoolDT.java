package no.hvl.past.graph.predicates;

import no.hvl.past.attributes.BoolValue;
import no.hvl.past.graph.*;

/**
 * Holds if all elements in the instance fibre are boolean values.
 */
public class BoolDT implements GraphPredicate {
    private static final String BOOL_PRED_NAME = "[bool]";

    private static BoolDT instance;
    @Override
    public String nameAsString() {
        return BOOL_PRED_NAME;
    }

    @Override
    public GraphImpl arity() {
        return Universe.ONE_NODE;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .allMatch(t -> t.getLabel().isValue() && (t.getLabel() instanceof BoolValue));
    }

    public static BoolDT getInstance() {
        if (instance == null) {
            instance = new BoolDT();
        }
        return instance;
    }
}
