package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

/**
 * The set of edges typed over the inputArity forms an surjective relation,
 * aka [1..*] on the association source side.
 */
public class Surjective implements GraphPredicate {

    private static final String SURJECTIVE_PRED_NAME = "[surjective]";

    private static Surjective instance;

    @Override
    public String nameAsString() {
        return SURJECTIVE_PRED_NAME;
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    private Surjective() {
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ARROW_TRG_NAME)
                .map(Triple::getLabel)
                .allMatch(n -> instance.allInstances(Universe.ARROW_THE_ARROW)
                        .filter(Triple::isEddge)
                        .map(Triple::getTarget)
                        .anyMatch(n::equals));
    }

    public static Surjective getInstance() {
        if (instance == null) {
            instance = new Surjective();
        }
        return instance;
    }
}
