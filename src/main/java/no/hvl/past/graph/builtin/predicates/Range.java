package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.attributes.FloatValue;
import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.graph.*;

import java.math.BigInteger;

/**
 * Holds if all elements in the instance fibre are numeric (integer or float) and are within the given range.
 */
public class Range implements GraphPredicate {

    private final long lower;
    private final long upper;

    private Range(long lower, long upper) {
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public String nameAsString() {
        return "[range("+lower+","+upper+")]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.ONE_NODE;
    }

    @Override
    public boolean check(TypedGraph instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .allMatch(t -> {
                    if (!t.getLabel().isValue()) {
                        return false;
                    }
                    if (t.getLabel() instanceof IntegerValue) {
                        IntegerValue iv = (IntegerValue) t.getLabel();
                        return (new IntegerValue(BigInteger.valueOf(lower)).lessEq(iv).isTrue() && iv.lessEq(new IntegerValue(BigInteger.valueOf(upper))).isTrue());
                    }
                    if (t.getLabel() instanceof FloatValue) {
                        FloatValue fv = (FloatValue) t.getLabel();
                        return new FloatValue(lower).lessEq(fv).isTrue() && fv.lessEq(new FloatValue(upper)).isTrue();

                    }
                    return false;
                });
    }

    public static Range getInstance(long lower, long upper) {
        return new Range(lower, upper);
    }



}
