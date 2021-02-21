package no.hvl.past.names;

import no.hvl.past.logic.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropositionalLogicTest {

    @Test
    public void testCombinators() {
        Identifier A = Name.identifier("A");
        Identifier B = Name.identifier("B");
        Identifier C = Name.identifier("C");
        Signature sig = new NameSet(A, B, C);
        Model<NameSet> model = new NameSet(B, C); // B and C are true

        assertTrue(sig.isSyntacticallyCorrect(model));
        assertFalse(A.isSatisfied(model));
        assertTrue(C.isSatisfied(model));
        assertTrue(A.not().isSatisfied(model));
        assertTrue(B.and(C).isSatisfied(model));
        assertFalse(A.and(B).isSatisfied(model));
        assertTrue(A.or(B).isSatisfied(model));
        assertTrue(A.implies(B).isSatisfied(model));
        assertTrue(A.implies(B).iff(A.not().or(B)).isSatisfied(model));
    }


}
