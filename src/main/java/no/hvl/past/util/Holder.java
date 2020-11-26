package no.hvl.past.util;


import java.util.Optional;
import java.util.function.Function;

/**
 * A utility class that can hold exactly one value.
 * The holder will hold the first value forever.
 * Any attempts to fill an already filled holder have no effect.
 */
public final class Holder<V> {

    private V value;

    public Holder() {
    }

    public boolean hasValue() {
        return this.value != null;
    }

    public boolean set(V value) {
        if (this.value == null) {
            this.value = value;
            return true;
        }
        return false;
    }

    public Optional<V> safeGet() {
        if (this.value != null) {
            return Optional.of(value);
        }
        return Optional.empty();
    }

    public <U> Holder<U> map(Function<V, U> function) {
        Holder<U> result = new Holder<>();
        if (hasValue()) {
            result.set(function.apply(value));
        }
        return result;
    }

    public <U> Holder<U> flatMap(Function<V, Holder<U>> function) {
        Holder<U> result = new Holder<>();
        if (hasValue()) {
            return function.apply(value);
        }
        return result;
    }

    public V unsafeGet() {
        return value;
    }
}
