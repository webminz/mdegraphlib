package no.hvl.past.attributes;

import no.hvl.past.names.Value;

public interface DataPredicate extends DataOperation {

    default boolean isSatisfied(Value[] arguments) {
        Value result = apply(arguments);
        if (result instanceof BoolValue) {
            return ((BoolValue) result).isTrue();
        } else {
            return false;
        }
    }

}
