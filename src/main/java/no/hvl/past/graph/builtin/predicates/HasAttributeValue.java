package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;

/**
 * Validates whether an edge, representing an attribute, points to the given value.
 */
public class HasAttributeValue implements GraphPredicate {

    private final Value value;

    private HasAttributeValue(Value value) {
        this.value = value;
    }

    @Override
    public boolean check(TypedGraph instance) {
        return instance.allInstances(Universe.ARROW_THE_ARROW).allMatch(triple -> triple.getTarget().equals(value));
    }

    @Override
    public String nameAsString() {
        return "[hasValue(" + value.print(PrintingStrategy.IGNORE_PREFIX) + ")]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    public static HasAttributeValue getInstance(Value value) {
        return new HasAttributeValue(value);
    }
}
