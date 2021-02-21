package no.hvl.past.logic;

abstract class QuantifiedFormula<Sig extends Signature> extends UnaryFormulaCombinator<Sig> {

    private final Matcher<Sig> matcher;
    private final Sig variables;

    QuantifiedFormula(Matcher<Sig> matcher, Sig variables, Formula<Sig> subFormula) {
        super(subFormula);
        this.matcher = matcher;
        this.variables = variables;
    }

    Matcher<Sig> getMatcher() {
        return matcher;
    }

    Sig getVariables() {
        return variables;
    }

}
