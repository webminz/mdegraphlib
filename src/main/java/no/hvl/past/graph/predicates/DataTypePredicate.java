package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;

/**
 * Marks the given node as a simple value domain (base data type).
 * Elements typed over a node with the given predicate are required to be values.
 */
public class DataTypePredicate implements GraphPredicate {

    private static final String PRED_NAME = "[value]";

    private static DataTypePredicate instance;

    private DataTypePredicate() {
    }

    @Override
    public String nameAsString() {
        return PRED_NAME;
    }

    @Override
    public GraphImpl arity() {
        return Universe.ONE_NODE;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .allMatch(t -> t.getLabel().isValue());
    }

    @Override
    public boolean diagramIsOfType(Diagram diagram) {
        return getClass().isAssignableFrom(diagram.label().getClass()) || StringDT.class.isAssignableFrom(diagram.label().getClass()) ||
                IntDT.class.isAssignableFrom(diagram.label().getClass()) || FloatDT.class.isAssignableFrom(diagram.label().getClass()) ||
                BoolDT.class.isAssignableFrom(diagram.label().getClass()) || EnumValue.class.isAssignableFrom(diagram.label().getClass());
    }

    public static DataTypePredicate getInstance() {
        if (instance == null) {
            instance = new DataTypePredicate();
        }
        return instance;
    }

}
