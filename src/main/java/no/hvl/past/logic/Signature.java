package no.hvl.past.logic;

/**
 * The ``material'' for building worlds for a respective logic.
 * An instance of a class that implements this interface can be seen as
 * a set of variables.
 */
public interface Signature {

    /**
     * Returns true if the given model is at least syntactically correct w.r.t. this signature.
     */
    boolean isSyntacticallyCorrect(Model<? extends Signature> model);


}
