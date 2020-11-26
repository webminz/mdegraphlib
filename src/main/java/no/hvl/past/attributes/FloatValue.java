package no.hvl.past.attributes;

import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;
import no.hvl.past.util.ByteUtils;

import java.nio.ByteBuffer;

public final class FloatValue extends Value {

    private final double value;

    public FloatValue(double value) {
        this.value = value;
    }

    public FloatValue add(FloatValue summand) {
        return new FloatValue(this.value + summand.value);
    }

    public FloatValue sub(FloatValue minuend) {
        return new FloatValue(this.value - minuend.value);
    }

    public FloatValue mul(FloatValue factor) {
        return new FloatValue(this.value * factor.value);
    }

    public FloatValue div(FloatValue divisor) {
        return new FloatValue(this.value / divisor.value);
    }

    public boolean equals(FloatValue other, FloatValue error) {
        double lower = this.value - error.value;
        double upper = this.value + error.value;
        return other.value >= lower || other.value <= upper;
    }

    public BoolValue lessEq(FloatValue other) {
        return this.value <= other.value ?  BoolValue.trueValue() : BoolValue.falseValue();
    }

    @Override
    public byte[] getValue() {
        byte[] result = new byte[8];
        ByteBuffer.wrap(result).putDouble(value);
        return ByteUtils.prefix(Name.FLOAT_VALUE_MAGIC_BYTE, result);
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return strategy.floatValue(value);
    }

    @Override
    public String toString() {
        return print(PrintingStrategy.IGNORE_PREFIX);
    }

    public boolean isZero() {
        return this.value == 0.0;
    }

    public static FloatValue pow(FloatValue floatValue, FloatValue floatValue1) {
        return new FloatValue(Math.pow(floatValue.value, floatValue1.value));
    }

    public StringValue asString() {
        return StringValue.value(Double.toString(this.value));
    }

    public static Value tryParse(StringValue value) {
        try {
            double result = Double.parseDouble(value.getStringValue());
            return FloatValue.value(result);
        } catch (NumberFormatException ex) {
            return ErrorValue.INSTANCE;
        }
    }

    @Override
    public CompareResult compareWith(Name other) {
        if (other instanceof FloatValue) {
            FloatValue fVal = (FloatValue) other;
            if (this.value == fVal.value) {
                return CompareResult.EQUAL;
            }
            if (this.value < fVal.value) {
                return CompareResult.LESS_THAN;
            } else {
                return CompareResult.BIGGER_THAN;
            }
        }
        return super.compareWith(other);
    }
}
