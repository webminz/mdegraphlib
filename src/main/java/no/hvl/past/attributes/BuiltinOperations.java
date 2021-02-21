package no.hvl.past.attributes;

import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import org.checkerframework.checker.units.qual.A;

import java.util.function.BiFunction;

public class BuiltinOperations {

    private static Value boolOp(
            Value[] arguments,
            BiFunction<BoolValue, BoolValue, ? extends  Value> op) {
        if (arguments[0] instanceof BoolValue) {
            if (arguments[1] instanceof BoolValue) {
                return op.apply((BoolValue) arguments[0], (BoolValue) arguments[1]);
            }
        }
        return ErrorValue.INSTANCE;
    }

    private static Value numericOp(
            Value[] arguments,
            BiFunction<IntegerValue, IntegerValue, ? extends Value> onInt,
            BiFunction<FloatValue, FloatValue, ? extends Value> onFloats,
            boolean secondArgumentMustNotBeZero) {
        if (arguments[0] instanceof IntegerValue) {
            if (arguments[1] instanceof IntegerValue) {
                IntegerValue fst = (IntegerValue) arguments[0];
                IntegerValue snd = (IntegerValue) arguments[1];
                if (snd.isZero() && secondArgumentMustNotBeZero) {
                    return ErrorValue.INSTANCE;
                }
                return onInt.apply(fst, snd);
            } else if (arguments[1] instanceof FloatValue) {
                FloatValue fst = ((IntegerValue) arguments[0]).toFloat();
                FloatValue snd = ((FloatValue) arguments[1]);
                if (snd.isZero() && secondArgumentMustNotBeZero) {
                    return ErrorValue.INSTANCE;
                }
                return onFloats.apply(fst, snd);
            }
        } else if (arguments[0] instanceof FloatValue) {
            if (arguments[1] instanceof IntegerValue) {
                FloatValue fst = ((FloatValue) arguments[0]);
                FloatValue snd = ((IntegerValue) arguments[1]).toFloat();
                if (snd.isZero() && secondArgumentMustNotBeZero) {
                    return ErrorValue.INSTANCE;
                }
                return onFloats.apply(fst, snd);
            } else if (arguments[1] instanceof FloatValue) {
                FloatValue fst = ((FloatValue) arguments[0]);
                FloatValue snd = ((FloatValue) arguments[1]);
                if (snd.isZero() && secondArgumentMustNotBeZero) {
                    return ErrorValue.INSTANCE;
                }
                return onFloats.apply(fst, snd);
            }
        }
        return ErrorValue.INSTANCE;
    }

    public static class Equality implements DataOperation {

        private static Equality instance;

        private Equality() {
        }

        @Override
        public String name() {
            return "_==_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0].equals(arguments[1])) {
                return BoolValue.trueValue();
            } else {
                return BoolValue.falseValue();
            }
        }

        public static Equality getInstance() {
            if (instance == null) {
                instance = new Equality();
            }
            return instance;
        }
    }

    public static class Addition implements DataOperation {

        private static Addition instance;

        private Addition() {
        }

        @Override

        public String name() {
            return "_+_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return numericOp(arguments, IntegerValue::plus, FloatValue::add, false);
        }

        public static Addition getInstance() {
            if (instance == null) {
                instance = new Addition();
            }
            return instance;
        }
    }

    public static class Subtraction implements DataOperation {

        private static Subtraction instance;

        private Subtraction() {
        }

        @Override
        public String name() {
            return "_-_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return numericOp(arguments, IntegerValue::minus, FloatValue::sub, false);
        }

        public static Subtraction getInstance() {
            if (instance == null) {
                instance = new Subtraction();
            }
            return instance;
        }
    }

    public static class Multiplication implements DataOperation {

        private static Multiplication instance;

        @Override
        public String name() {
            return "_*_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return numericOp(arguments, IntegerValue::multiply, FloatValue::mul, false);
        }

        public static Multiplication getInstance() {
            if (instance == null) {
                instance = new Multiplication();
            }
            return instance;
        }
    }

    public static class Division implements DataOperation {

        private static  Division instance;

        private Division() {

        }

        @Override
        public String name() {
            return "_/_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return numericOp(arguments, IntegerValue::integerDivision, FloatValue::div,true);
        }

        public static Division getInstance() {
            if (instance == null) {
                instance = new Division();
            }
            return instance;
        }
    }

    public static class Modulo implements DataOperation {

        private static Modulo instance;

        private Modulo() {

        }

        @Override
        public String name() {
            return "_mod_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return numericOp(arguments, IntegerValue::modulo, (fst, snd) -> ErrorValue.INSTANCE, true);
        }

        public static Modulo getInstance() {
            if (instance == null) {
                instance = new Modulo();
            }
            return instance;
        }
    }

    public static class Exponentiation implements DataOperation {

        private static Exponentiation instance;

        private Exponentiation() {
        }

        @Override
        public String name() {
            return "_^_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return numericOp(arguments, IntegerValue::power, FloatValue::pow, false);
        }

        public static Exponentiation getInstance() {
            if (instance == null) {
                instance = new Exponentiation();
            }
            return instance;
        }
    }

    public static class LessOrEqual implements DataOperation {

        private static LessOrEqual instance;

        private LessOrEqual() {
        }

        @Override
        public String name() {
            return "_<=_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof StringValue && arguments[1] instanceof StringValue) {
                StringValue left = (StringValue) arguments[0];
                StringValue right = (StringValue) arguments[1];
                return left.lessEq(right);
            }
            return numericOp(arguments, IntegerValue::lessEq, FloatValue::lessEq, false);
        }

        public static LessOrEqual getInstance() {
            if (instance == null) {
                instance = new LessOrEqual();
            }
            return instance;
        }
    }

    public static class AlmostEqual implements DataOperation {

        private static final FloatValue DEFAULT_EPSILON = Name.value(0.00001);
        private static AlmostEqual instance;

        @Override
        public String name() {
            return
                    "_~_";
        }

        @Override
        public int arity() {
            return 3;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            FloatValue left;
            if (arguments[0] instanceof IntegerValue) {
                left = ((IntegerValue) arguments[0]).toFloat();
            } else if (arguments[0] instanceof FloatValue) {
                left = (FloatValue) arguments[0];
            } else {
                return ErrorValue.INSTANCE;
            }

            FloatValue right;
            if (arguments[1] instanceof IntegerValue) {
                right = ((IntegerValue) arguments[1]).toFloat();
            } else if (arguments[1] instanceof FloatValue) {
                right = (FloatValue) arguments[1];
            } else {
                return ErrorValue.INSTANCE;
            }

            FloatValue epsilon;
            if (arguments[2] == null) {
                epsilon = DEFAULT_EPSILON;
            } else if (arguments[2] instanceof IntegerValue) {
                epsilon = ((IntegerValue) arguments[2]).toFloat();
            } else if (arguments[2] instanceof FloatValue) {
                epsilon = (FloatValue) arguments[2];
            } else {
                return ErrorValue.INSTANCE;
            }


            return left.equals(right, epsilon) ? Name.trueValue() : Name.falseValue();
        }

        private AlmostEqual() {
        }

        public static AlmostEqual getInstance() {
            if (instance == null) {
                instance = new AlmostEqual();
            }
            return instance;
        }
    }

    public static class Negation implements DataOperation {

        private static Negation instance;

        @Override
        public String name() {
            return "¬_";
        }

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof BoolValue) {
                BoolValue value = (BoolValue) arguments[0];
                return value.not();
            }
            return ErrorValue.INSTANCE;
        }

        public static Negation getInstance() {
            if (instance == null) {
                instance = new Negation();
            }
            return instance;
        }

        private Negation() {
        }
    }

    public static class Conjunction implements DataOperation {

        private static Conjunction instance;

        @Override
        public String name() {
            return "_∧_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return boolOp(arguments, BoolValue::and);
        }

        private Conjunction() {
        }

        public static Conjunction getInstance() {
            if (instance == null) {
                instance = new Conjunction();
            }
            return instance;
        }
    }

    public static class Disjunction implements DataOperation {

        private static Disjunction instance;

        @Override
        public String name() {
            return "_∨_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return boolOp(arguments, BoolValue::or);
        }

        private Disjunction() {
        }

        public static Disjunction getInstance() {
            if (instance == null) {
                instance = new Disjunction();
            }
            return instance;
        }
    }

    public static class Xor implements DataOperation {

        private static Xor instance;

        @Override
        public String name() {
            return "_xor_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return boolOp(arguments, BoolValue::xor);
        }

        public static Xor getInstance() {
            if (instance == null) {
                instance = new Xor();
            }
            return instance;
        }

        private Xor() {
        }
    }

    public static class Implication implements DataOperation {

        private static Implication instance;

        @Override
        public String name() {
            return "_=>_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return boolOp(arguments, BoolValue::implies);
        }

        public static Implication getInstance() {
            if (instance == null) {
                instance = new Implication();
            }
            return instance;
        }

        private Implication() {
        }
    }

    public static class BiImplication implements DataOperation {

        private static BiImplication instance;

        @Override
        public String name() {
            return "_<=>_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return boolOp(arguments, BoolValue::biImplies);
        }

        public static BiImplication getInstance() {
            if (instance == null) {
                instance = new BiImplication();
            }
            return instance;
        }

        private BiImplication() {
        }
    }

    public static class IfThenElse implements DataOperation {

        private static IfThenElse instance;

        @Override
        public String name() {
            return "_?_:_";
        }

        @Override
        public int arity() {
            return 3;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof BoolValue) {
                BoolValue value = (BoolValue) arguments[0];
                if (value.equals(BoolValue.trueValue())) {
                    return arguments[1];
                } else {
                    return arguments[2];
                }
            }
            return ErrorValue.INSTANCE;
        }

        private IfThenElse() {
        }

        public static IfThenElse getInstance() {
            if (instance == null) {
                instance = new IfThenElse();
            }
            return instance;
        }
    }

    public static class Concatenation implements DataOperation {

        private static Concatenation instance;

        @Override
        public String name() {
            return "_++_";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof StringValue) {
                if (arguments[1] instanceof StringValue) {
                    StringValue left = (StringValue) arguments[0];
                    StringValue right = (StringValue) arguments[1];
                    return left.concat(right);
                }
            }
            return ErrorValue.INSTANCE;
        }

        private Concatenation() {
        }

        public static Concatenation getInstance() {
            if (instance == null) {
                instance = new Concatenation();
            }
            return instance;
        }
    }

    public static class Length implements DataOperation {

        private static Length instance;

        @Override
        public String name() {
            return "length(_)";
        }

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof StringValue) {
                StringValue value = (StringValue) arguments[0];
                return value.length();
            }
            return null;
        }

        private Length() {
        }

        public static Length getInstance() {
            if (instance == null) {
                instance = new Length();
            }
            return instance;
        }
    }

    public static class Substring implements DataOperation {

        private static Substring instance;


        @Override
        public String name() {
            return "substring(_,_,_)";
        }

        @Override
        public int arity() {
            return 3;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof StringValue) {
                if (arguments[1] instanceof IntegerValue) {
                    StringValue string = (StringValue) arguments[0];
                    IntegerValue from = (IntegerValue) arguments[1];
                    if (arguments[2] != null && arguments[2] instanceof IntegerValue) {
                        IntegerValue to = (IntegerValue) arguments[2];
                        return string.substring(from, to);
                    } else {
                        return string.substring(from);
                    }
                }
            }
            return ErrorValue.INSTANCE;
        }


        private Substring() {
        }

        public static Substring getInstance() {
            if (instance == null) {
                instance = new Substring();
            }
            return instance;
        }
    }

    public static class FirstIndexOf implements DataOperation {

        private static FirstIndexOf instance;

        @Override
        public String name() {
            return "firstIndexOf(_,_)";
        }

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof StringValue) {
                if (arguments[1] instanceof StringValue) {
                    StringValue bigger = (StringValue) arguments[0];
                    StringValue pattern = (StringValue) arguments[1];
                    return bigger.indexOf(pattern);
                }
            }
            return null;
        }

        private FirstIndexOf() {
        }

        public static FirstIndexOf getInstance() {
            if (instance == null) {
                instance = new FirstIndexOf();
            }
            return instance;
        }
    }

    public static class Reverse implements DataOperation {

        private static Reverse instance;

        @Override
        public String name() {
            return "reverse(_)";
        }

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof StringValue) {
                StringValue value = (StringValue) arguments[0];
                return value.reverse();
            }
            return null;
        }

        private Reverse() {
        }

        public static Reverse getInstance() {
            if (instance == null) {
                instance = new Reverse();
            }
            return instance;
        }
    }



    public static class ToString implements DataOperation {

        private static ToString instance;

        @Override
        public String name() {
            return "toString(_)";
        }

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof IntegerValue) {
                return ((IntegerValue) arguments[0]).asString();
            }
            if (arguments[0] instanceof FloatValue) {
                return ((FloatValue) arguments[0]).asString();
            }
            if (arguments[0] instanceof BoolValue) {
                return ((BoolValue) arguments[0]).asString();
            }
            if (arguments[0] instanceof StringValue) {
                return arguments[0];
            }
            if (arguments[0] instanceof UserValue) {
                return ((UserValue) arguments[0]).asString();
            }
            return ErrorValue.INSTANCE;
        }

        private ToString() {
        }

        public static ToString getInstance() {
            if (instance == null) {
                instance = new ToString();
            }
            return instance;
        }
    }

    public static class ParseInt implements DataOperation {

        private static ParseInt instance;

        @Override
        public String name() {
            return "parseInt(_)";
        }

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof StringValue) {
                return IntegerValue.tryParse((StringValue) arguments[0]);
            }
            return ErrorValue.INSTANCE;
        }

        private ParseInt() {
        }

        public static ParseInt getInstance() {
            if (instance == null) {
                instance = new ParseInt();
            }
            return instance;
        }
    }

    public static class ParseFloat implements DataOperation {

        private static ParseFloat instance;

        @Override
        public String name() {
            return "parseFloat(_)";
        }

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof StringValue) {
                return FloatValue.tryParse((StringValue) arguments[0]);
            }
            return ErrorValue.INSTANCE;
        }

        private ParseFloat() {
        }

        public static ParseFloat getInstance() {
            if (instance == null) {
                instance = new ParseFloat();
            }
            return instance;
        }
    }

    public static class ParseBool implements DataOperation {

        private static ParseBool instance;

        @Override
        public String name() {
            return "parseBool(_)";
        }

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            if (arguments[0] instanceof IntegerValue) {
                IntegerValue ival = (IntegerValue) arguments[0];
                if (ival.equals(Name.value(0))) {
                    return Name.falseValue();
                } else if (ival.equals(Name.value(1))) {
                    return Name.trueValue();
                } else {
                    return ErrorValue.INSTANCE;
                }
            }
            if (arguments[0] instanceof StringValue) {
                return BoolValue.tryParse((StringValue) arguments[0]);
            }
            return ErrorValue.INSTANCE;
        }

        private ParseBool() {
        }

        public static ParseBool getInstance() {
            if (instance == null) {
                instance = new ParseBool();
            }
            return instance;
        }
    }

    private BuiltinOperations() {
    }
}
