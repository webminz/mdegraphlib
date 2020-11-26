package no.hvl.past.attributes;

import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;
import no.hvl.past.util.ByteUtils;

import java.math.BigInteger;

public final class StringValue extends Value {

    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    public StringValue concat(StringValue other) {
        return new StringValue(this.value + other.value);
    }

    public IntegerValue indexOf(StringValue sub) {
        return new IntegerValue(BigInteger.valueOf(this.value.indexOf(sub.value)));
    }

    public StringValue substring(IntegerValue from, IntegerValue length) {
        return new StringValue(this.value.substring(from.intCast(), from.intCast() + length.intCast()));
    }

    public String getStringValue() {
        return value;
    }

    @Override
    public byte[] getValue() {
        return ByteUtils.prefix(STRING_VALUE_MAGIC_BYTE, value.getBytes());
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return strategy.stringValue(value);
    }


    @Override
    public String toString() {
        return print(PrintingStrategy.IGNORE_PREFIX);
    }

    public IntegerValue length() {
        return IntegerValue.value(this.value.length());
    }

    public StringValue reverse() {
        StringBuilder reverse = new StringBuilder();
        for (int i = this.value.length() - 1; i >= 0; i--) {
            reverse.append(this.value.charAt(i));
        }
        return new StringValue(reverse.toString());
    }

    public BoolValue lessEq(StringValue rhs) {
        return this.value.compareTo(rhs.value) <= 0 ? BoolValue.trueValue() : BoolValue.falseValue();
    }

    @Override
    public CompareResult compareWith(Name other) {
        if (other instanceof StringValue) {
            StringValue sVal = (StringValue) other;
            if (this.value.equals(sVal.value)) {
                return CompareResult.EQUAL;
            }
            if (this.value.compareTo(sVal.value) < 0) {
                return CompareResult.LESS_THAN;
            } else {
                return CompareResult.BIGGER_THAN;
            }
        }
        return super.compareWith(other);
    }
}
