package no.hvl.past.logic;


/**
 * Marker interface for models (instance worlds)
 * of a theory.
 */
public interface Model<T extends Theory<T>> {

    default boolean isInstance(T theory) {
        return theory.isInstance(this);
    }
}
