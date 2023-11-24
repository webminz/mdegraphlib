package no.hvl.past.attributes;

import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;

public final class BoolValue extends Value {

    private final boolean value;

    public BoolValue(boolean value) {
        this.value = value;
    }

    public BoolValue and(BoolValue other) {
        return new BoolValue(value && other.value);
    }

    public BoolValue or(BoolValue other) {
        return new BoolValue(value || other.value);
    }

    public BoolValue not() {
        return new BoolValue(!value);
    }

    public BoolValue implies(BoolValue other) {
        return new BoolValue(!value || other.value);
    }


    public BoolValue biImplies(BoolValue other) {
        return new BoolValue(this.value == other.value);
    }

    @Override
    public byte[] getValue() {
        byte[] result = new byte[2];
        result[0] = BOOL_VALUE_MAGIC_BYTE;
        result[1] = (byte) (value ? 1 : 0);
        return result;
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return value ? strategy.trueValue() : strategy.falseValue();
    }

    @Override
    public String toString() {
        return print(PrintingStrategy.IGNORE_PREFIX);
    }

    public BoolValue xor(BoolValue other) {
        return new BoolValue((this.value != other.value));
    }

    public StringValue asString() {
        if (this.value) {
            return StringValue.value("true");
        } else {
            return StringValue.value("false");
        }
    }

    public static Value tryParse(StringValue value) {
        String rep = value.getStringValue().toLowerCase();
        if (rep.equals("true") || rep.equals("t") || rep.equals("1")) {
            return BoolValue.trueValue();
        }
        if (rep.equals("false") || rep.equals("f") || rep.equals("0")) {
            return BoolValue.falseValue();
        }
        return ErrorValue.INSTANCE;
    }

    @Override
    public CompareResult compareWith(Name other) {
        if (other instanceof BoolValue) {
            BoolValue otherValue = (BoolValue) other;
            if (this.value == otherValue.value) {
                return CompareResult.EQUAL;
            }
            if (!this.value) {
                return CompareResult.LESS_THAN;
            } else {
                return CompareResult.BIGGER_THAN;
            }
        }
        return super.compareWith(other);
    }

    @Override
    public boolean inATotalOrderWith(Name other) {
        if (other instanceof BoolValue) {
            return true;
        }
        return super.inATotalOrderWith(other);
    }

    public boolean isTrue() {
        return this.value;
    }

    public boolean isFalse() {
        return !isTrue();
    }
}
