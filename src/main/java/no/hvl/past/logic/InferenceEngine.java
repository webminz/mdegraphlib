package no.hvl.past.logic;

import no.hvl.past.ExtensionPoint;

/**
 * Interface for an inference mechanism for a given logic.
 * It works on a purely syntactical level.
 */
public interface InferenceEngine<Sig extends Signature> extends ExtensionPoint {


    /**
     * Returns true if the given premise induces satisfaction of the conclusion.
     * Depending on the logic it may not terminate (semi-decidable).
     */
    boolean entails(Formula<Sig> premise, Formula<Sig> conclusion);

}
