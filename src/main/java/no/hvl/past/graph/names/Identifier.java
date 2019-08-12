package no.hvl.past.graph.names;

import java.util.Optional;

public abstract class Identifier extends Name {

    public abstract Optional<String> getAuthority();

    @Override
    public Name unprefix(Name name) {
        return this;
    }


    @Override
    public Name unprefixAll() {
        return this;
    }

    @Override
    public boolean isIdentifier() {
        return true;
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean contains(Name name) {
        return this.equals(name);
    }
}
