package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.util.Multiplicity;

/**
 * Represents a variable multiplicity [m..n] on the source side.
 * Thus, every node on the target side should be pointed by at least m and at most n sources,
 */
public class SourceMultiplicity implements GraphPredicate {

    private final Multiplicity multiplicity;

    private SourceMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    @Override
    public String nameAsString() {
        return "[" + multiplicity.toString() + "|s]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ARROW_TRG_NAME).map(Triple::getTarget).allMatch(t -> {
            long n = instance.allInstances(Universe.ARROW_THE_ARROW).filter(e -> e.getTarget().equals(t)).count();
            return multiplicity.isValid(n);
        });
    }

    @Override
    public boolean labelIsEquivalent(GraphPredicate graphPredicate) {
        if (graphPredicate instanceof SourceMultiplicity) {
            SourceMultiplicity sm = (SourceMultiplicity) graphPredicate;
            return this.multiplicity.equals(sm.multiplicity);
        }
        return GraphPredicate.super.labelIsEquivalent(graphPredicate);
    }

    public static GraphPredicate getInstance(int lowerBound, int upperBound) {
        return new SourceMultiplicity(Multiplicity.of(lowerBound, upperBound));
    }

    public Multiplicity multiplicity() {
        return multiplicity;
    }
}
