package no.hvl.past.names;

import no.hvl.past.util.ByteUtils;

import java.util.Optional;

/**
 * Represents a real identifier. It therefore should
 * uniquely identify an element in a graph.
 */
public abstract class Identifier extends Name {


    @Override
    public final Name unprefix(Name name) {
        return this;
    }

    @Override
    public final Name unprefixAll() {
        return this;
    }

    @Override
    public final boolean isIdentifier() {
        return true;
    }

    @Override
    public final boolean isValue() {
        return false;
    }

    @Override
    public final boolean isVariable() {
        return false;
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return strategy.identifier(this);
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
    public final boolean isMultipart() {
        return false;
    }

    @Override
    public final boolean isDerived() {
        return false;
    }

    @Override
    public boolean isIndexed() {
        return false;
    }
}
