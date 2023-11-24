package no.hvl.past.logic;

public class UniversalQuantification<Sig extends Signature> extends QuantifiedFormula<Sig> {

    public UniversalQuantification(Matcher<Sig> matcher, Sig variables, Formula<Sig> subFormula) {
        super(matcher, variables, subFormula);
    }

    @Override
    public boolean isSatisfied(Model<Sig> model) {
        return getMatcher().allOccurrences(getVariables(), model).allMatch((Model<Sig> instance) -> getNested().isSatisfied(instance));
    }
}
