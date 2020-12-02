package no.hvl.past.logic;

/**
 * The signature for propositional logic, i.e. the empty set.
 */
public final class PropositionalLogic implements Signature {

    public static PropositionalLogic INSTANCE = new PropositionalLogic();
    public static Model MODEL = new Model();

    private PropositionalLogic() {
    }

    public static final class Model implements no.hvl.past.logic.Model<PropositionalLogic> {

        private Model() {
        }
    }

}
