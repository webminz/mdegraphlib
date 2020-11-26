package no.hvl.past.names;

import java.util.Optional;

/**
 * A simple identifier that is given by a human readable String.
 */
public final class SimpleIdentifier extends Identifier {

    /**
     * Should be a FQN in the respective scope.
     */
    private final String name;

    SimpleIdentifier(String name) {
        this.name = name;
    }

    @Override
    public Optional<String> getAuthority() {
        return Optional.empty();
    }

    @Override
    public byte[] serialize() {
        return name.getBytes();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public CompareResult compareWith(Name other) {
        if (other instanceof SimpleIdentifier) {
            // simple identifiers are compared by the lexigoprahical order on strings.
            SimpleIdentifier otherAsSI = (SimpleIdentifier) other;
            if (identity(other)) {
                return CompareResult.EQUAL;
            }
            return this.name.compareTo(otherAsSI.name) < 0 ? CompareResult.LESS_THAN : CompareResult.BIGGER_THAN;
        }
        return super.compareWith(other);
    }
}
