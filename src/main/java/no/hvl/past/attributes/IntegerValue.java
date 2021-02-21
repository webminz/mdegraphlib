package no.hvl.past.attributes;

import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;
import no.hvl.past.util.ByteUtils;

import java.math.BigInteger;

public final class IntegerValue extends Value {

    public static IntegerValue ZERO = new IntegerValue(BigInteger.ZERO);
    private final BigInteger value;

    public IntegerValue(BigInteger value) {
        this.value = value;
    }

    public IntegerValue plus(IntegerValue other) {
        return new IntegerValue(this.value.add(other.value));
    }

    public IntegerValue minus(IntegerValue other) {
        return new IntegerValue(this.value.subtract(other.value));
    }

    public IntegerValue multiply(IntegerValue other) {
        return new IntegerValue(this.value.multiply(other.value));
    }

    public IntegerValue integerDivision(IntegerValue other) {
        return new IntegerValue(this.value.divide(other.value));
    }

    public IntegerValue modulo(IntegerValue other) {
        return new IntegerValue(this.value.mod(other.value));
    }

    public IntegerValue power(IntegerValue exponent) {
        return new IntegerValue(this.value.pow(exponent.value.abs().intValue()));
    }

    public IntegerValue max(IntegerValue other) {
        return new IntegerValue(this.value.max(other.value));
    }

    public IntegerValue min(IntegerValue other) {
        return new IntegerValue(this.value.min(other.value));
    }

    public IntegerValue mean(IntegerValue other) {
        return new IntegerValue(this.value.add(other.value).divide(BigInteger.valueOf(2)));
    }

    public FloatValue toFloat() {
        return new FloatValue(this.value.doubleValue());
    }
    public BoolValue lessEq(IntegerValue other) {
        return this.value.compareTo(other.value) <= 0 ?  BoolValue.trueValue() : BoolValue.falseValue();
    }

    public int intCast() {
        return this.value.intValue();
    }

    @Override
    public byte[] getValue() {
        return ByteUtils.prefix(INT_VALUE_MAGIC_BYTE, value.toByteArray());
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return strategy.integerValue(value);
    }

    @Override
    public String toString() {
        return print(PrintingStrategy.IGNORE_PREFIX);
    }

    public boolean isZero() {
        return this.value.equals(BigInteger.ZERO);
    }

    public StringValue asString() {
        return StringValue.value(this.value.toString());
    }

    public static Value tryParse(StringValue value) {
        try {
            String toParse = value.getStringValue();
            if (toParse.contains(".")) {
                toParse = toParse.substring(0, toParse.indexOf('.'));
            }
            BigInteger result = new BigInteger(toParse);
            return new IntegerValue(result);
        } catch (NumberFormatException ex) {
            return ErrorValue.INSTANCE;
        }
    }

    @Override
    public CompareResult compareWith(Name other) {
        if (other instanceof IntegerValue) {
            IntegerValue iVal = (IntegerValue) other;
            if (this.value.equals(iVal.value)) {
                return CompareResult.EQUAL;
            }
            if (this.value.compareTo(iVal.value) < 0) {
                return CompareResult.LESS_THAN;
            } else {
                return CompareResult.BIGGER_THAN;
            }
        }
        if (other instanceof FloatValue) {
            return toFloat().compareWith(other);
        }
        return super.compareWith(other);
    }

    @Override
    public boolean inATotalOrderWith(Name other) {
        if (other instanceof IntegerValue || other instanceof FloatValue) {
            return true;
        }
        return super.inATotalOrderWith(other);
    }
}
