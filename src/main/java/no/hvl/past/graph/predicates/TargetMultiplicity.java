package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.util.Multiplicity;

/**
 * Represents a variable multiplicity [m..n] on the target side.
 * Thus, every node on the source side should point at least m and at most n targets,
 */
public class TargetMultiplicity implements GraphPredicate {

    private final Multiplicity multiplicity;

    private TargetMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    @Override
    public String nameAsString() {
        return "[" + multiplicity.toString() + "|t]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ARROW_SRC_NAME).map(Triple::getSource).allMatch(s -> {
            long n = instance.allInstances(Universe.ARROW_THE_ARROW).filter(t -> t.getSource().equals(s)).count();
            return multiplicity.isValid(n);
        });

    }

    @Override
    public boolean labelIsEquivalent(GraphPredicate graphPredicate) {
        if (graphPredicate instanceof TargetMultiplicity) {
            TargetMultiplicity other = (TargetMultiplicity) graphPredicate;
            return this.multiplicity.equals(other.multiplicity);
        }
        return GraphPredicate.super.labelIsEquivalent(graphPredicate);
    }


    public static GraphPredicate getInstance(int lowerBound, int upperBound) {
        return new TargetMultiplicity(Multiplicity.of(lowerBound, upperBound));
    }

    public static GraphPredicate getInstance(Multiplicity multiplicity) {
        return new TargetMultiplicity(multiplicity);
    }


    public Multiplicity multiplicity() {
        return multiplicity;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }
}
