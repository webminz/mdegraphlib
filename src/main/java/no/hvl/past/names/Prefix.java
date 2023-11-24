package no.hvl.past.names;


import java.util.Optional;

public final class Prefix extends Name {

    private final Name nested;

    private final Name prefix;

    Prefix(Name nested, Name prefix) {
        this.nested = nested;
        this.prefix = prefix;
    }

    @Override
    public byte[] getValue() {
        int size = nested.getValue().length + prefix.getValue().length;
        byte[] result = new byte[size + 1];
        result[0] = PREFIX_MAGIC_BYTE;
        System.arraycopy(prefix.getValue(), 0, result, 1, prefix.getValue().length);
        System.arraycopy(nested.getValue(), 0, result, prefix.getValue().length + 1, nested.getValue().length);
        return result;
    }

    @Override
    public boolean isVariable() {
        return nested.isVariable();
    }

    @Override
    public boolean isValue() {
        return nested.isValue();
    }

    @Override
    public boolean isIdentifier() {
        return nested.isIdentifier();
    }

    @Override
    public boolean isDerived() {
        return nested.isDerived();
    }

    @Override
    public boolean isIndexed() {
        return nested.isIndexed();
    }

    @Override
    public String print(PrintingStrategy strategy) {
        String prefix = this.prefix.print(strategy);
        String prefixed = this.nested.print(strategy);
        return strategy.handlePrefix(prefix, prefixed);
    }

    @Override
    public boolean contains(Name name) {
        if (name instanceof Prefix) {
            Prefix other = (Prefix) name;
            return this.prefix.equals(other.prefix) && this.nested.contains(other.nested);
        }
        return  nested.contains(name);
    }

    @Override
    public Name unprefix(Name name) {
        if (this.prefix.equals(name)) {
            return this.nested.unprefix(name);
        }
        return new Prefix(this.nested.unprefix(name), this.prefix);
    }

    @Override
    public Name unprefixAll() {
        return nested.unprefixAll();
    }

    @Override
    public Name firstPart() {
        return prefix;
    }

    @Override
    public Name secondPart() {
        return nested;
    }

    @Override
    public Name part(int i) {
        return nested;
    }

    @Override
    public boolean isMultipart() {
        return true;
    }

    Name getNested() {
        return nested;
    }

    public Optional<Name> getPrefix() {
        return Optional.of(prefix);
    }

    @Override
    public String toString() {
        return this.print(PrintingStrategy.DETAILED);
    }

    @Override
    public boolean hasPrefix(Name name) {
        return this.prefix.equals(name);
    }

    @Override
    public CompareResult compareWith(Name other) {
        if (other instanceof Prefix) {
            Prefix otherPrefixed = (Prefix) other;
            return this.nested.compareWith(otherPrefixed.nested);
        }
        return super.compareWith(other);
    }

    @Override
    public Name unprefixTop() {
        return nested;
    }
}
