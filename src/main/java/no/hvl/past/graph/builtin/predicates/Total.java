package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

/**
 * The set of edges typed over the inputArity forms a total relation,
 * aka [1..*] on the association target side.
 */
public class Total implements GraphPredicate {
    private static final String TOTAL_PRED_NAME = "[total]";

    private static Total instance;


    @Override
    public String nameAsString() {
        return TOTAL_PRED_NAME;
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    private Total() {
    }

    @Override
    public boolean check(TypedGraph instance) {
        return instance.allInstances(Universe.ARROW_SRC_NAME)
                .map(Triple::getLabel)
                .allMatch(n -> instance.allInstances(Universe.ARROW_THE_ARROW)
                        .filter(Triple::isEddge)
                        .map(Triple::getSource)
                        .anyMatch(n::equals));
    }

    public static Total getInstance() {
        if (instance == null) {
            instance = new Total();
        }
        return instance;
    }
}
