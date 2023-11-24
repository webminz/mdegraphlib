package no.hvl.past.logic;


public abstract class UnaryFormulaCombinator<Sig extends Signature> implements Formula<Sig> {

    private final Formula<Sig> nested;

    UnaryFormulaCombinator(Formula<Sig> nested) {
        this.nested = nested;
    }

    Formula<Sig> getNested() {
        return nested;
    }

    public static class Negation<Sig extends Signature> extends UnaryFormulaCombinator<Sig> {

        public Negation(Formula<Sig> nested) {
            super(nested);
        }

        @Override
        public boolean isSatisfied(Model<Sig> model) {
            return !getNested().isSatisfied(model);
        }
    }

}
