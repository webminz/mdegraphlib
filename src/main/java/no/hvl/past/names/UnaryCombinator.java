package no.hvl.past.names;


import no.hvl.past.util.ByteUtils;

/**
 * A unary combinator is a special case of a names combinator consisting of
 * only a single name. It can thus be thougth of as a modifier.
 */
public final class UnaryCombinator extends Combinator {

    enum Operation {

        COMPLEMENT {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.complement(nested);
            }
        },

        OPTIONAL {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.optional(nested);
            }
        },

        MANDATORY {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.mandatory(nested);
            }
        },

        INVERSE {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.inverse(nested);
            }
        },

        ITERATED {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.iterated(nested);
            }
        }, ABSOLUTE {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.absolute(nested);
            }
        }, COPIED {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.copied(nested);
            }
        }, GLOBAL {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.global(nested);
            }
        }, SOURCE {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.source(nested);
            }
        }, TARGET {
            @Override
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.target(nested);
            }
        }, RESULT {
            public String print(PrintingStrategy strategy, String nested) {
                return strategy.resultOf(nested);
            }

        };



        public abstract String print(PrintingStrategy strategy, String nested);
    }

    private final Name nested;

    private final Operation op;

    UnaryCombinator(Name nested, Operation op) {
        this.nested = nested;
        this.op = op;
    }


    @Override
    public byte[] getValue() {
        byte[] value = nested.getValue();
        byte[] result = new byte[2];
        result[0] = UNARY_OP_MAGIC_BYTE;
        result[1] =  (byte) this.op.ordinal();
        return ByteUtils.concat(result, value);
    }

    @Override
    public boolean isVariable() {
        return nested.isVariable();
    }

    @Override
    public boolean isValue() {
        return nested.isValue();
    }

    @Override
    public boolean isIdentifier() {
        return nested.isIdentifier();
    }

    @Override
    public boolean isIndexed() {
        return nested.isIndexed();
    }


    @Override
    public String print(PrintingStrategy strategy) {
        String nested = this.nested.print(strategy);
        return this.op.print(strategy, nested);
    }

    @Override
    public boolean contains(Name name) {
        return this.identity(name) || nested.contains(name);
    }

    @Override
    public Name unprefix(Name name) {
        return new UnaryCombinator(nested.unprefix(name), op);
    }

    @Override
    public Name unprefixAll() {
        return new UnaryCombinator(nested.unprefixAll(), op);
    }

    @Override
    public Name firstPart() {
        return this;
    }

    @Override
    public Name secondPart() {
        return this;
    }

    @Override
    public Name part(int i) {
        return this;
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public String toString() {
        return this.print(PrintingStrategy.DETAILED);
    }
}
