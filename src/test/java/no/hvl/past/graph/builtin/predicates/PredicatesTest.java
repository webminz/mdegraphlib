package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.names.Name;
import org.junit.Test;

import static org.junit.Assert.*;

public class PredicatesTest {


    @Test
    public void testSingleton() throws GraphError {
        GraphMorphism m0 = new GraphBuilders().domain(Universe.EMPTY).codomain(Universe.ONE_NODE).morphism("M0").fetchResultMorphism();
        GraphMorphism m1 = new GraphBuilders().node("A").graph("oneA").codomain(Universe.ONE_NODE).map(Name.identifier("A"), Universe.ONE_NODE_THE_NODE).morphism("M1").fetchResultMorphism();
        GraphMorphism m2 = new GraphBuilders().node("A").node("B").node("C").graph("ABC").codomain(Universe.ONE_NODE)
                .map(Name.identifier("A"), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("B"), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("C"), Universe.ONE_NODE_THE_NODE)
                .morphism("M2")
                .fetchResultMorphism();
        assertFalse(Singleton.getInstance().check(TypedGraph.interpret(m0)));
        assertTrue(Singleton.getInstance().check(TypedGraph.interpret(m1)));
        assertFalse(Singleton.getInstance().check(TypedGraph.interpret(m2)));
    }

//
//
//    @Test
//    public void testStringDT() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphImpl twoString = new GraphBuilder("one").node(Name.value("Hello")).node(Name.value("World")).build();
//        GraphImpl oneStringOneInt = new GraphBuilder("one").node(Name.value("Hello")).node(Name.value(23)).build();
//        GraphImpl mixed = new GraphBuilder("x").node("Id").node(Name.variable("X")).node(Name.value("")).build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, StringDT.getInstance().arity()).build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", twoString, StringDT.getInstance().arity())
//                .map(Name.value("Hello"), Name.variable("0"))
//                .map(Name.value("World"), Name.variable("0")).build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", oneStringOneInt, StringDT.getInstance().arity())
//                .map(Name.value("Hello"), Name.variable("0"))
//                .map(Name.value(23), Name.variable("0")).build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M0", mixed, StringDT.getInstance().arity())
//                .map(Name.identifier("Id"), Name.variable("0"))
//                .map(Name.variable("X"), Name.variable("0"))
//                .map(Name.value(""), Name.variable("0")).build();
//        assertTrue(StringDT.getInstance().check(m0));
//        assertTrue(StringDT.getInstance().check(m1));
//        assertFalse(StringDT.getInstance().check(m2));
//        assertFalse(StringDT.getInstance().check(m3));
//
//    }
//
//    @Test
//    public void testIntDT() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphImpl twoString = new GraphBuilder("one").node(Name.value(23)).node(Name.value(42)).build();
//        GraphImpl oneStringOneInt = new GraphBuilder("one").node(Name.value("Hello")).node(Name.value(23)).build();
//        GraphImpl mixed = new GraphBuilder("x").node("Id").node(Name.variable("X")).node(Name.value(0)).build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, IntDT.getInstance().arity()).build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", twoString, IntDT.getInstance().arity())
//                .map(Name.value(23), Name.variable("0"))
//                .map(Name.value(42), Name.variable("0")).build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", oneStringOneInt, IntDT.getInstance().arity())
//                .map(Name.value("Hello"), Name.variable("0"))
//                .map(Name.value(23), Name.variable("0")).build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M0", mixed, IntDT.getInstance().arity())
//                .map(Name.identifier("Id"), Name.variable("0"))
//                .map(Name.variable("X"), Name.variable("0"))
//                .map(Name.value(0), Name.variable("0")).build();
//        assertTrue(IntDT.getInstance().check(m0));
//        assertTrue(IntDT.getInstance().check(m1));
//        assertFalse(IntDT.getInstance().check(m2));
//        assertFalse(IntDT.getInstance().check(m3));
//    }
//
//
//    @Test
//    public void testFloatDT() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphImpl twoString = new GraphBuilder("one").node(Name.value(3.14)).node(Name.value(2.178)).build();
//        GraphImpl oneStringOneInt = new GraphBuilder("one").node(Name.value("Hello")).node(Name.value(23.0)).build();
//        GraphImpl mixed = new GraphBuilder("x").node("Id").node(Name.variable("X")).node(Name.value(0.5)).build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, FloatDT.getInstance().arity()).build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", twoString, FloatDT.getInstance().arity())
//                .map(Name.value(3.14), Name.variable("0"))
//                .map(Name.value(2.178), Name.variable("0")).build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", oneStringOneInt, FloatDT.getInstance().arity())
//                .map(Name.value("Hello"), Name.variable("0"))
//                .map(Name.value(23.0), Name.variable("0")).build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M0", mixed, FloatDT.getInstance().arity())
//                .map(Name.identifier("Id"), Name.variable("0"))
//                .map(Name.variable("X"), Name.variable("0"))
//                .map(Name.value(0.5), Name.variable("0")).build();
//        assertTrue(FloatDT.getInstance().check(m0.interpret()));
//        assertTrue(FloatDT.getInstance().check(m1.interpret()));
//        assertFalse(FloatDT.getInstance().check(m2.interpret()));
//        assertFalse(FloatDT.getInstance().check(m3.interpret()));
//    }
//
//    @Test
//    public void testBoolDT() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphImpl twoString = new GraphBuilder("one").node(Name.trueValue()).node(Name.falseValue()).build();
//        GraphImpl oneStringOneInt = new GraphBuilder("one").node(Name.value("Hello")).node(Name.trueValue()).build();
//        GraphImpl mixed = new GraphBuilder("x").node("Id").node(Name.variable("X")).node(Name.falseValue()).build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, BoolDT.getInstance().arity()).build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", twoString, BoolDT.getInstance().arity())
//                .map(Name.trueValue(), Name.variable("0"))
//                .map(Name.falseValue(), Name.variable("0")).build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", oneStringOneInt, BoolDT.getInstance().arity())
//                .map(Name.value("Hello"), Name.variable("0"))
//                .map(Name.trueValue(), Name.variable("0")).build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M0", mixed, BoolDT.getInstance().arity())
//                .map(Name.identifier("Id"), Name.variable("0"))
//                .map(Name.variable("X"), Name.variable("0"))
//                .map(Name.falseValue(), Name.variable("0")).build();
//        assertTrue(BoolDT.getInstance().check(m0.interpret()));
//        assertTrue(BoolDT.getInstance().check(m1.interpret()));
//        assertFalse(BoolDT.getInstance().check(m2.interpret()));
//        assertFalse(BoolDT.getInstance().check(m3.interpret()));
//    }
//
//    @Test
//    public void testRange() {
//        GraphPredicate p = Range.getInstance(4, 6);
//        assertEquals("[range(4,6)]", p.getName());
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphImpl twoInRange = new GraphBuilder("one").node(Name.value(5)).node(Name.value(5.1)).build();
//        GraphImpl oneOutOfRange = new GraphBuilder("one").node(Name.value(7)).node(Name.value(4.999)).build();
//        GraphImpl mixed = new GraphBuilder("x").node("Id").node(Name.variable("X")).node(Name.value(6)).build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Singleton.getInstance().inputArity()).build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", twoInRange, p.inputArity())
//                .map(Name.value(5), Name.variable("0"))
//                .map(Name.value(5.1), Name.variable("0")).build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", oneOutOfRange, p.inputArity())
//                .map(Name.value(7), Name.variable("0"))
//                .map(Name.value(4.999), Name.variable("0")).build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M0", mixed, p.inputArity())
//                .map(Name.identifier("Id"), Name.variable("0"))
//                .map(Name.variable("X"), Name.variable("0"))
//                .map(Name.value(6), Name.variable("0")).build();
//        assertTrue(p.check(m0));
//        assertTrue(p.check(m1));
//        assertFalse(p.check(m2));
//        assertFalse(p.check(m3));
//    }
//
//
//    @Test
//    public void testRegex() {
//        GraphPredicate p = Regex.getInstance("ab+");
//        assertEquals("[regex(ab+)]", p.name());
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphImpl twoInRange = new GraphBuilder("one").node(Name.value("abb")).node(Name.identifier("ab")).build();
//        GraphImpl oneOutOfRange = new GraphBuilder("one").node(Name.value("a")).node(Name.value("abbbbb")).build();
//        GraphImpl mixed = new GraphBuilder("x").node("ab").node(Name.variable("X")).node(Name.value(6)).build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, p.inputArity()).build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", twoInRange, p.inputArity())
//                .map(Name.value("abb"), Name.variable("0"))
//                .map(Name.identifier("ab"), Name.variable("0")).build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", oneOutOfRange, p.inputArity())
//                .map(Name.value("a"), Name.variable("0"))
//                .map(Name.value("abbbbb"), Name.variable("0")).build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M0", mixed, p.inputArity())
//                .map(Name.identifier("ab"), Name.variable("0"))
//                .map(Name.variable("X"), Name.variable("0"))
//                .map(Name.value(6), Name.variable("0")).build();
//        assertTrue(p.check(m0));
//        assertTrue(p.check(m1));
//        assertFalse(p.check(m2));
//        assertFalse(p.check(m3));
//    }
//
//    @Test
//    public void testInjective() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Injective.getInstance().inputArity()).build();
//        GraphImpl single = new GraphBuilder("single").edge("1", "2", "3").build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M1", single, Injective.getInstance().inputArity())
//                .map(Name.identifier("1"), Name.variable("0"))
//                .map(Name.identifier("2"), Name.variable("1"))
//                .map(Name.identifier("3"), Name.variable("2")).build();
//        GraphImpl neitherInjNorSurj = new GraphBuilder("g1")
//                .edge("A", "i", "1")
//                .edge("B","ii","1")
//                .node("2").build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M2", neitherInjNorSurj, Injective.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("i"), Name.variable("1"))
//                .map(Name.identifier("ii"), Name.variable("1"))
//                .map(Name.identifier("1"), Name.variable("2"))
//                .map(Name.identifier("2"), Name.variable("2")).build();
//        GraphImpl injButNotSurj = new GraphBuilder("g2")
//                .edge("A", "i", "1")
//                .node("2").build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M3", injButNotSurj, Injective.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("i"), Name.variable("1"))
//                .map(Name.identifier("1"), Name.variable("2"))
//                .map(Name.identifier("2"), Name.variable("2")).build();
//        GraphImpl surjButNorInj = new GraphBuilder("g3")
//                .edge("A", "i", "1")
//                .edge("B", "ii", "2")
//                .edge("C", "iii", "2")
//                .build();
//        GraphMorphismImpl m4 = new GraphMorphismImpl.Builder("M4", surjButNorInj, Injective.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("i"), Name.variable("1"))
//                .map(Name.identifier("ii"), Name.variable("1"))
//                .map(Name.identifier("iii"), Name.variable("1"))
//                .map(Name.identifier("1"), Name.variable("2"))
//                .map(Name.identifier("2"), Name.variable("2")).build();
//        assertTrue(Injective.getInstance().check(m0));
//        assertTrue(Injective.getInstance().check(m1));
//        assertFalse(Injective.getInstance().check(m2));
//        assertTrue(Injective.getInstance().check(m3));
//        assertFalse(Injective.getInstance().check(m4));
//    }
//
//
//    @Test
//    public void testSurjective() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Surjective.getInstance().inputArity()).build();
//        GraphImpl single = new GraphBuilder("single").edge("1", "2", "3").build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M1", single, Surjective.getInstance().inputArity())
//                .map(Name.identifier("1"), Name.variable("0"))
//                .map(Name.identifier("2"), Name.variable("1"))
//                .map(Name.identifier("3"), Name.variable("2")).build();
//        GraphImpl neitherInjNorSurj = new GraphBuilder("g1")
//                .edge("A", "i", "1")
//                .edge("B","ii","1")
//                .node("2").build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M2", neitherInjNorSurj, Surjective.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("i"), Name.variable("1"))
//                .map(Name.identifier("ii"), Name.variable("1"))
//                .map(Name.identifier("1"), Name.variable("2"))
//                .map(Name.identifier("2"), Name.variable("2")).build();
//        GraphImpl injButNotSurj = new GraphBuilder("g2")
//                .edge("A", "i", "1")
//                .node("2").build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M3", injButNotSurj, Surjective.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("i"), Name.variable("1"))
//                .map(Name.identifier("1"), Name.variable("2"))
//                .map(Name.identifier("2"), Name.variable("2")).build();
//        GraphImpl surjButNorInj = new GraphBuilder("g3")
//                .edge("A", "i", "1")
//                .edge("B", "ii", "2")
//                .edge("C", "iii", "2")
//                .build();
//        GraphMorphismImpl m4 = new GraphMorphismImpl.Builder("M4", surjButNorInj, Surjective.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("i"), Name.variable("1"))
//                .map(Name.identifier("ii"), Name.variable("1"))
//                .map(Name.identifier("1"), Name.variable("2"))
//                .map(Name.identifier("2"), Name.variable("2")).build();
//        assertTrue(Surjective.getInstance().check(m0));
//        assertTrue(Surjective.getInstance().check(m1));
//        assertFalse(Surjective.getInstance().check(m2));
//        assertFalse(Surjective.getInstance().check(m3));
//        assertTrue(Surjective.getInstance().check(m4));
//    }
//
//
//    @Test
//    public void testFunction() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Function.getInstance().inputArity()).build();
//        GraphImpl single = new GraphBuilder("single").edge("1", "2", "3").build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M1", single, Function.getInstance().inputArity())
//                .map(Name.identifier("1"), Name.variable("0"))
//                .map(Name.identifier("2"), Name.variable("1"))
//                .map(Name.identifier("3"), Name.variable("2")).build();
//        GraphImpl list = new GraphBuilder("list")
//                .edge("A", "a1", "1")
//                .edge("A", "a2", "2")
//                .edge("A", "a3", "3")
//                .build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M2", list, Function.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("1"), Name.variable("2"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .map(Name.identifier("a1"), Name.variable("1"))
//                .map(Name.identifier("a2"), Name.variable("1"))
//                .map(Name.identifier("a3"), Name.variable("1"))
//                .build();
//        GraphImpl nul = new GraphBuilder("null")
//                .node("A")
//                .build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M3", nul, Function.getInstance().inputArity())
//                .map("A", "0")
//                .build();
//        assertTrue(Function.getInstance().check(m0));
//        assertTrue(Function.getInstance().check(m1));
//        assertFalse(Function.getInstance().check(m2));
//        assertTrue(Function.getInstance().check(m3));
//    }
//
//
//    @Test
//    public void testTotal() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Total.getInstance().inputArity()).build();
//        GraphImpl single = new GraphBuilder("single").edge("1", "2", "3").build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M1", single, Total.getInstance().inputArity())
//                .map(Name.identifier("1"), Name.variable("0"))
//                .map(Name.identifier("2"), Name.variable("1"))
//                .map(Name.identifier("3"), Name.variable("2")).build();
//        GraphImpl list = new GraphBuilder("list")
//                .edge("A", "a1", "1")
//                .edge("A", "a2", "2")
//                .edge("A", "a3", "3")
//                .build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M2", list, Total.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("1"), Name.variable("2"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .map(Name.identifier("a1"), Name.variable("1"))
//                .map(Name.identifier("a2"), Name.variable("1"))
//                .map(Name.identifier("a3"), Name.variable("1"))
//                .build();
//        GraphImpl nul = new GraphBuilder("null")
//                .node("A")
//                .build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M3", nul, Total.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .build();
//        assertTrue(Total.getInstance().check(m0));
//        assertTrue(Total.getInstance().check(m1));
//        assertTrue(Total.getInstance().check(m2));
//        assertFalse(Total.getInstance().check(m3));
//    }
//
//    @Test
//    public void testAcyclic() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Acyclicity.getInstance().inputArity()).build();
//        GraphImpl fine = new GraphBuilder("G")
//                .edge("A", "f","B")
//                .edge("A", "g", "C").build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", fine, Acyclicity.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("f"), Name.variable("1"))
//                .map(Name.identifier("g"), Name.variable("1"))
//                .build();
//        GraphImpl loop = new GraphBuilder("G")
//                .edge("A", "f","A").build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", loop, Acyclicity.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("f"), Name.variable("1"))
//                .build();
//        GraphImpl inverse = new GraphBuilder("G")
//                .edge("A", "f","B")
//                .edge("B", "g", "A").build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M0", inverse, Acyclicity.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("f"), Name.variable("1"))
//                .map(Name.identifier("g"), Name.variable("1"))
//                .build();
//        GraphImpl bigLoop = new GraphBuilder("G")
//                .edge("A", "f","B")
//                .edge("B", "g", "C")
//                .edge("C", "h", "A")
//                .build();
//        GraphMorphismImpl m4 = new GraphMorphismImpl.Builder("M0", bigLoop, Acyclicity.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("f"), Name.variable("1"))
//                .map(Name.identifier("g"), Name.variable("1"))
//                .map(Name.identifier("h"), Name.variable("1"))
//                .build();
//        assertTrue(Acyclicity.getInstance().check(m0));
//        assertTrue(Acyclicity.getInstance().check(m1));
//        assertFalse(Acyclicity.getInstance().check(m2));
//        assertFalse(Acyclicity.getInstance().check(m3));
//        assertFalse(Acyclicity.getInstance().check(m4));
//    }
//
//    @Test
//    public void testCommutes() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Commutes.getInstance().inputArity()).build();
//        GraphImpl simpleEqual = new GraphBuilder("S1")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(1))
//                .build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", simpleEqual, Commutes.getInstance().inputArity())
//                .map(Name.identifier("1"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .build();
//        GraphImpl simpleNEqual = new GraphBuilder("S2")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(2))
//                .build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", simpleNEqual, Commutes.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(2), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .build();
//        GraphImpl complex = new GraphBuilder("G")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(1))
//                .edge(Name.identifier("B"), Name.identifier("3"), Name.value(1))
//                .edge(Name.identifier("B"), Name.identifier("4"), Name.value(2))
//                .edge(Name.identifier("B"), Name.identifier("5"), Name.value(1))
//                .edge(Name.identifier("B"), Name.identifier("6"), Name.value(2))
//                .node(Name.identifier("C"))
//                .node(Name.value(3))
//                .build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M3", complex, Commutes.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(2), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .map(Name.identifier("3"), Name.variable("1"))
//                .map(Name.identifier("4"), Name.variable("1"))
//                .map(Name.identifier("5"), Name.variable("2"))
//                .map(Name.identifier("6"), Name.variable("2"))
//                .build();
//        GraphImpl inclusion = new GraphBuilder("G")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("3"), Name.value(3))
//                .build();
//        GraphMorphismImpl m4 = new GraphMorphismImpl.Builder("M3", inclusion, Commutes.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(3), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .build();
//        GraphImpl mismatch = new GraphBuilder("G")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(2))
//                .edge(Name.identifier("A"), Name.identifier("3"), Name.value(2))
//                .edge(Name.identifier("A"), Name.identifier("4"), Name.value(3))
//                .build();
//        GraphMorphismImpl m5 = new GraphMorphismImpl.Builder("M3", mismatch, Commutes.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(3), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("1"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .map(Name.identifier("4"), Name.variable("2"))
//                .build();
//        assertTrue(Commutes.getInstance().check(m0));
//        assertTrue(Commutes.getInstance().check(m1));
//        assertFalse(Commutes.getInstance().check(m2));
//        assertTrue(Commutes.getInstance().check(m3));
//        assertFalse(Commutes.getInstance().check(m4));
//        assertFalse(Commutes.getInstance().check(m5));
//    }
//
//    @Test
//    public void testInclusion() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Inclusion.getInstance().inputArity()).build();
//        GraphImpl simpleEqual = new GraphBuilder("S1")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(1))
//                .build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M0", simpleEqual, Inclusion.getInstance().inputArity())
//                .map(Name.identifier("1"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .build();
//        GraphImpl simpleNEqual = new GraphBuilder("S2")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(2))
//                .build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M0", simpleNEqual, Inclusion.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(2), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .build();
//        GraphImpl complex = new GraphBuilder("G")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(1))
//                .edge(Name.identifier("B"), Name.identifier("3"), Name.value(1))
//                .edge(Name.identifier("B"), Name.identifier("4"), Name.value(2))
//                .edge(Name.identifier("B"), Name.identifier("5"), Name.value(1))
//                .edge(Name.identifier("B"), Name.identifier("6"), Name.value(2))
//                .node(Name.identifier("C"))
//                .node(Name.value(3))
//                .build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M3", complex, Inclusion.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(2), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .map(Name.identifier("3"), Name.variable("1"))
//                .map(Name.identifier("4"), Name.variable("1"))
//                .map(Name.identifier("5"), Name.variable("2"))
//                .map(Name.identifier("6"), Name.variable("2"))
//                .build();
//        GraphImpl inclusion = new GraphBuilder("G")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("3"), Name.value(3))
//                .build();
//        GraphMorphismImpl m4 = new GraphMorphismImpl.Builder("M3", inclusion, Inclusion.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(3), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .build();
//        GraphImpl mismatch = new GraphBuilder("G")
//                .edge(Name.identifier("A"), Name.identifier("1"), Name.value(1))
//                .edge(Name.identifier("A"), Name.identifier("2"), Name.value(2))
//                .edge(Name.identifier("A"), Name.identifier("3"), Name.value(2))
//                .edge(Name.identifier("A"), Name.identifier("4"), Name.value(3))
//                .build();
//        GraphMorphismImpl m5 = new GraphMorphismImpl.Builder("M3", mismatch, Inclusion.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(1), Name.variable("3"))
//                .map(Name.value(3), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("1"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .map(Name.identifier("4"), Name.variable("2"))
//                .build();
//        assertTrue(Inclusion.getInstance().check(m0));
//        assertTrue(Inclusion.getInstance().check(m1));
//        assertFalse(Inclusion.getInstance().check(m2));
//        assertTrue(Inclusion.getInstance().check(m3));
//        assertTrue(Inclusion.getInstance().check(m4));
//        assertFalse(Inclusion.getInstance().check(m5));
//    }
//
//    @Test
//    public void testInverse() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, Inverse.getInstance().inputArity()).build();
//        GraphImpl happy = new GraphBuilder("G")
//                .edge("A", "1", "B")
//                .edge("B", "2", "A")
//                .build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M1", happy, Inverse.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .build();
//        GraphImpl partialUndefined = new GraphBuilder("G")
//                .edge("A", "1", "B")
//                .edge("D", "2", "C")
//                .build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M1", partialUndefined, Inverse.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("3"))
//                .map(Name.identifier("D"), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .build();
//        GraphImpl setsReverse = new GraphBuilder("G")
//                .edge("A", "1", "B")
//                .edge("C", "2", "B")
//                .edge("B", "3", "A")
//                .edge("B", "4", "C")
//                .build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M1", setsReverse, Inverse.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("1"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .build();
//        GraphImpl setsReversePartial = new GraphBuilder("G")
//                .edge("A", "1", "B")
//                .edge("B", "3", "A")
//                .edge("B", "4", "C")
//                .build();
//        GraphMorphismImpl m4 = new GraphMorphismImpl.Builder("M1", setsReversePartial, Inverse.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("1"))
//                .map(Name.identifier("3"), Name.variable("2"))
//                .build();
//        GraphImpl nok = new GraphBuilder("G")
//                .edge("A", "1", "B")
//                .edge("B", "2", "C")
//                .edge("C", "3", "D")
//                .edge("D", "4", "A")
//                .build();
//        GraphMorphismImpl m5 = new GraphMorphismImpl.Builder("M1", nok, Inverse.getInstance().inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("3"))
//                .map(Name.identifier("B"), Name.variable("3"))
//                .map(Name.identifier("1"), Name.variable("1"))
//                .map(Name.identifier("2"), Name.variable("2"))
//                .map(Name.identifier("3"), Name.variable("1"))
//                .map(Name.identifier("4"), Name.variable("2"))
//                .build();
//
//        assertTrue(Inverse.getInstance().check(m0));
//        assertTrue(Inverse.getInstance().check(m1));
//        assertTrue(Inverse.getInstance().check(m2));
//        assertFalse(Inverse.getInstance().check(m3));
//        assertTrue(Inverse.getInstance().check(m4));
//        assertFalse(Inverse.getInstance().check(m5));
//    }
//
//    @Test
//    public void testTargetMultiplicity() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphPredicate p1 = TargetMultiplicity.getInstance(0, 1);
//        GraphPredicate p2 = TargetMultiplicity.getInstance(1, 1);
//        GraphPredicate p3 = TargetMultiplicity.getInstance(1, -1);
//        GraphPredicate p4 = TargetMultiplicity.getInstance(0, -1);
//        GraphPredicate p5 = TargetMultiplicity.getInstance(3, 5);
//
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, p1.inputArity()).build();
//        GraphImpl oneToOne = new GraphBuilder("G1")
//                .edge("A","a","A'")
//                .edge("B","b","B'")
//                .build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M1", oneToOne, p1.inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("a"), Name.variable("1"))
//                .map(Name.identifier("A'"), Name.variable("2"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("b"), Name.variable("1"))
//                .map(Name.identifier("B'"), Name.variable("2"))
//                .build();
//        GraphImpl zeroToOne = new GraphBuilder("G1")
//                .node("A")
//                .node("A'")
//                .edge("B","b","B'")
//                .build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M2", zeroToOne, p1.inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("A'"), Name.variable("2"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("b"), Name.variable("1"))
//                .map(Name.identifier("B'"), Name.variable("2"))
//                .build();
//        GraphImpl oneToMany = new GraphBuilder("G1")
//                .edge("A","a","A'")
//                .edge("A","a'","B'")
//                .edge("B","b","B'")
//                .edge("B","b'","A'")
//                .build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M3", oneToMany, p1.inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("a"), Name.variable("1"))
//                .map(Name.identifier("a'"), Name.variable("1"))
//                .map(Name.identifier("A'"), Name.variable("2"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("b"), Name.variable("1"))
//                .map(Name.identifier("b'"), Name.variable("1"))
//                .map(Name.identifier("B'"), Name.variable("2"))
//                .build();
//        GraphImpl longList = new GraphBuilder("G1")
//                .edge(Name.identifier("A"),Name.identifier("a1"),Name.value(1))
//                .edge(Name.identifier("A"),Name.identifier("a2"),Name.value(2))
//                .edge(Name.identifier("A"),Name.identifier("a3"),Name.value(3))
//                .edge(Name.identifier("A"),Name.identifier("a4"),Name.value(4))
//                .build();
//        GraphMorphismImpl m4 = new GraphMorphismImpl.Builder("M3", longList, p1.inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("a1"), Name.variable("1"))
//                .map(Name.identifier("a2"), Name.variable("1"))
//                .map(Name.identifier("a3"), Name.variable("1"))
//                .map(Name.identifier("a4"), Name.variable("1"))
//                .map(Name.value(1), Name.variable("2"))
//                .map(Name.value(2), Name.variable("2"))
//                .map(Name.value(3), Name.variable("2"))
//                .map(Name.value(4), Name.variable("2"))
//                .build();
//
//        assertTrue(p1.check(m0));
//        assertTrue(p1.check(m1));
//        assertTrue(p1.check(m2));
//        assertFalse(p1.check(m3));
//        assertFalse(p1.check(m4));
//
//        assertTrue(p2.check(m0));
//        assertTrue(p2.check(m1));
//        assertFalse(p2.check(m2));
//        assertFalse(p2.check(m3));
//        assertFalse(p2.check(m4));
//
//        assertTrue(p3.check(m0));
//        assertTrue(p3.check(m1));
//        assertFalse(p3.check(m2));
//        assertTrue(p3.check(m3));
//        assertTrue(p3.check(m4));
//
//        assertTrue(p4.check(m0));
//        assertTrue(p4.check(m1));
//        assertTrue(p4.check(m2));
//        assertTrue(p4.check(m3));
//        assertTrue(p4.check(m4));
//
//        assertTrue(p5.check(m0));
//        assertFalse(p5.check(m1));
//        assertFalse(p5.check(m2));
//        assertFalse(p5.check(m3));
//        assertTrue(p5.check(m4));
//    }
//
//    @Test
//    public void testSourceMultiplicity() {
//        GraphImpl empty = new GraphBuilder("empty").build();
//        GraphPredicate p1 = SourceMultiplicity.getInstance(0, 1);
//        GraphPredicate p2 = SourceMultiplicity.getInstance(1, 1);
//        GraphPredicate p3 = SourceMultiplicity.getInstance(1, -1);
//        GraphPredicate p4 = SourceMultiplicity.getInstance(0, -1);
//        GraphPredicate p5 = SourceMultiplicity.getInstance(3, 5);
//
//        GraphMorphismImpl m0 = new GraphMorphismImpl.Builder("M0", empty, p1.inputArity()).build();
//        GraphImpl oneToOne = new GraphBuilder("G1")
//                .edge("A","a","A'")
//                .edge("B","b","B'")
//                .build();
//        GraphMorphismImpl m1 = new GraphMorphismImpl.Builder("M1", oneToOne, p1.inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("a"), Name.variable("1"))
//                .map(Name.identifier("A'"), Name.variable("2"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("b"), Name.variable("1"))
//                .map(Name.identifier("B'"), Name.variable("2"))
//                .build();
//        GraphImpl zeroToOne = new GraphBuilder("G1")
//                .node("A")
//                .node("A'")
//                .edge("B","b","B'")
//                .build();
//        GraphMorphismImpl m2 = new GraphMorphismImpl.Builder("M2", zeroToOne, p1.inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("A'"), Name.variable("2"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("b"), Name.variable("1"))
//                .map(Name.identifier("B'"), Name.variable("2"))
//                .build();
//        GraphImpl manyToOne = new GraphBuilder("G1")
//                .edge("A","a","A'")
//                .edge("B","b","A'")
//                .edge("A","a'","B''")
//                .edge("B","b'","B'")
//                .build();
//        GraphMorphismImpl m3 = new GraphMorphismImpl.Builder("M3", manyToOne, p1.inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("a"), Name.variable("1"))
//                .map(Name.identifier("a'"), Name.variable("1"))
//                .map(Name.identifier("A'"), Name.variable("2"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("b"), Name.variable("1"))
//                .map(Name.identifier("b'"), Name.variable("1"))
//                .map(Name.identifier("B'"), Name.variable("2"))
//                .build();
//        GraphImpl longList = new GraphBuilder("G1")
//                .edge(Name.identifier("A"),Name.identifier("a1"),Name.value(1))
//                .edge(Name.identifier("B"),Name.identifier("a2"),Name.value(1))
//                .edge(Name.identifier("C"),Name.identifier("a3"),Name.value(1))
//                .edge(Name.identifier("D"),Name.identifier("a4"),Name.value(1))
//                .build();
//        GraphMorphismImpl m4 = new GraphMorphismImpl.Builder("M3", longList, p1.inputArity())
//                .map(Name.identifier("A"), Name.variable("0"))
//                .map(Name.identifier("B"), Name.variable("0"))
//                .map(Name.identifier("C"), Name.variable("0"))
//                .map(Name.identifier("D"), Name.variable("0"))
//                .map(Name.identifier("a1"), Name.variable("1"))
//                .map(Name.identifier("a2"), Name.variable("1"))
//                .map(Name.identifier("a3"), Name.variable("1"))
//                .map(Name.identifier("a4"), Name.variable("1"))
//                .map(Name.value(1), Name.variable("2"))
//                .build();
//
//        assertTrue(p1.check(m0));
//        assertTrue(p1.check(m1));
//        assertTrue(p1.check(m2));
//        assertFalse(p1.check(m3));
//        assertFalse(p1.check(m4));
//
//        assertTrue(p2.check(m0));
//        assertTrue(p2.check(m1));
//        assertFalse(p2.check(m2));
//        assertFalse(p2.check(m3));
//        assertFalse(p2.check(m4));
//
//        assertTrue(p3.check(m0));
//        assertTrue(p3.check(m1));
//        assertFalse(p3.check(m2));
//        assertTrue(p3.check(m3));
//        assertTrue(p3.check(m4));
//
//        assertTrue(p4.check(m0));
//        assertTrue(p4.check(m1));
//        assertTrue(p4.check(m2));
//        assertTrue(p4.check(m3));
//        assertTrue(p4.check(m4));
//
//        assertTrue(p5.check(m0));
//        assertFalse(p5.check(m1));
//        assertFalse(p5.check(m2));
//        assertFalse(p5.check(m3));
//        assertTrue(p5.check(m4));
//    }

}
