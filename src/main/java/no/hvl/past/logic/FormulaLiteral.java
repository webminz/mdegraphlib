package no.hvl.past.logic;

public abstract class FormulaLiteral<Sig extends Signature> implements Formula<Sig> {


    /**
     * Never satisfied.
     */
    public static class Bottom<Sig extends Signature> extends FormulaLiteral<Sig> {

        Bottom() {
        }

        @Override
        public boolean isSatisfied(Model<Sig> model) {
            return false;
        }


        @Override
        public boolean equals(Object obj) {
            return obj instanceof Bottom<?>;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    /**
     * Always satisfied.
     */
    public static class Top<Sig extends Signature> extends FormulaLiteral<Sig> {

        Top() {
        }

        @Override
        public boolean isSatisfied(Model<Sig> model) {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Top<?>;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

}


