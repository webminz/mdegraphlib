package no.hvl.past.graph.names;

import java.util.Optional;

public class BinaryOperation extends Name {

    enum Operation {

        SEQUENTIAL_COMPOSITION,

        PULLBACK,

        COPRODUCT,

        TYPEDBY

    }

    private final Operation operation;

    private final Name first;

    private final Name second;

    BinaryOperation(Name first, Name second, Operation operation) {
        this.operation = operation;
        this.first = first;
        this.second = second;
    }

    public Operation getOperation() {
        return operation;
    }

    public Name getFirst() {
        return first;
    }

    public Name getSecond() {
        return second;
    }

    @Override
    public byte[] getValue() {
        byte[] fst = first.getValue();
        byte[] snd = second.getValue();
        byte[] result = new byte[1 + fst.length + snd.length];
        result[0] = (byte) this.operation.ordinal();
        System.arraycopy(fst, 0, result, 1, fst.length);
        System.arraycopy(snd, 0, result, fst.length + 1, snd.length);
        return result;
    }

    @Override
    public boolean isVariable() {
        return first.isVariable() || second.isVariable();
    }

    @Override
    public boolean isValue() {
        return first.isValue() && second.isValue();
    }

    @Override
    public boolean isIdentifier() {
        return first.isIdentifier() && second.isIdentifier();
    }

    @Override
    public boolean isTyped() {
        return this.operation == Operation.TYPEDBY;
    }

    @Override
    public String print(PrintingStrategy strategy) {
        String firstTransformed = first.print(strategy);
        String secondTransformed = second.print(strategy);
        switch (operation) {
            case SEQUENTIAL_COMPOSITION:
                return strategy.sequentialComposition(firstTransformed, secondTransformed);
            case PULLBACK:
                return strategy.pullback(firstTransformed, secondTransformed);
            case COPRODUCT:
                return strategy.coproduct(firstTransformed, secondTransformed);
            case TYPEDBY:
                return strategy.typedBy(firstTransformed, secondTransformed);
                default:
                    throw new Error("Not implemented");
        }
    }

    @Override
    public boolean contains(Name name) {
        return first.contains(name) || second.contains(name);
    }

    @Override
    public Name unprefix(Name name) {
        return new BinaryOperation(first.unprefix(name), second.unprefix(name), this.operation);
    }

    @Override
    public Name unprefixAll() {
        return new BinaryOperation(first.unprefixAll(), second.unprefixAll(), this.operation);
    }

    @Override
    public Name stripType() {
        if (this.operation == Operation.TYPEDBY) {
            return this.first;
        }
        return this;
    }

    @Override
    public Optional<Name> getType() {
        if (this.operation == Operation.TYPEDBY) {
            return Optional.of(this.second);
        }
        return Optional.empty();
    }


}
