package no.hvl.past.logic;

public abstract class BinaryFormulaCombinator<Sig extends Signature> implements Formula<Sig> {

    private final Formula<Sig> lhs;
    private final Formula<Sig> rhs;

    BinaryFormulaCombinator(Formula<Sig> lhs, Formula<Sig> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Formula<Sig> getLhs() {
        return lhs;
    }

    public Formula<Sig> getRhs() {
        return rhs;
    }
}
