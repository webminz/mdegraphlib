package no.hvl.past.logic;

import java.util.stream.Stream;

/**
 * A syntactic representation of a class of models (instance worlds).
 * @param <This> A theory is type-constrained by itself.
 */
public interface Theory<This extends Theory<This>> {

    /**
     * Returns true if the given model is defined by this theory.
     */
    boolean isInstance(Model<This> model);

    /**
     * Returns true if this theory is enumeratable, i.e.
     * the method models() returns results.
     */
    boolean isEnumeratable();

    /**
     * Returns true if the class of models described by this
     * theory is finite.
     */
    boolean isFinite();

    /**
     * Returns a stream of models, which are instances of this theory.
     * If this theory is not enumeratable the stream is empty.
     */
    Stream<Model<This>> models();
}
