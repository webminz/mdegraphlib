package no.hvl.past.logic;

import no.hvl.past.ExtensionPoint;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The interface for an arbitrary (possibly highly optimized off-the-shelf) logic solver.
 * The solver can check whether a theory (given as formulas) is satisfiable under previously stated
 * axioms and if it is satisfiable it can try to find one or all models that satisfy the theory.
 * Solvers are stateful, i.e. their knowledge base can be loaded (axioms are assumed) and reset (all axioms are forgotten).
 */
public interface Solver<Sig extends Signature> extends ExtensionPoint {

    /**
     * Resets the knowledge base, i.e. after calling this method there are no more axioms.
     */
    void reset();

    /**
     * Loads the knowledge base with a set of given axioms.
     * This method workds incrementally, thus the set of axioms is extended with each call of this method.
     */
    void loadKnowledgeBase(Set<Formula<Sig>> axioms);

    /**
     * Returns true if there exists at at least one model that satisfies the given formula and all axioms.
     * Note that there are logics that are semi- or undecideable and therefor this method may run forever.
     */
    boolean isSatisfiable(Formula<Sig> formula);

    /**
     * Tries to find an arbitrary model that satisfies the given formula and all axioms.
     * If the respective theory is not satisfiable, Optional.empty is returned.
     * Note that there are logics that are semi- or undecideable and therefor this method may run forever.
     */
    Optional<Model<Sig>> oneModel(Formula<Sig> formula);

    /**
     * Tries to find *all* models that satisfy the given formula and all axioms.
     * If there are infinitely many, the stream may be infinite.
     * Also note that there are logics that are semi- or undecideable and therefor this method may run forever.
     */
    Stream<Model<Sig>> allModels(Formula<Sig> formula);

}
