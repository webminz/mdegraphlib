package no.hvl.past.attributes;

import no.hvl.past.names.Name;

public enum DataTypeDescription {

    INTEGRAL_NUMBER {
        @Override
        public <R> R accept(DataTypeVisitor<R> visitor) {
            return visitor.handleIntegerType();
        }
    },
    FLOATING_POINT_NUMBER {
        @Override
        public <R> R accept(DataTypeVisitor<R> visitor) {
            return visitor.handleFloatType();
        }
    },
    BOOLEAN {
        @Override
        public <R> R accept(DataTypeVisitor<R> visitor) {
            return visitor.handleBoolType();
        }
    },
    STRING {
        @Override
        public <R> R accept(DataTypeVisitor<R> visitor) {
            return visitor.handleStringType();
        }
    },
    ENUMERATED {
        @Override
        public <R> R accept(DataTypeVisitor<R> visitor) {
            return visitor.handleEnumType();
        }
    },
    CUSTOM {
        @Override
        public <R> R accept(DataTypeVisitor<R> visitor) {
            return visitor.handleOtherUserType();
        }
    };

    public abstract  <R> R accept(DataTypeVisitor<R> visitor);

    public Name parse(String value) {
        return this.accept(new DataTypeVisitor<Name>() {
            @Override
            public Name handleStringType() {
                return Name.value(value);
            }

            @Override
            public Name handleIntegerType() {
                return Name.value(Long.parseLong(value));
            }

            @Override
            public Name handleFloatType() {
                return Name.value(Double.parseDouble(value));
            }

            @Override
            public Name handleBoolType() {
                return value.equals("true") ? Name.trueValue() : Name.falseValue();
            }

            @Override
            public Name handleEnumType() {
                return Name.identifier(value);
            }

            @Override
            public Name handleOtherUserType() {
                return Name.identifier(value);
            }
        });
    }

}
