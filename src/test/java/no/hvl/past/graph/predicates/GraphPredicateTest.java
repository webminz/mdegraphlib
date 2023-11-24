package no.hvl.past.graph.predicates;


import no.hvl.past.attributes.BuiltinOperations;
import no.hvl.past.attributes.DataOperation;
import no.hvl.past.graph.*;
import no.hvl.past.names.Identifier;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GraphPredicateTest extends TestWithGraphLib {


    // one node predicates

    @Test
    public void testSingleton() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ONE_NODE).morphism("M0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder().node("A").graph("oneA").codomain(Universe.ONE_NODE).map(Name.identifier("A"), Universe.ONE_NODE_THE_NODE).morphism("M1").getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder().node("B").node("C").graph("ABC").codomain(Universe.ONE_NODE)
                .map(Name.identifier("A"), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("B"), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("C"), Universe.ONE_NODE_THE_NODE)
                .morphism("M2")
                .getResult(GraphMorphism.class);
        assertFalse(Singleton.getInstance().isSatisfied(m0));
        assertTrue(Singleton.getInstance().isSatisfied(m1));
        assertFalse(Singleton.getInstance().isSatisfied(m2));
    }


    @Test
    public void testStringDT() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ONE_NODE).morphism("M0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .node(Name.value("Hei"))
                .graph("sval1")
                .codomain(Universe.ONE_NODE)
                .map(Name.value("Hei"), Universe.ONE_NODE_THE_NODE)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder()
                .node("Hei")
                .node(Name.value("Hei"))
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.value("Hei"), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("Hei"), Universe.ONE_NODE_THE_NODE)
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder()
                .node(Name.value(""))
                .node(Name.value("Hallo"))
                .node(Name.value("World!"))
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(""), Universe.ONE_NODE_THE_NODE)
                .map(Name.value("Hello"), Universe.ONE_NODE_THE_NODE)
                .map(Name.value("World!"), Universe.ONE_NODE_THE_NODE)
                .morphism("m3")
                .getResult(GraphMorphism.class);
        GraphMorphism m4 = getContextCreatingBuilder()
                .node(Name.value(""))
                .node(Name.value(23))
                .node(Name.value(3.14159))
                .node(Name.trueValue())
                .graph("sval3")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(""), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(23), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(3.14159), Universe.ONE_NODE_THE_NODE)
                .morphism("m4")
                .getResult(GraphMorphism.class);

        assertTrue(StringDT.getInstance().isSatisfied(m0));
        assertTrue(StringDT.getInstance().isSatisfied(m1));
        assertFalse(StringDT.getInstance().isSatisfied(m2));
        assertTrue(StringDT.getInstance().isSatisfied(m3));
        assertFalse(StringDT.getInstance().isSatisfied(m4));
    }


    @Test
    public void testIntDT() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ONE_NODE).morphism("M0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .node(Name.value(42))
                .graph("sval1")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(42), Universe.ONE_NODE_THE_NODE)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder()
                .node(Name.value(42))
                .node("42")
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(42), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("42"), Universe.ONE_NODE_THE_NODE)
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder()
                .node(Name.value(0))
                .node(Name.value(23))
                .node(Name.value(42))
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(0), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(23), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(42), Universe.ONE_NODE_THE_NODE)
                .morphism("m3")
                .getResult(GraphMorphism.class);
        GraphMorphism m4 = getContextCreatingBuilder()
                .node(Name.value(""))
                .node(Name.value(23))
                .node(Name.value(3.14159))
                .node(Name.trueValue())
                .graph("sval3")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(""), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(23), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(3.14159), Universe.ONE_NODE_THE_NODE)
                .morphism("m4")
                .getResult(GraphMorphism.class);

        assertTrue(IntDT.getInstance().isSatisfied(m0));
        assertTrue(IntDT.getInstance().isSatisfied(m1));
        assertFalse(IntDT.getInstance().isSatisfied(m2));
        assertTrue(IntDT.getInstance().isSatisfied(m3));
        assertFalse(IntDT.getInstance().isSatisfied(m4));
    }


    @Test
    public void testFloatDT() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ONE_NODE).morphism("M0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .node(Name.value(3.159))
                .graph("sval1")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(3.159), Universe.ONE_NODE_THE_NODE)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder()
                .node(Name.value(3.159))
                .node("3.159")
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(3.159), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("3.159"), Universe.ONE_NODE_THE_NODE)
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder()
                .node(Name.value(1.0))
                .node(Name.value(3.159))
                .node(Name.value(42))
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(1.0), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(3.159), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(42), Universe.ONE_NODE_THE_NODE)
                .morphism("m3")
                .getResult(GraphMorphism.class);
        GraphMorphism m4 = getContextCreatingBuilder()
                .node(Name.value(""))
                .node(Name.value(23))
                .node(Name.value(3.14159))
                .node(Name.trueValue())
                .graph("sval3")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(""), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(23), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(3.14159), Universe.ONE_NODE_THE_NODE)
                .morphism("m4")
                .getResult(GraphMorphism.class);

        assertTrue(FloatDT.getInstance().isSatisfied(m0));
        assertTrue(FloatDT.getInstance().isSatisfied(m1));
        assertFalse(FloatDT.getInstance().isSatisfied(m2));
        assertTrue(FloatDT.getInstance().isSatisfied(m3));
        assertFalse(FloatDT.getInstance().isSatisfied(m4));
    }



    @Test
    public void testBoolDT() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ONE_NODE).morphism("M0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .node(Name.trueValue())
                .graph("sval1")
                .codomain(Universe.ONE_NODE)
                .map(Name.trueValue(), Universe.ONE_NODE_THE_NODE)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder()
                .node(Name.trueValue())
                .node("true")
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.trueValue(), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("true"), Universe.ONE_NODE_THE_NODE)
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder()
                .node(Name.trueValue())
                .node(Name.falseValue())
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.trueValue(), Universe.ONE_NODE_THE_NODE)
                .map(Name.falseValue(), Universe.ONE_NODE_THE_NODE)
                .morphism("m3")
                .getResult(GraphMorphism.class);
        GraphMorphism m4 = getContextCreatingBuilder()
                .node(Name.value(""))
                .node(Name.value(23))
                .node(Name.value(3.14159))
                .node(Name.trueValue())
                .graph("sval3")
                .codomain(Universe.ONE_NODE)
                .map(Name.value(""), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(23), Universe.ONE_NODE_THE_NODE)
                .map(Name.value(3.14159), Universe.ONE_NODE_THE_NODE)
                .morphism("m4")
                .getResult(GraphMorphism.class);

        assertTrue(BoolDT.getInstance().isSatisfied(m0));
        assertTrue(BoolDT.getInstance().isSatisfied(m1));
        assertFalse(BoolDT.getInstance().isSatisfied(m2));
        assertTrue(BoolDT.getInstance().isSatisfied(m3));
        assertFalse(BoolDT.getInstance().isSatisfied(m4));
    }


    @Test
    public void testRegex() throws GraphError {

        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ONE_NODE).morphism("M0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .node(Name.value("ab"))
                .graph("sval1")
                .codomain(Universe.ONE_NODE)
                .map(Name.value("ab"), Universe.ONE_NODE_THE_NODE)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder()
                .node(Name.value("bab"))
                .graph("sval2")
                .codomain(Universe.ONE_NODE)
                .map(Name.value("bab"), Universe.ONE_NODE_THE_NODE)
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder()
                .node(Name.value("ab"))
                .node("abbbbbbbb")
                .node(Name.variable("aabb"))
                .graph("sval3")
                .codomain(Universe.ONE_NODE)
                .map(Name.value("ab"), Universe.ONE_NODE_THE_NODE)
                .map(Name.identifier("abbbbbbbb"), Universe.ONE_NODE_THE_NODE)
                .map(Name.value("aabb"), Universe.ONE_NODE_THE_NODE)
                .morphism("m3")
                .getResult(GraphMorphism.class);

        Regex predicate = Regex.getInstance("aa?b+");

        assertTrue(predicate.isSatisfied(m0));
        assertTrue(predicate.isSatisfied(m1));
        assertFalse(predicate.isSatisfied(m2));
        assertTrue(predicate.isSatisfied(m3));

    }


    @Test
    public void testEnum() throws GraphError {
        Identifier val1 = Name.identifier("EClass");
        Identifier val2 = Name.identifier("EReference");
        Identifier val3 = Name.identifier("EDataType");
        Identifier val4 = Name.identifier("EAttribute");
        GraphPredicate pred = EnumValue.getInstance(val1, val2, val3, val4);
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ONE_NODE).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .node(val1)
                .graph("G1")
                .codomain(Universe.ONE_NODE)
                .map(val1, Universe.ONE_NODE_THE_NODE)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder()
                .node(val1)
                .node(val3)
                .graph("G2")
                .codomain(Universe.ONE_NODE)
                .map(val1, Universe.ONE_NODE_THE_NODE)
                .map(val3, Universe.ONE_NODE_THE_NODE)
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder()
                .node(Name.identifier("EEnum"))
                .node(val3)
                .graph("G3")
                .codomain(Universe.ONE_NODE)
                .map(Name.identifier("EEnum"), Universe.ONE_NODE_THE_NODE)
                .map(val3, Universe.ONE_NODE_THE_NODE)
                .morphism("m3")
                .getResult(GraphMorphism.class);

        assertTrue(pred.isSatisfied(m0));
        assertTrue(pred.isSatisfied(m1));
        assertTrue(pred.isSatisfied(m2));
        assertFalse(pred.isSatisfied(m3));
    }


    // one arrow predicate


    @Test
    public void testAttributePredicate() throws GraphError {
        DataOperation betwen23And42 = new DataOperation() {
            @Override
            public String name() {
                return "23 <= x <= 42";
            }

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Value applyImplementation(Value[] arguments) {
                Value[] left = new Value[2];
                left[0] = Name.value(23);
                left[1] = arguments[0];
                Value[] right = new Value[2];
                right[0] = arguments[0];
                right[1] = Name.value(42);
                Value resultLeft = BuiltinOperations.LessOrEqual.getInstance().apply(left);
                Value resultRight = BuiltinOperations.LessOrEqual.getInstance().apply(right);
                Value[] finalArgs = new Value[2];
                finalArgs[0] = resultLeft;
                finalArgs[1] = resultRight;
                return BuiltinOperations.Conjunction.getInstance().apply(finalArgs);
            }

        };
        GraphPredicate predicate = AttributePredicate.getInstance(betwen23And42);
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("M0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .edge(Name.identifier("A"), Name.identifier("a"), Name.value(27))
                .node(Name.value(13))
                .graph("sval1")
                .codomain(Universe.ARROW)
                .map(Name.value(27), Universe.ARROW_TRG_NAME)
                .map(Name.value(13), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("a"), Universe.ARROW_LBL_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism m2 = getContextCreatingBuilder()
                .edge(Name.identifier("A"), Name.identifier("a1"),Name.value(27))
                .edge(Name.identifier("A"), Name.identifier("a2"),Name.value(13))
                .graph("sval2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("a1"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a2"), Universe.ARROW_LBL_NAME)
                .map(Name.value(27), Universe.ARROW_TRG_NAME)
                .map(Name.value(13), Universe.ARROW_TRG_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertTrue(predicate.isSatisfied(m0));
        assertTrue(predicate.isSatisfied(m1));
        assertFalse(predicate.isSatisfied(m2));

    }

    @Test
    public void testInjective() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder().edge("A", "f", "B") // bijective
                .graph("G0")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder() // neither inj nor surj
                .edge("A", "f", "B")
                .edge("A'", "f'", "B")
                .node("B'")
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f'"), Universe.ARROW_LBL_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder() //  inj but not total or surj
                .edge("A", "f", "B")
                .node("B'")
                .node("A'")
                .graph("G2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .morphism("m3")
                .getResult(GraphMorphism.class);
        GraphMorphism m4 = getContextCreatingBuilder() //   surj but not inj
                .edge("A", "f", "B")
                .edge("A'", "f'", "B'")
                .edge("A''", "f''", "B'")
                .graph("G3")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A''"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f'"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f''"), Universe.ARROW_LBL_NAME)
                .morphism("m4")
                .getResult(GraphMorphism.class);



        assertTrue(Injective.getInstance().isSatisfied(m0));
        assertTrue(Injective.getInstance().isSatisfied(m1));
        assertFalse(Injective.getInstance().isSatisfied(m2));
        assertTrue(Injective.getInstance().isSatisfied(m3));
        assertFalse(Injective.getInstance().isSatisfied(m4));
    }



    @Test
    public void testSurjective() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder().edge("A", "f", "B") // bijective
                .graph("G0")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder() // neither inj nor surj
                .edge("A", "f", "B")
                .edge("A'", "f'", "B")
                .node("B'")
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f'"), Universe.ARROW_LBL_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder() //  inj but not total or surj
                .edge("A", "f", "B")
                .node("B'")
                .node("A'")
                .graph("G2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .morphism("m3")
                .getResult(GraphMorphism.class);
        GraphMorphism m4 = getContextCreatingBuilder() //   surj but not inj
                .edge("A", "f", "B")
                .edge("A'", "f'", "B'")
                .edge("A''", "f''", "B'")
                .graph("G3")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A''"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f'"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f''"), Universe.ARROW_LBL_NAME)
                .morphism("m4")
                .getResult(GraphMorphism.class);



        assertTrue(Surjective.getInstance().isSatisfied(m0));
        assertTrue(Surjective.getInstance().isSatisfied(m1));
        assertFalse(Surjective.getInstance().isSatisfied(m2));
        assertFalse(Surjective.getInstance().isSatisfied(m3));
        assertTrue(Surjective.getInstance().isSatisfied(m4));
    }


    @Test
    public void testFunction() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder().edge("A", "f", "B") // bijective
                .graph("G0")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder() //  inj but not total or surj
                .edge("A", "f", "B")
                .node("B'")
                .node("A'")
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);

        GraphMorphism m3 = getContextCreatingBuilder() //  not a function
                .edge("A", "f", "B")
                .edge("A", "f'", "B'")
                .graph("G2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f'"), Universe.ARROW_LBL_NAME)
                .morphism("m3")
                .getResult(GraphMorphism.class);


        assertTrue(Function.getInstance().isSatisfied(m0));
        assertTrue(Function.getInstance().isSatisfied(m1));
        assertTrue(Function.getInstance().isSatisfied(m2));
        assertFalse(Function.getInstance().isSatisfied(m3));
    }

    @Test
    public void testTotal() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder().edge("A", "f", "B") // bijective
                .graph("G0")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder() //  inj but not total or surj
                .edge("A", "f", "B")
                .node("B'")
                .node("A'")
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);

        GraphMorphism m3 = getContextCreatingBuilder() //  not a function
                .edge("A", "f", "B")
                .edge("A", "f'", "B'")
                .graph("G2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f'"), Universe.ARROW_LBL_NAME)
                .morphism("m3")
                .getResult(GraphMorphism.class);

        GraphMorphism m4 = getContextCreatingBuilder() // neither inj nor surj
                .edge("A", "f", "B")
                .edge("A'", "f'", "B")
                .node("B'")
                .graph("G4")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f'"), Universe.ARROW_LBL_NAME)
                .morphism("m4")
                .getResult(GraphMorphism.class);


        assertTrue(Total.getInstance().isSatisfied(m0));
        assertTrue(Total.getInstance().isSatisfied(m1));
        assertFalse(Total.getInstance().isSatisfied(m2));
        assertTrue(Total.getInstance().isSatisfied(m3));
        assertTrue(Total.getInstance().isSatisfied(m4));
    }


    @Test
    public void testTargetMultiplicity() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);
        GraphPredicate p1 = TargetMultiplicity.getInstance(0, 1);
        GraphPredicate p2 = TargetMultiplicity.getInstance(1, 1);
        GraphPredicate p3 = TargetMultiplicity.getInstance(1, -1);
        GraphPredicate p4 = TargetMultiplicity.getInstance(0, -1);
        GraphPredicate p5 = TargetMultiplicity.getInstance(3, 5);

        GraphMorphism m1 = getContextCreatingBuilder()
                .edge("A","a","A'")
                .edge("B","b","B'")
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("a"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("b"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism m2 = getContextCreatingBuilder()
                .node("A")
                .node("A'")
                .edge("B", "b", "B'")
                .graph("G2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("b"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);

        GraphMorphism m3 = getContextCreatingBuilder()
                .edge("A", "a", "A'")
                .edge("A", "a'", "B'")
                .edge("B", "b", "B'")
                .edge("B", "b'", "A'")
                .graph("G3")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("a"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a'"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("b"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("b'"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .morphism("m3")
                .getResult(GraphMorphism.class);

        GraphMorphism m4 = getContextCreatingBuilder()
                .edge(Name.identifier("A"), Name.identifier("a1"), Name.value(1))
                .edge(Name.identifier("A"), Name.identifier("a2"), Name.value(2))
                .edge(Name.identifier("A"), Name.identifier("a3"), Name.value(3))
                .edge(Name.identifier("A"), Name.identifier("a4"), Name.value(4))
                .graph("G4")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("a1"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a2"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a3"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a4"), Universe.ARROW_LBL_NAME)
                .map(Name.value(1), Universe.ARROW_TRG_NAME)
                .map(Name.value(2), Universe.ARROW_TRG_NAME)
                .map(Name.value(3), Universe.ARROW_TRG_NAME)
                .map(Name.value(4), Universe.ARROW_TRG_NAME)
                .morphism("m4")
                .getResult(GraphMorphism.class);

        assertTrue(p1.check(m0));
        assertTrue(p1.check(m1));
        assertTrue(p1.check(m2));
        assertFalse(p1.check(m3));
        assertFalse(p1.check(m4));

        assertTrue(p2.check(m0));
        assertTrue(p2.check(m1));
        assertFalse(p2.check(m2));
        assertFalse(p2.check(m3));
        assertFalse(p2.check(m4));

        assertTrue(p3.check(m0));
        assertTrue(p3.check(m1));
        assertFalse(p3.check(m2));
        assertTrue(p3.check(m3));
        assertTrue(p3.check(m4));

        assertTrue(p4.check(m0));
        assertTrue(p4.check(m1));
        assertTrue(p4.check(m2));
        assertTrue(p4.check(m3));
        assertTrue(p4.check(m4));

        assertTrue(p5.check(m0));
        assertFalse(p5.check(m1));
        assertFalse(p5.check(m2));
        assertFalse(p5.check(m3));
        assertTrue(p5.check(m4));
    }

    @Test
    public void testSourceMultiplicity() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);
        GraphPredicate p1 = SourceMultiplicity.getInstance(0, 1);
        GraphPredicate p2 = SourceMultiplicity.getInstance(1, 1);
        GraphPredicate p3 = SourceMultiplicity.getInstance(1, -1);
        GraphPredicate p4 = SourceMultiplicity.getInstance(0, -1);
        GraphPredicate p5 = SourceMultiplicity.getInstance(3, 5);

        GraphMorphism m1 = getContextCreatingBuilder()
                .edge("A", "a", "A'")
                .edge("B", "b", "B'")
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("a"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("b"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism m2 = getContextCreatingBuilder()
                .node("A")
                .node("A'")
                .edge("B", "b", "B'")
                .graph("G2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("b"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);

        GraphMorphism m3 = getContextCreatingBuilder()
                .edge("A","a","A'")
                .edge("B","b","A'")
                .edge("A","a'","B''")
                .edge("B","b'","B'")
                .graph("G3")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("a"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a'"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("b"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("b'"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B'"), Universe.ARROW_TRG_NAME)
                .morphism("m3")
                .getResult(GraphMorphism.class);

        GraphMorphism m4 = getContextCreatingBuilder()
                .edge(Name.identifier("A"), Name.identifier("a1"), Name.value(1))
                .edge(Name.identifier("B"), Name.identifier("a2"), Name.value(1))
                .edge(Name.identifier("C"), Name.identifier("a3"), Name.value(1))
                .edge(Name.identifier("D"), Name.identifier("a4"), Name.value(1))
                .graph("G4")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"),  Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"),  Universe.ARROW_SRC_NAME)
                .map(Name.identifier("C"),  Universe.ARROW_SRC_NAME)
                .map(Name.identifier("D"),  Universe.ARROW_SRC_NAME)
                .map(Name.identifier("a1"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a2"),Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a3"),Universe.ARROW_LBL_NAME)
                .map(Name.identifier("a4"), Universe.ARROW_LBL_NAME)
                .map(Name.value(1), Universe.ARROW_TRG_NAME)
                .morphism("m4")
                .getResult(GraphMorphism.class);


        assertTrue(p1.check(m0));
        assertTrue(p1.check(m1));
        assertTrue(p1.check(m2));
        assertFalse(p1.check(m3));
        assertFalse(p1.check(m4));

        assertTrue(p2.check(m0));
        assertTrue(p2.check(m1));
        assertFalse(p2.check(m2));
        assertFalse(p2.check(m3));
        assertFalse(p2.check(m4));

        assertTrue(p3.check(m0));
        assertTrue(p3.check(m1));
        assertFalse(p3.check(m2));
        assertTrue(p3.check(m3));
        assertTrue(p3.check(m4));

        assertTrue(p4.check(m0));
        assertTrue(p4.check(m1));
        assertTrue(p4.check(m2));
        assertTrue(p4.check(m3));
        assertTrue(p4.check(m4));

        assertTrue(p5.check(m0));
        assertFalse(p5.check(m1));
        assertFalse(p5.check(m2));
        assertFalse(p5.check(m3));
        assertTrue(p5.check(m4));
    }


    @Test
    public void testOrdered() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .node("A")
                .edge("A'", "f", "B")
                .edge(Name.identifier("A''"), Name.identifier("f").index(0), Name.identifier("B"))
                .edge(Name.identifier("A''"), Name.identifier("f").index(1), Name.identifier("B"))
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A''"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f").index(0), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f").index(1), Universe.ARROW_LBL_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder()
                .edge(Name.identifier("A"), Name.identifier("f"), Name.identifier("B1"))
                .edge(Name.identifier("A"), Name.identifier("g"), Name.identifier("B2"))
                .graph("G2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B1"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B2"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B3"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("g"), Universe.ARROW_LBL_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);


        assertTrue(Ordered.getInstance().isSatisfied(m0));
        assertTrue(Ordered.getInstance().isSatisfied(m1));
        assertFalse(Ordered.getInstance().isSatisfied(m2));
    }


    @Test
    public void testUnique() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder()
                .node("A")
                .edge("A'", "f", "B")
                .edge(Name.identifier("A''"), Name.identifier("f").index(0), Name.identifier("B"))
                .edge(Name.identifier("A''"), Name.identifier("f").index(1), Name.identifier("B"))
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A'"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("A''"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f").index(0), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("f").index(1), Universe.ARROW_LBL_NAME)
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder()
                .edge(Name.identifier("A"), Name.identifier("f"), Name.identifier("B1"))
                .edge(Name.identifier("A"), Name.identifier("g"), Name.identifier("B2"))
                .graph("G2")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B1"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B2"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B3"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("g"), Universe.ARROW_LBL_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);


        assertTrue(Unique.getInstance().isSatisfied(m0));
        assertFalse(Unique.getInstance().isSatisfied(m1));
        assertTrue(Unique.getInstance().isSatisfied(m2));
    }


    // Loop predicates

    @Test
    public void testAcyclicity() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.LOOP).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder().edge("A", "f", "B") // valid
                .graph("G1")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("f"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getTarget())
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder().edge("A", "f", "A") // obiously invalid
                .graph("G2")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("f"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder() // DAG
                .edge("A", "l1", "B")
                .edge("A", "r1", "C")
                .edge("B", "l2", "D")
                .edge("B", "r2", "E")
                .edge("C", "l3", "F")
                .edge("C", "r3", "G")
                .graph("G3")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("C"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("D"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("E"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("F"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("G"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("l1"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("l2"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("l3"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r1"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r2"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r3"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m3")
                .getResult(GraphMorphism.class);
        GraphMorphism m4 = getContextCreatingBuilder() // the same as above but there is an additionale edge creating a loop
                .edge("A", "l1", "B")
                .edge("A", "r1", "C")
                .edge("B", "l2", "D")
                .edge("B", "r2", "E")
                .edge("C", "l3", "F")
                .edge("C", "r3", "G")
                .edge("G", "evil", "A")
                .graph("G4")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("C"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("D"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("E"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("F"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("G"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("l1"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("l2"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("l3"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r1"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r2"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r3"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("evil"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m4")
                .getResult(GraphMorphism.class);

        assertTrue(Acyclicity.getInstance().isSatisfied(m0));
        assertTrue(Acyclicity.getInstance().isSatisfied(m1));
        assertFalse(Acyclicity.getInstance().isSatisfied(m2));
        assertTrue(Acyclicity.getInstance().isSatisfied(m3));
        assertFalse(Acyclicity.getInstance().isSatisfied(m4));
    }


    @Test
    public void Irreflexivity() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.LOOP).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder().edge("A", "f", "B") // valid
                .graph("G1")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("f"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getTarget())
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder().edge("A", "f", "A") // obiously invalid
                .graph("G2")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("f"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder() // DAG
                .edge("A", "l1", "B")
                .edge("A", "r1", "C")
                .edge("B", "l2", "D")
                .edge("B", "r2", "E")
                .edge("C", "l3", "F")
                .edge("C", "r3", "G")
                .graph("G3")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("C"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("D"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("E"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("F"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("G"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("l1"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("l2"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("l3"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r1"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r2"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r3"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m3")
                .getResult(GraphMorphism.class);
        GraphMorphism m4 = getContextCreatingBuilder() // the same as above but there is an additionale edge creating a loop
                .edge("A", "l1", "B")
                .edge("A", "r1", "C")
                .edge("B", "l2", "D")
                .edge("B", "r2", "E")
                .edge("C", "l3", "F")
                .edge("C", "r3", "G")
                .edge("G", "evil", "A")
                .graph("G4")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("C"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("D"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("E"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("F"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("G"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("l1"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("l2"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("l3"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r1"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r2"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("r3"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("evil"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m4")
                .getResult(GraphMorphism.class);

        assertTrue(Irreflexive.getInstance().isSatisfied(m0));
        assertTrue(Irreflexive.getInstance().isSatisfied(m1));
        assertFalse(Irreflexive.getInstance().isSatisfied(m2));
        assertTrue(Irreflexive.getInstance().isSatisfied(m3));
        assertTrue(Irreflexive.getInstance().isSatisfied(m4));
    }

    // Parallel arrows predicates

    @Test
    public void testCommutativity() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.CELL).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder() // oviously fulfilled
                .edge("A", "f", "B")
                .edge("A", "g", "B")
                .graph("G1")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_RHS.getLabel())
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder() // not fulfilled
                .edge("A", "f", "B")
                .edge("A", "g", "B'")
                .edge("A'", "f'", "B'")
                .edge("A'", "g'", "B'")
                .graph("G1")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("A'"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("B'"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("f'"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("g'"), Universe.CELL_RHS.getLabel())
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder() // multi-valued but fulfilled
                .edge("A", "f", "B")
                .edge("A", "f'", "B'")
                .edge("A", "g", "B")
                .edge("A", "g'", "B'")
                .edge("A'", "f''", "B'")
                .edge("A'", "g''", "B'")
                .node("A''")
                .graph("G3")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("A'"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("A''"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("B'"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("f'"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("f''"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("g'"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("g''"), Universe.CELL_RHS.getLabel())
                .morphism("m3")
                .getResult(GraphMorphism.class);

        GraphMorphism m4 = getContextCreatingBuilder() // inclusion
                .edge("A", "f", "B")
                .edge("A", "f'", "B'")
                .edge("A", "g", "B")
                .node("A''")
                .graph("G3")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("B'"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("f'"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_RHS.getLabel())
                .morphism("m3")
                .getResult(GraphMorphism.class);

        assertTrue(Commutativity.getInstance().isSatisfied(m0));
        assertTrue(Commutativity.getInstance().isSatisfied(m1));
        assertFalse(Commutativity.getInstance().isSatisfied(m2));
        assertTrue(Commutativity.getInstance().isSatisfied(m3));
        assertFalse(Commutativity.getInstance().isSatisfied(m4));

    }


    @Test
    public void testInclusion() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.CELL).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder() // oviously fulfilled
                .edge("A", "f", "B")
                .edge("A", "g", "B")
                .graph("G1")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_RHS.getLabel())
                .morphism("m1")
                .getResult(GraphMorphism.class);
        GraphMorphism m2 = getContextCreatingBuilder() // not fulfilled
                .edge("A", "f", "B")
                .edge("A", "g", "B'")
                .edge("A'", "f'", "B'")
                .edge("A'", "g'", "B'")
                .graph("G1")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("A'"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("B'"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("f'"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("g'"), Universe.CELL_RHS.getLabel())
                .morphism("m2")
                .getResult(GraphMorphism.class);
        GraphMorphism m3 = getContextCreatingBuilder() // multi-valued but fulfilled
                .edge("A", "f", "B")
                .edge("A", "f'", "B'")
                .edge("A", "g", "B")
                .edge("A", "g'", "B'")
                .edge("A'", "f''", "B'")
                .edge("A'", "g''", "B'")
                .node("A''")
                .graph("G3")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("A'"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("A''"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("B'"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("f'"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("f''"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("g'"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("g''"), Universe.CELL_RHS.getLabel())
                .morphism("m3")
                .getResult(GraphMorphism.class);

        GraphMorphism m4 = getContextCreatingBuilder() // inclusion
                .edge("A", "f", "B")
                .edge("A", "f'", "B'")
                .edge("A", "g", "B")
                .node("A''")
                .graph("G3")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("B'"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("f'"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_LHS.getLabel())
                .morphism("m3")
                .getResult(GraphMorphism.class);

        GraphMorphism m5 = getContextCreatingBuilder() // inclusion
                .edge("A", "f", "B")
                .graph("G5")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_RHS.getLabel())
                .morphism("m5")
                .getResult(GraphMorphism.class);

        GraphMorphism m6 = getContextCreatingBuilder() // inclusion
                .edge("A", "f", "B")
                .graph("G6")
                .codomain(Universe.CELL)
                .map(Name.identifier("A"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("B"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .morphism("m6")
                .getResult(GraphMorphism.class);

        // basically same as for commutativity but now also m4 should be fulfilled.
        assertTrue(Inclusion.getInstance().isSatisfied(m0));
        assertTrue(Inclusion.getInstance().isSatisfied(m1));
        assertFalse(Inclusion.getInstance().isSatisfied(m2));
        assertTrue(Inclusion.getInstance().isSatisfied(m3));
        assertTrue(Inclusion.getInstance().isSatisfied(m4));
        assertTrue(Inclusion.getInstance().isSatisfied(m5));
        assertFalse(Inclusion.getInstance().isSatisfied(m6));

    }





}
