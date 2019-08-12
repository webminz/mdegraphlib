package no.hvl.past.graph.names;

public class Variable extends Name {

    private final String name;

    Variable(String name) {
        this.name = name;
    }

    @Override
    public byte[] getValue() {
        return name.getBytes();
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
    public String print(PrintingStrategy strategy) {
        return name;
    }

    @Override
    public boolean contains(Name name) {
        return this.equals(name);
    }

    @Override
    public Name unprefix(Name name) {
        return this;
    }

    @Override
    public Name unprefixAll() {
        return this;
    }
}
