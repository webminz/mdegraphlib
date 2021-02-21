package no.hvl.past.names;


import no.hvl.past.names.Identifier;
import no.hvl.past.names.Name;
import no.hvl.past.names.UUIDIdentifier;
import no.hvl.past.util.ProperComparator;
import org.junit.Test;

import static org.junit.Assert.*;

public class NamesTest {

    @Test
    public void testUniqueness() {
        Name hiId = Name.identifier("Hi");
        Name hiVar = Name.variable("Hi");
        Name hiVal = Name.value("Hi");

        assertEquals(hiId, hiId);
        assertEquals(hiVal, hiVal);
        assertEquals(hiVar, hiVar);

        assertNotEquals(hiId, hiVar);
        assertNotEquals(hiVar, hiVal);
        assertNotEquals(hiId, hiVal);

        Name anon1 = Name.anonymousIdentifier();
        Name anon2 = Name.anonymousIdentifier();
        Name anon3 = Name.anonymousIdentifier();

        assertNotEquals(anon1, anon2);
        assertNotEquals(anon2, anon3);
        assertNotEquals(anon1, anon3);
    }

    @Test
    public void testPrefixing() {
        Name foo = Name.identifier("foo");
        Name bar = Name.identifier("bar");
        Name baz = Name.identifier("baz");
        Name a = Name.identifier("A");
        Name p1 = a.prefixWith(baz);
        Name p2 = p1.prefixWith(baz);
        assertNotEquals(a, p1);
        assertNotEquals(p1, p2);
        assertEquals(a, p1.unprefix(baz));
        assertEquals(a, p2.unprefix(baz));
        assertEquals(a, p2.unprefixAll());

        Name b = Name.identifier("b");
        assertEquals(a, a.prefixWith(baz).prefixWith(bar).prefixWith(foo).unprefixAll());
        assertEquals(a.prefixWith(baz).prefixWith(foo),  a.prefixWith(baz).prefixWith(bar).prefixWith(foo).unprefix(bar));

        Name complex = a.prefixWith(baz).times(b.prefixWith(bar)).prefixWith(foo);
        assertEquals(a.times(b.prefixWith(bar)).prefixWith(foo), complex.unprefix(baz));
        assertEquals(a.times(b), complex.unprefixAll());
   }


    @Test
    public void testUUIDs() {
        UUIDIdentifier id1 = Name.randomUUID();
        UUIDIdentifier time1 = Name.timeBasedUUID();
        UUIDIdentifier time2 = Name.timeBasedUUID();
        assertNotEquals(id1, time1);
        assertNotEquals(time1, time2);
        assertNotEquals(id1, time2);
        assertEquals(UUIDIdentifier.Version.VERSION_1, time1.getVersion());
        assertEquals(UUIDIdentifier.Version.VERSION_1, time2.getVersion());
        assertEquals(UUIDIdentifier.Version.VERSION_4, id1.getVersion());
        UUIDIdentifier namespace1 = Name.namespaceBased(id1, Name.identifier("a"));
        UUIDIdentifier namespace2 = Name.namespaceBased(id1, Name.identifier("a"));
        assertEquals(namespace1, namespace2);
        assertEquals(UUIDIdentifier.Version.VERSION_5, namespace1.getVersion());
        assertEquals(UUIDIdentifier.Version.VERSION_5, namespace2.getVersion());
        assertNotEquals(namespace1, id1);
        assertNotEquals(namespace1, time1);
        assertNotEquals(namespace1, time2);
    }

    @Test
    public void testDecorations() {
        Name a = Name.identifier("A");
        Name b = Name.identifier("B");
        Name pairAB = a.pair(b);
        Name bAsPrefixA = a.prefixWith(b);
        Name aPlusB = a.sum(b);
        Name aComposeB = a.composeSequentially(b);
        Name aElementOfB = a.elementOf(b);
        Name aPBAgainstB = a.query(b);
        Name aCrossB = a.times(b);
        Name ATypeByB = a.typeBy(b);
        Name AExtendsB = a.subTypeOf(b);

        Name aInverted = a.inverse();
        Name aIterated = a.iterated();
        Name aComplement = a.complement();
        Name aOptional = a.optional();
        Name aMandatory = a.mandatory();

        assertTrue(pairAB.isMultipart());
        assertTrue(bAsPrefixA.isMultipart());
        assertTrue(aPlusB.isMultipart());
        assertTrue(aComposeB.isMultipart());
        assertTrue(aElementOfB.isMultipart());
        assertTrue(aPBAgainstB.isMultipart());
        assertTrue(aCrossB.isMultipart());
        assertTrue(ATypeByB.isMultipart());
        assertFalse(aInverted.isMultipart());
        assertFalse(aIterated.isMultipart());
        assertFalse(aComplement.isMultipart());
        assertFalse(aOptional.isMultipart());
        assertFalse(aMandatory.isMultipart());


        assertTrue(pairAB.isIdentifier());
        assertTrue(bAsPrefixA.isIdentifier());
        assertTrue(aPlusB.isIdentifier());
        assertTrue(aComposeB.isIdentifier());
        assertTrue(aElementOfB.isIdentifier());
        assertTrue(aPBAgainstB.isIdentifier());
        assertTrue(aCrossB.isIdentifier());
        assertTrue(ATypeByB.isIdentifier());
        assertTrue(aInverted.isIdentifier());
        assertTrue(aIterated.isIdentifier());
        assertTrue(aComplement.isIdentifier());
        assertTrue(aOptional.isIdentifier());
        assertTrue(aMandatory.isIdentifier());

        assertEquals(a, aComposeB.firstPart());
        assertEquals(a, aCrossB.firstPart());
        assertEquals(a, aElementOfB.firstPart());
        assertEquals(a, aPBAgainstB.firstPart());
        assertEquals(a, aPlusB.firstPart());
        assertEquals(a, pairAB.firstPart());
        assertEquals(a, ATypeByB.firstPart());
        assertEquals(a, AExtendsB.firstPart());
        assertEquals(b, aComposeB.secondPart());
        assertEquals(b, aCrossB.secondPart());
        assertEquals(b, aElementOfB.secondPart());
        assertEquals(b, aPBAgainstB.secondPart());
        assertEquals(b, aPlusB.secondPart());
        assertEquals(b, pairAB.secondPart());
        assertEquals(b, ATypeByB.secondPart());
        assertEquals(b, AExtendsB.secondPart());

        assertTrue(aComposeB.contains(a));
        assertTrue(aComposeB.contains(b));
        assertTrue(aComposeB.contains(aComposeB));
        assertFalse(aComposeB.contains(aIterated));
        assertFalse(aComposeB.contains(aElementOfB));

        assertTrue(aElementOfB.contains(a));
        assertTrue(aElementOfB.contains(b));
        assertTrue(aElementOfB.contains(aElementOfB));
        assertFalse(aElementOfB.contains(aIterated));
        assertFalse(aElementOfB.contains(aComposeB));
    }

    @Test
    public void testComparison() {

        Identifier a = Name.identifier("a");
        Name a0 = a.index(0);
        Name a1 = a.index(1);
        Name a2 = a.index(2);

        assertEquals(a.compareWith(a0), ProperComparator.CompareResult.INCOMPARABLE);
        assertEquals(a0.compareWith(a1), ProperComparator.CompareResult.LESS_THAN);
        assertEquals(a2.compareWith(a0), ProperComparator.CompareResult.BIGGER_THAN);

    }





}
