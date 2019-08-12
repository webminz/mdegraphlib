package no.hvl.past.graph.names;


import java.util.Optional;

public class Prefix extends Name {

    private final Name nested;

    private final Name prefix;

    Prefix(Name nested, Name prefix) {
        this.nested = nested;
        this.prefix = prefix;
    }

    @Override
    public byte[] getValue() {
        int size = nested.getValue().length + prefix.getValue().length;
        byte[] result = new byte[size];
        System.arraycopy(prefix.getValue(), 0, result, 0, prefix.getValue().length);
        System.arraycopy(nested.getValue(), 0, result, prefix.getValue().length, nested.getValue().length);
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
    public String print(PrintingStrategy strategy) {
        String prefix = this.prefix.print(strategy);
        return strategy.transform(nested, prefix);
    }

    @Override
    public boolean contains(Name name) {
        return prefix.equals(name) || nested.contains(name);
    }

    @Override
    public Name unprefix(Name name) {
        if (this.prefix.equals(name)) {
            return this.nested.unprefix(name);
        }
        return new Prefix(this.prefix, this.nested.unprefix(name));
    }

    @Override
    public Name unprefixAll() {
        return nested;
    }

    public Name getNested() {
        return nested;
    }

    public Optional<Name> getPrefix() {
        return Optional.of(prefix);
    }

    @Override
    public String toString() {
        return this.prefix.toString() + "." + this.nested.toString();
    }

    @Override
    public boolean hasPrefix(Name name) {
        return this.prefix.equals(name);
    }
}
