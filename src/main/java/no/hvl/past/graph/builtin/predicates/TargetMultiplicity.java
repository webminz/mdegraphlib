package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

/**
 * Represents a variable multiplicity [m..n] on the target side.
 * Thus, every node on the source side should point at least m and at most n targets,
 */
public class TargetMultiplicity implements GraphPredicate {

    private final int lowerBound;
    private final int upperBound;

    private TargetMultiplicity(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public String nameAsString() {
        return "[" + (lowerBound < 0 ? "*" : lowerBound + ".." +
                (upperBound < 0 ? "*" : upperBound + "|t]"));
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ARROW_SRC_NAME).map(Triple::getSource).allMatch(s -> {
            long n = instance.allInstances(Universe.ARROW_THE_ARROW).filter(t -> t.getSource().equals(s)).count();
            if (lowerBound >= 0 && upperBound >= 0) {
                return n >= lowerBound && n <= upperBound;
            } else if (upperBound <= 0) {
                return n >= lowerBound;
            } else {
                return n <= upperBound;
            }

        });
    }


    public static GraphPredicate getInstance(int lowerBound, int upperBound) {
        if (upperBound < 0 && lowerBound <= 0) {
            return new GraphPredicate() {
                @Override
                public boolean check(GraphMorphism instance) {
                    return true;
                }
                @Override
                public String nameAsString() {
                    return "[0..*|t]";
                }
                @Override
                public GraphImpl arity() {
                    return Universe.ARROW;
                }
            };
        }else if (upperBound == 1 && lowerBound == 1) { // 1..1. -> total + function predicate
            return new GraphPredicate() {
                @Override
                public boolean check(GraphMorphism instance) {
                    return Function.getInstance().check(instance) && Total.getInstance().check(instance);
                }

                @Override
                public String nameAsString() {
                    return "[1..1|t]";
                }

                @Override
                public GraphImpl arity() {
                    return Universe.ARROW;
                }

            };
        } else if (upperBound == 1 && lowerBound < 1) { // 0..1 -> function predicate
            return Function.getInstance();
        } else if (upperBound < 0 && lowerBound == 1) {  // 1..* -> total predicate
            return Total.getInstance();
        } else {
            return new TargetMultiplicity(lowerBound, upperBound);
        }
    }
}
