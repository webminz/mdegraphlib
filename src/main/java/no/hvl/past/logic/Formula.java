package no.hvl.past.logic;

/**
 * A syntactic representation of a class of models.
 */
public interface Formula<Sig extends Signature> {

    static <S extends Signature> Formula<S> bot() {
        return new FormulaLiteral.Bottom<>();
    }

    static <S extends Signature> Formula<S> top() {
        return new FormulaLiteral.Top<>();
    }

    default Formula<Sig> not() {
        return new UnaryFormulaCombinator.Negation<>(this);
    }

    default Formula<Sig> and(Formula<Sig> other) {
        return new Conjunction<>(this, other);
    }

    default Formula<Sig> or(Formula<Sig> other) {
        return new Disjunction<>(this, other);
    }

    default Formula<Sig> implies(Formula<Sig> other) {
        return new Implication<>(this, other);
    }

    default Formula<Sig> iff(Formula<Sig> other) {
        return new BiImplication<>(this, other);
    }

    /**
     * Checks if a given structure satisfies this formula, i.e. if
     * it is an instance of the generated theory of this formula.
     */
    boolean isSatisfied(Model<Sig> model);

}
