package no.hvl.past.attributes;

import no.hvl.past.ExtensionPoint;
import no.hvl.past.names.Value;

/**
 * An operation on values.
 */
public interface DataOperation extends ExtensionPoint {

    /**
     * The name of the operation.
     */
    String name();

    /**
     * The arity of the operation, i.e. the number of arguments.
     */
    int arity();

    /**
     * Provides a value that is the result of this operation application on the given values.
     */
    Value applyImplementation(Value[] arguments);

    default Value apply(Value[] arguments) {
        if (arguments.length != arity()) {
            return ErrorValue.INSTANCE;
        }
        return applyImplementation(arguments);
    }


}
