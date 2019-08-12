package no.hvl.past.graph.names;

import java.util.Optional;

public class SimpleIdentifier extends Identifier {

    /**
     * Should be a FQN in the respective scope.
     */
    private final String name;

    public SimpleIdentifier(String name) {
        this.name = name;
    }

    @Override
    public Optional<String> getAuthority() {
        return Optional.empty();
    }

    @Override
    public byte[] getValue() {
        return name.getBytes();
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
