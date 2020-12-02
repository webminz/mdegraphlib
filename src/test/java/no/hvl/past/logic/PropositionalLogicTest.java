package no.hvl.past.logic;

import org.junit.Test;

import static org.junit.Assert.*;

public class PropositionalLogicTest {

    private static final Formula<PropositionalLogic> A = FormulaLiteral.bot(); // A = false
    private static final Formula<PropositionalLogic> B = FormulaLiteral.top(); // B = true
    private static final Formula<PropositionalLogic> C = FormulaLiteral.top(); // C = true


    @Test
    public void testCombinator() {
        assertFalse(A.isSatisfied(PropositionalLogic.MODEL));
        assertTrue(C.isSatisfied(PropositionalLogic.MODEL));
        assertTrue(Implication.negation(A).isSatisfied(PropositionalLogic.MODEL));
        assertTrue(new Conjunction<>(B, C).isSatisfied(PropositionalLogic.MODEL));
        assertFalse(new Conjunction<>(A, B).isSatisfied(PropositionalLogic.MODEL));
        assertTrue(new Disjunction<>(A, B).isSatisfied(PropositionalLogic.MODEL));
        assertTrue(new Implication<>(A, new Disjunction<>(B, C)).isSatisfied(PropositionalLogic.MODEL));
        assertTrue(new BiImplication<>(new Implication<>(A, B), new Disjunction<>(Implication.negation(A), B)).isSatisfied(PropositionalLogic.MODEL));
    }


}
