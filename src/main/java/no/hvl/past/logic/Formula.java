package no.hvl.past.logic;

/**
 * A syntactic representation of a class of models.
 */
public interface Formula<Sig extends Signature> {

    /**
     * Checks if a given structure satisfies this formula, i.e. if
     * it is an instance of the generated theory of this formula.
     */
    boolean isSatisfied(Model<Sig> model);

}
