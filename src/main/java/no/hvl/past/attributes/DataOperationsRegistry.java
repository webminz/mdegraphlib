package no.hvl.past.attributes;

import no.hvl.past.names.Value;

import java.util.HashMap;
import java.util.Map;

public class DataOperationsRegistry {

    private static DataOperationsRegistry instance;

    private DataOperationsRegistry() {
    }

    private final Map<String, DataOperation> operations = new HashMap<>();

    public Value apply(String opName, Value[] arguments) {
        // TODO logging/reporting about what went wrong
        // Operation name not found
        if (!operations.containsKey(opName)) {
            return ErrorValue.INSTANCE;
        }
        // implementing strictness
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].equals(ErrorValue.INSTANCE)) {
                return ErrorValue.INSTANCE;
            }
        }
        DataOperation dataOperation = this.operations.get(opName);
        // arity does not match
        if (dataOperation.arity() != arguments.length) {
            return ErrorValue.INSTANCE;
        }
        return dataOperation.applyImplementation(arguments);
    }


    public static DataOperationsRegistry getInstance() {
        if (instance == null) {
            instance = new DataOperationsRegistry();
        }
        return instance;
    }

}
