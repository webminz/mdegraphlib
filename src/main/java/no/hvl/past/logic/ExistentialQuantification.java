package no.hvl.past.logic;

public class ExistentialQuantification<Sig extends  Signature> extends QuantifiedFormula<Sig> {

    public ExistentialQuantification(Matcher<Sig> matcher, Sig variables, Formula<Sig> subFormula) {
        super(matcher, variables, subFormula);
    }

    @Override
    public boolean isSatisfied(Model<Sig> model) {
        return getMatcher().allOccurrences(getVariables(), model).anyMatch(instance -> getNested().isSatisfied(instance));
    }

}
