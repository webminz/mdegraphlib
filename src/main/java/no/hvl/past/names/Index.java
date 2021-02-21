package no.hvl.past.names;

import no.hvl.past.util.ByteUtils;

/**
 * Represents an indexing for a given name, which
 * can be used to impose an order on given names.
 *
 * Note that indexing
 */
public class Index extends Name {

    private final Name wrapped;
    private final long index;

    public Index(Name wrapped, long index) {
        this.wrapped = wrapped;
        this.index = index;
    }

    @Override
    public byte[] getValue() {
        return ByteUtils.concat(wrapped.getValue(), ByteUtils.longToByteArray(index, false));
    }

    @Override
    public boolean isVariable() {
        return wrapped.isVariable();
    }

    @Override
    public boolean isValue() {
        return wrapped.isValue();
    }

    @Override
    public boolean isIdentifier() {
        return wrapped.isIdentifier();
    }

    @Override
    public boolean isDerived() {
        return wrapped.isDerived();
    }

    @Override
    public boolean isIndexed() {
        return true;
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return strategy.indexed(wrapped.print(strategy), index);
    }

    @Override
    public Name firstPart() {
        return this;
    }

    @Override
    public Name secondPart() {
        return this;
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public Name part(int i) {
        return this;
    }

    @Override
    public Name unprefix(Name name) {
        return new Index(wrapped.unprefix(name), index);
    }

    @Override
    public Name unprefixAll() {
        return new Index(wrapped.unprefixAll(), index);
    }

    @Override
    public CompareResult compareWith(Name other) {
        if (other instanceof Index) {
            Index otherIndex = (Index) other;
            if (this.wrapped.identity(otherIndex.wrapped)) {
                if (this.index == otherIndex.index) {
                    return CompareResult.EQUAL;
                }
                if (this.index < otherIndex.index) {
                    return CompareResult.LESS_THAN;
                } else {
                    return CompareResult.BIGGER_THAN;
                }
            }
        }
        return super.compareWith(other);
    }

    @Override
    public boolean inATotalOrderWith(Name other) {
        if (other instanceof Index) {
            return true;
        }
        return super.inATotalOrderWith(other);
    }

    @Override
    public String toString() {
        return print(PrintingStrategy.DETAILED);
    }
}
