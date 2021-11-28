package no.hvl.past.attributes;

public interface DataTypeVisitor<R> {

    R handleStringType();

    R handleIntegerType();

    R handleFloatType();

    R handleBoolType();

    R handleEnumType();

    R handleOtherUserType();

    class DefaultDataTypeVisitor<R> implements  DataTypeVisitor<R> {

        private final R defaultReturnValue;

        public DefaultDataTypeVisitor(R defaultReturnValue) {
            this.defaultReturnValue = defaultReturnValue;
        }


        @Override
        public R handleStringType() {
            return defaultReturnValue;
        }

        @Override
        public R handleIntegerType() {
            return defaultReturnValue;
        }

        @Override
        public R handleFloatType() {
            return defaultReturnValue;
        }

        @Override
        public R handleBoolType() {
            return defaultReturnValue;
        }

        @Override
        public R handleEnumType() {
            return defaultReturnValue;
        }

        @Override
        public R handleOtherUserType() {
            return defaultReturnValue;
        }
    }


}
