package no.hvl.past.util;

import junit.framework.TestCase;
import org.junit.Test;

public class MultiplicityTest extends TestCase {


    @Test
    public void testCreation() {
        Multiplicity optional = Multiplicity.of(false, false);
        assertTrue(optional.isOptional());
        assertFalse(optional.isCollection());
        assertTrue(optional.isSingleValued());
        assertFalse(optional.isRequired());
        assertTrue(optional.isLowerUnbounded());
        assertFalse(optional.isUpperUnbounded());
        assertTrue(optional.isValid(0));
        assertTrue(optional.isValid(1));
        assertFalse(optional.isValid(4));
        assertEquals("?", optional.toString());

        Multiplicity mandatory = Multiplicity.of(true, false);
        assertFalse(mandatory.isOptional());
        assertTrue(mandatory.isRequired());
        assertFalse(mandatory.isCollection());
        assertTrue(mandatory.isSingleValued());
        assertFalse(mandatory.isLowerUnbounded());
        assertFalse(mandatory.isUpperUnbounded());
        assertFalse(mandatory.isValid(0));
        assertTrue(mandatory.isValid(1));
        assertFalse(mandatory.isValid(4));
        assertEquals("!", mandatory.toString());

        Multiplicity star = Multiplicity.of(false, true);
        assertTrue(star.isOptional());
        assertTrue(star.isCollection());
        assertFalse(star.isRequired());
        assertFalse(star.isSingleValued());
        assertTrue(star.isLowerUnbounded());
        assertTrue(star.isUpperUnbounded());
        assertTrue(star.isValid(0));
        assertTrue(star.isValid(1));
        assertTrue(star.isValid(23));
        assertEquals("*", star.toString());

        Multiplicity pluss = Multiplicity.of(true, true);
        assertFalse(pluss.isOptional());
        assertTrue(pluss.isCollection());
        assertTrue(pluss.isRequired());
        assertFalse(pluss.isSingleValued());
        assertFalse(pluss.isLowerUnbounded());
        assertTrue(pluss.isUpperUnbounded());
        assertFalse(pluss.isValid(0));
        assertTrue(pluss.isValid(1));
        assertTrue(pluss.isValid(42));
        assertEquals("+", pluss.toString());

        Multiplicity custom = Multiplicity.of(3, 7);
        assertFalse(custom.isOptional());
        assertTrue(custom.isCollection());
        assertTrue(custom.isRequired());
        assertFalse(custom.isSingleValued());
        assertFalse(custom.isLowerUnbounded());
        assertFalse(custom.isUpperUnbounded());
        assertFalse(custom.isValid(0));
        assertFalse(custom.isValid(1));
        assertTrue(custom.isValid(3));
        assertTrue(custom.isValid(5));
        assertTrue(custom.isValid(7));
        assertFalse(custom.isValid(8));
        assertFalse(custom.isValid(42));
        assertEquals("3..7", custom.toString());

        assertEquals("*..4", Multiplicity.of(-1, 4).toString());
        assertEquals("5..*", Multiplicity.of(5).toString());
    }

    @Test
    public void testAnd() {

        assertEquals(Multiplicity.of(3,5), Multiplicity.of(1, 5).and(Multiplicity.of(3, 7)));
        assertEquals(Multiplicity.of(2,4), Multiplicity.of(-1, 4).and(Multiplicity.of(2, -1)));

        Multiplicity optional = Multiplicity.of(false, false);
        Multiplicity mandatory = Multiplicity.of(true, false);
        Multiplicity star = Multiplicity.of(false, true);
        Multiplicity pluss = Multiplicity.of(true, true);

        assertEquals(mandatory, optional.and(mandatory));
        assertEquals(optional, optional.and(optional));
        assertEquals(optional, optional.and(star));
        assertEquals(mandatory, optional.and(pluss));

        assertEquals(mandatory, mandatory.and(mandatory));
        assertEquals(mandatory, mandatory.and(optional));
        assertEquals(mandatory, mandatory.and(star));
        assertEquals(mandatory, mandatory.and(pluss));

        assertEquals(star, star.and(star));
        assertEquals(optional, star.and(optional));
        assertEquals(mandatory, star.and(mandatory));
        assertEquals(pluss, star.and(pluss));

        assertEquals(mandatory, pluss.and(optional));
        assertEquals(mandatory, pluss.and(mandatory));
        assertEquals(pluss, pluss.and(star));
        assertEquals(pluss, pluss.and(pluss));
    }

    @Test
    public void testOr() {

        assertEquals(Multiplicity.of(1,7), Multiplicity.of(1, 5).or(Multiplicity.of(3, 7)));
        assertEquals(Multiplicity.of(-1,-1), Multiplicity.of(-1, 4).or(Multiplicity.of(2, -1)));

        Multiplicity optional = Multiplicity.of(false, false);
        Multiplicity mandatory = Multiplicity.of(true, false);
        Multiplicity star = Multiplicity.of(false, true);
        Multiplicity pluss = Multiplicity.of(true, true);

        assertEquals(optional, optional.or(mandatory));
        assertEquals(optional, optional.or(optional));
        assertEquals(star, optional.or(star));
        assertEquals(star, optional.or(pluss));

        assertEquals(mandatory, mandatory.or(mandatory));
        assertEquals(optional, mandatory.or(optional));
        assertEquals(star, mandatory.or(star));
        assertEquals(pluss, mandatory.or(pluss));

        assertEquals(star, star.or(star));
        assertEquals(star, star.or(optional));
        assertEquals(star, star.or(mandatory));
        assertEquals(star, star.or(pluss));

        assertEquals(star, pluss.or(optional));
        assertEquals(pluss, pluss.or(mandatory));
        assertEquals(star, pluss.or(star));
        assertEquals(pluss, pluss.or(pluss));

    }

}