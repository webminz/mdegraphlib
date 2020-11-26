package no.hvl.past.attributes;

import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;

public class ErrorValue extends Value {

    public static final ErrorValue INSTANCE = new ErrorValue();

    private ErrorValue() {
    }

    @Override
    public byte[] getValue() {
        byte[] result = new byte[1];
        result[0] = ERROR_VALUE;
        return result;
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return "⊥";
    }
}
