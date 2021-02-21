package no.hvl.past.attributes;

import no.hvl.past.plugin.ExtensionPoint;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;
import no.hvl.past.util.ByteUtils;

/**
 * Represents the value of a user defined data type.
 */
public abstract class UserValue extends Value implements ExtensionPoint {

    @Override
    public byte[] getValue() {
        return ByteUtils.prefix(USER_VALUE_MAGIC_BYTE, representation().getBytes());
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return representation();
    }

    /**
     * Provides a representation of this user-value.
     * Note that the kernel of this functions defined the equality relation.
     * Thus, two equal value must have the same representation and two non-equal values
     * must never have the same representation.
     */
    public abstract String representation();

    public StringValue asString() {
        return StringValue.value(representation());
    }
}
