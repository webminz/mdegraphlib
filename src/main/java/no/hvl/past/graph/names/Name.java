package no.hvl.past.graph.names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The abstract superclass of all types of names of graph elements.
 * Every graph element, i.e. node and edge label has to have a name, which
 * gives that particular node or edge an identity.
 * There are many different types of names, some represent variable names, other represent
 * actual values whilst others are identifiers that may be unique in a certain context.
 * Names can be prefixed, suffixed, re-combined and much more.
 *
 * The commonality of all names is that it can be serialized into a bytearray.
 * Equality of names is based on this bytarray.
 */
public abstract class Name {

    /**
     * A serizalizable represenation of this name, which also defines when two names are equal.
     */
    public abstract byte[] getValue();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Name) {
            Name other = (Name) obj;
            return Arrays.equals(this.getValue(), (other.getValue()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getValue());
    }

    public abstract boolean isVariable();

    public abstract boolean isValue();

    public abstract boolean isIdentifier();

    public abstract String print(PrintingStrategy strategy);

    public abstract boolean contains(Name name);

    // Prefix and suffix operations

    public Name prefix(Name name) {
        return new Prefix(this, name);
    }

    public boolean isTyped() {
        return getType().isPresent();
    }

    public Optional<Name> getType() {
        return Optional.empty();
    }

    public Name stripType() {
        return this;
    }

    public boolean hasPrefix(Name name) {
        return getPrefix().isPresent();
    }

    public Optional<Name> getPrefix() {
        return Optional.empty();
    }

    public abstract Name unprefix(Name name);

    public abstract Name unprefixAll();

    // Composition operations

    public Name composeSequentially(Name with) {
        return new BinaryOperation(this, with, BinaryOperation.Operation.SEQUENTIAL_COMPOSITION);
    }

    public Name typeBy(Name type) {
        return new BinaryOperation(this, type, BinaryOperation.Operation.TYPEDBY);
    }

    public Name query(Name query) {
        return new BinaryOperation(this, query, BinaryOperation.Operation.PULLBACK);
    }

    public Name sum(Name other) {
        return new BinaryOperation(this, other, BinaryOperation.Operation.COPRODUCT);
    }

    public Name merge(Name... others) {
        List<Name> result = new ArrayList<>();
        result.add(this);
        for (int i = 0; i < others.length; i++) {
            result.add(others[i]);
        }
        return new Merge(result);
    }


    // Factory methods

    public static Name identifier(String name) {
        return new SimpleIdentifier(name);
    }

    public static Name variable(String name) {
        return new Variable(name);
    }




}
