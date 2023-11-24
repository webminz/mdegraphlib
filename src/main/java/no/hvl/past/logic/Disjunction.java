package no.hvl.past.logic;

public class Disjunction<Sig extends Signature> extends BinaryFormulaCombinator<Sig> {

    public Disjunction(Formula<Sig> lhs, Formula<Sig> rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean isSatisfied(Model<Sig> model) {
        return getLhs().isSatisfied(model) || getRhs().isSatisfied(model);
    }
}
