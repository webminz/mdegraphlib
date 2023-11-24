package no.hvl.past.names;

import no.hvl.past.util.ByteUtils;

/**
 * Represent a variable, which are used as temporarily references for various elements.
 * Variables play an important role in logic frameworks where there main purpose is substitution.
 * To avoid ambiguities when definining identifiers with the same names, {@link Variable}s have their own
 * type.
 */
public final class Variable extends Name {

    private final String name;

    Variable(String name) {
        this.name = name;
    }

    public String getVariableName() {
        return name;
    }

    @Override
    public byte[] getValue() {
        return ByteUtils.prefix(VARIABLE_MAGIC_BYTE, name.getBytes());
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public boolean isIdentifier() {
        return false;
    }

    @Override
    public boolean isDerived() {
        return false;
    }

    @Override
    public final String print(PrintingStrategy strategy) {
        return strategy.variable(this);
    }

    @Override
    public final Name unprefix(Name name) {
        return this;
    }

    @Override
    public final Name unprefixAll() {
        return this;
    }

    @Override
    public final Name firstPart() {
        return this;
    }

    @Override
    public final Name secondPart() {
        return this;
    }

    @Override
    public final Name part(int i) {
        return this;
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isIndexed() {
        return false;
    }
}
