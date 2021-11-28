package no.hvl.past.names;

import no.hvl.past.util.ByteUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class MulitaryCombinator extends Combinator {

    enum Operation {
        UNION {
            @Override
            public String print(PrintingStrategy strategy, List<String> arguments) {
                return strategy.merge(arguments);
            }
        }, CONCAT {
            @Override
            public String print(PrintingStrategy strategy, List<String> arguments) {
                return null;
            }
        };

        public abstract String print(PrintingStrategy strategy, List<String> arguments);

    }

    private final List<Name> names;
    private final Operation op;

    MulitaryCombinator(List<Name> names, Operation op) {
        this.names = names;
        this.op = op;
    }

    @Override
    public byte[] getValue() {
        byte[] result = new byte[2];
        result[0] = MULTIARY_OP_MAGIC_BYTE;
        result[1] = (byte) op.ordinal();
        for (Name n : names) {
            result = ByteUtils.concat(result, n.getValue());
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
    public boolean isIndexed() {
        return false;
    }

    @Override
    public String print(PrintingStrategy strategy) {
        return this.op.print(strategy, this.names.stream().map(n -> n.print(strategy)).collect(Collectors.toList()));
    }

    @Override
    public boolean contains(Name name) {
        return names.stream().anyMatch(n -> n.contains(name));
    }

    @Override
    public Name unprefix(Name name) {
        return new MulitaryCombinator(this.names.stream().map(n -> n.unprefix(name)).collect(Collectors.toList()), op);
    }

    @Override
    public Name unprefixAll() {
        return new MulitaryCombinator(this.names.stream().map(Name::unprefixAll).collect(Collectors.toList()), op);
    }

    @Override
    public Name firstPart() {
        return this.names.iterator().next();
    }

    @Override
    public Name secondPart() {
        return part(2);
    }

    @Override
    public Name part(int i) {
        Iterator<Name> iterator = this.names.iterator();
        Name current = iterator.next();
        if (i <= 0) {
            return current;
        }
        int j = 0;
        while (j < i && iterator.hasNext()) {
            current = iterator.next();
            j++;
        }
        return current;
    }

    @Override
    public boolean isMultipart() {
        return names.size() > 1;
    }

    public Optional<Name> findNameWithPrefix(Name prefix) {
        return this.names.stream()
                .filter(n -> n.hasPrefix(prefix))
                .map(n -> (Prefix) n)
                .findFirst().map(Prefix::getNested);
    }

    @Override
    public String toString() {
        return print(PrintingStrategy.DETAILED);
    }
}
