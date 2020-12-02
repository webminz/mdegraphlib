package no.hvl.past.logic;

/**
 * The interface for a translation between different logics.
 * The respective implementation must translate models and formulas.
 * The translation is correct if it respects the satisfaction relation in the well-known way.
 * For more details, refer to institution theory.
 */
public interface LogicTranslation<From extends Signature, To extends Signature> {

    /**
     * Translates one model from one logic to anther.
     */
    Model<To> translateModel(Model<From> model);

    /**
     * Translates a formula from one logic to anther.
     */
    Formula<To> translateFormula(Formula<From> formula);

}
