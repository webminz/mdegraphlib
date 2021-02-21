package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

/**
 * Represents a variable multiplicity [m..n] on the source side.
 * Thus, every node on the target side should be pointed by at least m and at most n sources,
 */
public class SourceMultiplicity implements GraphPredicate {

    private final int lowerBound;
    private final int upperBound;

    private SourceMultiplicity(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public String nameAsString() {
        return "[" + (lowerBound < 0 ? "*" : lowerBound + ".." +
                (upperBound < 0 ? "*" : upperBound + "|s]"));
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        if (upperBound < 0 && lowerBound <= 0) {
            return true;
        } else if (upperBound == 1 && lowerBound == 1) {
            return Injective.getInstance().check(instance) && Surjective.getInstance().check(instance);
        } else if (upperBound == 1 && lowerBound <= 0) { // 0..1 -> inj predicate
            return Injective.getInstance().check(instance);
        } else if (upperBound < 0 && lowerBound == 1) {  // 1..* -> surj predicate
            return Surjective.getInstance().check(instance);
        } else {
            return instance.allInstances(Universe.ARROW_TRG_NAME).map(Triple::getTarget).allMatch(s -> {
                long n = instance.allInstances(Universe.ARROW_THE_ARROW).filter(t -> t.getTarget().equals(s)).count();
                if (lowerBound >= 0 && upperBound >= 0) {
                    return n >= lowerBound && n <= upperBound;
                } else if (upperBound <= 0) {
                    return n >= lowerBound;
                } else {
                    return n <= upperBound;
                }
            });
        }
    }


    public static GraphPredicate getInstance(int lowerBound, int upperBound) {
        return new SourceMultiplicity(lowerBound, upperBound);
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }
}
