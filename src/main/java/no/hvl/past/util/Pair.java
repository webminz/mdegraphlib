package no.hvl.past.util;

import java.util.Objects;
import java.util.function.Function;

public class Pair<X, Y> {

    private final X first;

    private final Y second;

    public Pair(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    public X getFirst() {
        return first;
    }

    public Y getSecond() {
        return second;
    }

    public X getLeft() {
        return getFirst();
    }

    public Y getRight() {
        return getSecond();
    }

    public <A, B> Pair<A, B> map(Function<X, A> firstFunction, Function<Y,B> secondFunction) {
        return new Pair<>(firstFunction.apply(first), secondFunction.apply(second));
    }

    public Pair<Y, X> revert() {
        return new Pair<>(second, first);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair other = (Pair) obj;
            return this.getFirst().equals(other.getFirst()) && this.getSecond().equals(other.getSecond());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "<"+ this.getFirst().toString() + "," + this.getSecond().toString() + ">";
    }


}
