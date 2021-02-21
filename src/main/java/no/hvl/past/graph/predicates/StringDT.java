package no.hvl.past.graph.predicates;

import no.hvl.past.attributes.StringValue;
import no.hvl.past.graph.*;

/**
 * Holds if all elements in the instance fibre are string values.
 */
public class StringDT implements GraphPredicate {

    private static final String STRING_PRED_NAME = "[string]";

    private static StringDT instance;

    @Override
    public String nameAsString() {
        return STRING_PRED_NAME;
    }


    @Override
    public GraphImpl arity() {
        return Universe.ONE_NODE;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .allMatch(t -> t.getLabel().isValue() && (t.getLabel() instanceof StringValue));
    }

    public static StringDT getInstance() {
        if (instance == null) {
            instance = new StringDT();
        }
        return instance;
    }
}
