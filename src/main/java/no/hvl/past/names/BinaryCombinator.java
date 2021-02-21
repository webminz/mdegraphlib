package no.hvl.past.names;

import no.hvl.past.util.ByteUtils;

import java.util.Optional;

public final class BinaryCombinator extends Combinator {

    /**
     * The type of the operation that combines both names.
     */
    enum Operation {

        /**
         * Sequential composition of both names (possibly edge names).
         */
        SEQUENTIAL_COMPOSITION {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.sequentialComposition(first, second);
            }
        },

        /**
         * Aka. query of the first over the second.
         */
        PULLBACK {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.pullback(first, second);
            }
        },

        /**
         * Both names are grouped together in a pair.
         */
        PAIR {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.pair(first, second);
            }
        },

        /**
         * Aka. as the sum of two names.
         */
        COPRODUCT {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.coproduct(first, second);
            }
        },

        /**
         * The first is typed over the latter.
         */
        TYPEDBY {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.typedBy(first, second);
            }
        },

        /**
         * The first extends the latter (subset relation)
         */
        EXTENDS {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.extens(first, second);
            }
        },

        /**
         * The first is applied with the second as parameter.
         */
        APPLIED_TO {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.appliedTo(first, second);
            }
        },

        /**
         * The first is an element of the latter.
         */
        ELEMENT_OF {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.elementOf(first, second);
            }
        },

        /**
         * Both names are combined in a product.
         */
        TIMES {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.times(first, second);
            }
        }, DOWNTYPE {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.downType(first, second);
            }

        }, PROJECTION {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.projection(first, second);
            }
        }, INJECTION {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.injection(first, second);
            }
        }, PREIMAGE {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.preimage(first,second);
            }
        },

        AUGMENTED_WITH {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.augmentedWith(first, second);
            }

        }, CHILD_OF {
            @Override
            public String print(PrintingStrategy strategy, String child, String parent) {
                return strategy.childOf(child, parent);
            }
        }, SUBSTITUTION {
            @Override
            public String print(PrintingStrategy strategy, String first, String second) {
                return strategy.substituted(first,second);
            }
        };


        public abstract String print(PrintingStrategy strategy, String first, String second);

    }

    private final Operation operation;

    private final Name first;

    private final Name second;

    BinaryCombinator(Name first, Name second, Operation operation) {
        this.operation = operation;
        this.first = first;
        this.second = second;
    }

    @Override
    public byte[] getValue() {
        byte[] fst = first.getValue();
        byte[] snd = second.getValue();
        byte[] result = new byte[2];
        result[0] = BINARY_OP_MAGIC_BYTE;
        result[1] = (byte) this.operation.ordinal();
        return ByteUtils.concat(result, ByteUtils.concat(fst, snd));
    }

    @Override
    public boolean isVariable() {
        return first.isVariable() || second.isVariable();
    }

    @Override
    public boolean isValue() {
        return first.isValue() && second.isValue();
    }

    @Override
    public boolean isIdentifier() {
        return first.isIdentifier() && second.isIdentifier();
    }

    @Override
    public boolean isIndexed() {
        return false;
    }

    @Override
    public boolean isTyped() {
        return this.operation == Operation.TYPEDBY;
    }

    @Override
    public String print(PrintingStrategy strategy) {
        String firstTransformed = first.print(strategy);
        String secondTransformed = second.print(strategy);
        return this.operation.print(strategy, firstTransformed, secondTransformed);
    }

    @Override
    public boolean contains(Name name) {
        return this.identity(name) || first.contains(name) || second.contains(name);
    }

    @Override
    public Name unprefix(Name name) {
        return new BinaryCombinator(first.unprefix(name), second.unprefix(name), this.operation);
    }

    @Override
    public boolean isComposed() {
        return this.operation == Operation.SEQUENTIAL_COMPOSITION;
    }

    @Override
    public Name firstPart() {
        return first;
    }

    @Override
    public Name secondPart() {
        return second;
    }

    @Override
    public Name part(int i) {
        if (i >= 0) {
            return first;
        }
        return secondPart();
    }

    @Override
    public boolean isMultipart() {
        return true;
    }

    @Override
    public Name unprefixAll() {
        return new BinaryCombinator(first.unprefixAll(), second.unprefixAll(), this.operation);
    }

    @Override
    public Name stripType() {
        if (this.operation == Operation.TYPEDBY) {
            return this.first;
        }
        return this;
    }

    @Override
    public Optional<Name> getType() {
        if (this.operation == Operation.TYPEDBY) {
            return Optional.of(this.second);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return this.print(PrintingStrategy.IGNORE_PREFIX);
    }

}
