package no.hvl.past.attributes;

import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;

import java.util.List;

public abstract class OperationTerm implements DataOperation {

    public static class Const<V extends Value>  extends OperationTerm {

        private final V value;

        public Const(V value) {
            this.value = value;
        }

        @Override
        public String name() {
            return value.print(PrintingStrategy.DETAILED);
        }

        @Override
        public int arity() {
            return 0;
        }

        @Override
        public Value apply(Value[] arguments) {
            return applyImplementation(arguments);
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return value;
        }
    }

    public static class Appl extends OperationTerm {

        private final DataOperation operation;
        private final OperationTerm[] subTerms;
        private final int arity;

        public Appl(DataOperation operation, int arity,  OperationTerm[] subTerms) {
            this.operation = operation;
            this.subTerms = subTerms;
            this.arity = arity;
        }

        @Override
        public String name() {
            String result = "(" + operation.name() + ")";
            int i = 0;
            while (i < subTerms.length && result.contains("_")) {
                int idx = result.indexOf('_');
                result = (result.substring(0, idx) + subTerms[i].name() + result.substring(idx + 1));
                i++;
            }
            return result;
        }

        @Override
        public int arity() {
            return arity;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            Value[] args = new Value[subTerms.length];
            for (int i = 0; i < subTerms.length; i++) {
               args[i] = subTerms[i].applyImplementation(arguments);
            }
            return operation.apply(args);
        }
    }

    public static class Var extends OperationTerm {

        public final int argNo;

        public Var(int argNo) {
            this.argNo = argNo;
        }

        @Override
        public String name() {
            return "arg" + argNo;
        }

        @Override
        public int arity() {
            return argNo + 1;
        }

        @Override
        public Value applyImplementation(Value[] arguments) {
            return arguments[argNo];
        }
    }

}
