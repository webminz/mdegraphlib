package no.hvl.past.attributes;

import no.hvl.past.attributes.*;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class AttributesTest {

    @Test
    public void testErrorHandling() {
        Value zero = Name.value(0);
        Value number = Name.value(23);
        Value string = Name.value("hello");
        Value bool = Name.trueValue();
        Value[] divByZero = new Value[]{number, zero};
        Value[] numberString = new Value[]{number, string};
        Value[] stringBool = new Value[]{string, bool};
        assertEquals(ErrorValue.INSTANCE, BuiltinOperations.Division.getInstance().apply(divByZero));
        assertEquals(ErrorValue.INSTANCE, BuiltinOperations.Addition.getInstance().apply(numberString));
        assertEquals(ErrorValue.INSTANCE, BuiltinOperations.Addition.getInstance().apply(stringBool));
        assertEquals(ErrorValue.INSTANCE, BuiltinOperations.Addition.getInstance().apply(new Value[]{zero}));
    }

    @Test
    public void testInteger() {
        Value x = Name.value(3);
        Value y = Name.value(15);
        Value[] pairYX = new Value[2];
        pairYX[0] = y;
        pairYX[1] = x;
        assertEquals(Name.value(18), BuiltinOperations.Addition.getInstance().apply(pairYX));
        assertEquals(Name.value(12), BuiltinOperations.Subtraction.getInstance().apply(pairYX));
        assertEquals(Name.value(5), BuiltinOperations.Division.getInstance().apply(pairYX));
        assertEquals(Name.value(45), BuiltinOperations.Multiplication.getInstance().apply(pairYX));
        assertEquals(Name.value(0), BuiltinOperations.Modulo.getInstance().apply(pairYX));
        pairYX[0] = Name.value(2);
        pairYX[1] = Name.value(3);
        assertEquals(Name.value(8), BuiltinOperations.Exponentiation.getInstance().apply(pairYX));
    }

    @Test
    public void testComparison() {
        IntegerValue v1 = Name.value(23);
        IntegerValue v2 = Name.value(42);
        FloatValue v3 = Name.value(3.1415926);
        FloatValue v4 = Name.value(8.5);
        StringValue v5 = Name.value("abba");
        StringValue v6 = Name.value("acdc");
        Value[] arguments = new Value[2];
        arguments[0] = v1;
        arguments[1] = v2;
        assertEquals(Name.trueValue(), BuiltinOperations.LessOrEqual.getInstance().apply(arguments)); // 23 <= 42
        arguments[0] = v2;
        assertEquals(Name.trueValue(), BuiltinOperations.LessOrEqual.getInstance().apply(arguments)); // 42 <= 42
        arguments[1] = v4;
        assertEquals(Name.falseValue(), BuiltinOperations.LessOrEqual.getInstance().apply(arguments)); // not 42 <= 8,5
        arguments[0] = v3;
        assertEquals(Name.trueValue(), BuiltinOperations.LessOrEqual.getInstance().apply(arguments)); // 3,1415926 <= 8,5
        arguments[0] = v5;
        arguments[1] = v6;
        assertEquals(Name.trueValue(), BuiltinOperations.LessOrEqual.getInstance().apply(arguments)); // abba <= acdc
        arguments[1] = v1;
        assertEquals(ErrorValue.INSTANCE, BuiltinOperations.LessOrEqual.getInstance().apply(arguments)); // not comparable
    }

    @Test
    public void testFloats() {
        FloatValue v1 = Name.value(0.1);
        FloatValue v2 = Name.value(10.0);
        Value[] arguments = new Value[2];

        arguments[0] = v2;
        arguments[1] = v1;
        assertEquals(Name.value(9.9), BuiltinOperations.Subtraction.getInstance().apply(arguments));
        assertEquals(Name.value(10.1), BuiltinOperations.Addition.getInstance().apply(arguments));
        assertEquals(Name.value(1.0), BuiltinOperations.Multiplication.getInstance().apply(arguments));
        assertEquals(Name.value(100.0), BuiltinOperations.Division.getInstance().apply(arguments));

        arguments[0] = Name.value(0.0);
        for (int i = 0; i < 10; i++) {
            Value res = BuiltinOperations.Addition.getInstance().apply(arguments);
            arguments[0] = res;
        }
        arguments[1] = Name.value(10.0);
        assertEquals(Name.falseValue(), BuiltinOperations.Equality.getInstance().applyImplementation(arguments));
        Value[] arguments2 = new Value[3];
        arguments2[0] = arguments[0];
        arguments2[1] = arguments[1];
        assertEquals(Name.trueValue(), BuiltinOperations.AlmostEqual.getInstance().applyImplementation(arguments2));
    }

    @Test
    public void testBooleans() {
        Value[] arguments = new Value[1];

        arguments[0] = Name.trueValue();
        assertEquals(Name.falseValue(), BuiltinOperations.Negation.getInstance().apply(arguments));
        arguments[0] = Name.falseValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Negation.getInstance().apply(arguments));

        arguments = new Value[2];

        arguments[0] = Name.falseValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.falseValue(), BuiltinOperations.Disjunction.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Disjunction.getInstance().applyImplementation(arguments));
        arguments[0] = Name.trueValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Disjunction.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Disjunction.getInstance().applyImplementation(arguments));


        arguments[0] = Name.falseValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.falseValue(), BuiltinOperations.Conjunction.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.falseValue(), BuiltinOperations.Conjunction.getInstance().applyImplementation(arguments));
        arguments[0] = Name.trueValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.falseValue(), BuiltinOperations.Conjunction.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Conjunction.getInstance().applyImplementation(arguments));


        arguments[0] = Name.falseValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Implication.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Implication.getInstance().applyImplementation(arguments));
        arguments[0] = Name.trueValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.falseValue(), BuiltinOperations.Implication.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Implication.getInstance().applyImplementation(arguments));


        arguments[0] = Name.falseValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.trueValue(), BuiltinOperations.BiImplication.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.falseValue(), BuiltinOperations.BiImplication.getInstance().applyImplementation(arguments));
        arguments[0] = Name.trueValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.falseValue(), BuiltinOperations.BiImplication.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.trueValue(), BuiltinOperations.BiImplication.getInstance().applyImplementation(arguments));


        arguments[0] = Name.falseValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.falseValue(), BuiltinOperations.Xor.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Xor.getInstance().applyImplementation(arguments));
        arguments[0] = Name.trueValue();
        arguments[1] = Name.falseValue();
        assertEquals(Name.trueValue(), BuiltinOperations.Xor.getInstance().applyImplementation(arguments));
        arguments[1] = Name.trueValue();
        assertEquals(Name.falseValue(), BuiltinOperations.Xor.getInstance().applyImplementation(arguments));

        arguments = new Value[3];
        arguments[0] = Name.trueValue();
        arguments[1] = Name.value(23);
        arguments[2] = Name.value("Hei");

        assertEquals(Name.value(23), BuiltinOperations.IfThenElse.getInstance().apply(arguments));
        arguments[0] = Name.falseValue();
        assertEquals(Name.value("Hei"), BuiltinOperations.IfThenElse.getInstance().apply(arguments));
    }

    @Test
    public void testStrings() {
        Value[] arguments = new Value[3];

        arguments[0] = Name.value("Optimist");
        arguments[1] = Name.value(4);
        arguments[2] = Name.value(4);
        assertEquals(Name.value("mist"), BuiltinOperations.Substring.getInstance().apply(arguments));

        arguments =new Value[2];
        arguments[0] = Name.value("Hello");
        arguments[1] = Name.value(", World!");
        assertEquals(Name.value("Hello, World!"), BuiltinOperations.Concatenation.getInstance().apply(arguments));


        arguments[0] = Name.value("The black brown fox jumped over the black brown house");
        arguments[1] = Name.value("brown");
        assertEquals(Name.value(10), BuiltinOperations.FirstIndexOf.getInstance().apply(arguments));

        arguments = new Value[1];
        arguments[0] = Name.value("Hallo");
        assertEquals(Name.value("ollaH"), BuiltinOperations.Reverse.getInstance().apply(arguments));

        assertEquals(Name.value(5), BuiltinOperations.Length.getInstance().apply(arguments));
    }

    @Test
    public void testParsingAndToString() {

        Value[] argument = new Value[1];

        argument[0] = Name.value("23");
        Value result = BuiltinOperations.ParseInt.getInstance().apply(argument);
        assertTrue(result instanceof IntegerValue);
        assertEquals(Name.value(23), result);

        argument[0] = Name.value("3.14159");
        result = BuiltinOperations.ParseFloat.getInstance().applyImplementation(argument);
        assertTrue(result instanceof FloatValue);
        assertEquals(Name.value(3.14159), result);

        assertEquals(Name.value(3), BuiltinOperations.ParseInt.getInstance().apply(argument));

        argument[0] = Name.value("1.5 litres");
        assertEquals(ErrorValue.INSTANCE, BuiltinOperations.ParseFloat.getInstance().apply(argument));

        argument[0] = Name.value("true");
        assertEquals(Name.trueValue(), BuiltinOperations.ParseBool.getInstance().apply(argument));
        argument[0] = Name.value("FALSE");
        assertEquals(Name.falseValue(), BuiltinOperations.ParseBool.getInstance().apply(argument));
        argument[0] = Name.value(0);
        assertEquals(Name.falseValue(), BuiltinOperations.ParseBool.getInstance().apply(argument));

        argument[0] = Name.value(3.14159);
        result = BuiltinOperations.ToString.getInstance().apply(argument);
        assertTrue(result instanceof StringValue);
        assertEquals(Name.value("3.14159"), result);

        argument[0] = Name.value(1);
        argument[0] = BuiltinOperations.ParseBool.getInstance().apply(argument);
        assertEquals(Name.value("true"), BuiltinOperations.ToString.getInstance().apply(argument));

    }

    @Test
    public void testTerms() {
        // Term ((2*(x+y))-((y^3)+z)) ; x=0, x=1, x=2
        DataOperation term = new OperationTerm.Appl( // root: -
                BuiltinOperations.Subtraction.getInstance(),
                3,
                new OperationTerm []{
                        new OperationTerm.Appl( // left tree: *
                            BuiltinOperations.Multiplication.getInstance(),
                            3,
                            new OperationTerm [] {
                                    new OperationTerm.Const<>(Name.value(2)), // left left const:2
                                    new OperationTerm.Appl( // left right tree: +
                                            BuiltinOperations.Addition.getInstance(),
                                            3,
                                            new OperationTerm[] {
                                                    new OperationTerm.Var(0),
                                                    new OperationTerm.Var(1)
                                            }
                                    )
                        }),
                        new OperationTerm.Appl( // right tree: +
                                BuiltinOperations.Addition.getInstance(),
                                3,
                                new OperationTerm[] {
                                        new OperationTerm.Appl(
                                                BuiltinOperations.Exponentiation.getInstance(),
                                                3,
                                                new OperationTerm[] {
                                                        new OperationTerm.Var(1),
                                                        new OperationTerm.Const<>(Name.value(3))
                                                }
                                        ),
                                        new OperationTerm.Var(2)
                                }
                        )
                }
        );

        Value[] arguments = new Value[3];
        arguments[0] = Name.value(3); // x=3
        arguments[1] = Name.value(2); // y=3
        arguments[2] = Name.value(1); // z=1

        // ((2*(3+2))-((2^3)+1)) = ((2*5)-(8+1)) = (10-9) = 1
        assertEquals(Name.value(1), term.apply(arguments));
        assertEquals("((2*(x+y))-((y^3)+z))", term.name().replaceAll("arg0", "x").replaceAll("arg1", "y").replaceAll("arg2", "z"));

        // setting one argument to a wrong type and so how error gets propagated
        arguments[1] = Name.value("2");
        assertEquals(ErrorValue.INSTANCE, term.apply(arguments));

    }

}
