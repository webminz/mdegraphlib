package no.hvl.past.logic;

public abstract class FormulaCombinator<Sig extends Signature> implements Formula<Sig> {

    private final Formula<Sig> lhs;
    private final Formula<Sig> rhs;

    public FormulaCombinator(Formula<Sig> lhs, Formula<Sig> rhs) {
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
