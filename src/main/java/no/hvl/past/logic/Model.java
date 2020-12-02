package no.hvl.past.logic;


/**
 * Marker interface for models (instance worlds)
 */
public interface Model<Sig extends Signature> {

    /**
     * Returns true if this structures satisfies the given formular, i.e
     * a model of the respective theory.
     */
    default boolean isInstance(Formula<Sig> theory) {
        return theory.isSatisfied(this);
    }
}
