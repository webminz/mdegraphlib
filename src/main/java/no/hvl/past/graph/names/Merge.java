package no.hvl.past.graph.names;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Merge extends Name {

    private final List<Name> names;

    public Merge(List<Name> names) {
        this.names = names;
    }

    @Override
    public byte[] getValue() {
        int size = names.stream().map(n -> n.getValue().length).reduce(0, (a, b) -> a + b);
        byte[] result = new byte[size];
        int idx = 0;
        for (Name n : names) {
            byte[] toConcat = n.getValue();
            System.arraycopy(toConcat, 0, result, idx, toConcat.length);
            idx = idx + toConcat.length;
        }
        return result;
    }

    @Override
    public boolean isVariable() {
        return names.stream().anyMatch(Name::isVariable);
    }

    @Override
    public boolean isValue() {
        return names.stream().allMatch(Name::isValue);
    }

    @Override
    public boolean isIdentifier() {
        return names.stream().allMatch(Name::isIdentifier);
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return strategy.merge(this.names.stream().map(n -> n.print(strategy)).collect(Collectors.toSet()));
    }

    @Override
    public boolean contains(Name name) {
        return names.stream().anyMatch(n -> n.contains(name));
    }

    @Override
    public Name unprefix(Name name) {
        return new Merge(this.names.stream().map(n -> n.unprefix(name)).collect(Collectors.toList()));
    }

    @Override
    public Name unprefixAll() {
        return new Merge(this.names.stream().map(Name::unprefixAll).collect(Collectors.toList()));
    }

    public Optional<Name> findNameWithPrefix(Name prefix) {
        return this.names.stream()
                .filter(n -> n instanceof Prefix)
                .map(n -> (Prefix) n)
                .filter(p -> p.getPrefix().get().equals(prefix))
                .findFirst().map(Prefix::getNested);
    }

    public List<Name> getMembers() {
        return this.names;
    }


}
