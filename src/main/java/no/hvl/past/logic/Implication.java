package no.hvl.past.logic;


public class Implication<Sig extends Signature> extends FormulaCombinator<Sig> {


    public static <Sig extends Signature> Formula<Sig> negation(Formula<Sig> formula) {
        return new Implication<>(formula, FormulaLiteral.bot());
    }


    public Implication(Formula<Sig> lhs, Formula<Sig> rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean isSatisfied(Model<Sig> model) {
        return !getLhs().isSatisfied(model) || getRhs().isSatisfied(model);
    }


}
