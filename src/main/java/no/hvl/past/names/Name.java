package no.hvl.past.names;

import no.hvl.past.attributes.BoolValue;
import no.hvl.past.attributes.FloatValue;
import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.attributes.StringValue;
import no.hvl.past.logic.Formula;
import no.hvl.past.logic.Model;
import no.hvl.past.util.ProperComparator;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;

/**
 * The abstract superclass of all types of names of graph elements.
 * Every graph element, i.e. node and edge label has to have a name, which
 * gives that particular node or edge an identity.
 * There are many different types of names, some represent variable names, other represent
 * actual values whilst others are identifiers that may be unique in a certain context.
 * Names can be prefixed, suffixed, re-combined and much more.
 *
 * The commonality of all names is that it can be serialized into a bytearray.
 * Equality of names is based on the bytearray representation.
 */
public abstract class Name implements ProperComparator<Name>, Formula<NameSet> {

    static final byte IDENTIFIER_MAGIC_BYTE = (byte) 0x00E0;
    static final byte VARIABLE_MAGIC_BYTE = (byte) 0x00E1;
    protected static final byte BOOL_VALUE_MAGIC_BYTE = (byte) 0x00E2;
    protected static final byte INT_VALUE_MAGIC_BYTE = (byte) 0x00E3;
    protected static final byte FLOAT_VALUE_MAGIC_BYTE = (byte) 0x00E4;
    protected static final byte STRING_VALUE_MAGIC_BYTE = (byte) 0x00E5;
    protected static final byte USER_VALUE_MAGIC_BYTE = (byte) 0x00E6;
    protected static final byte ERROR_VALUE = (byte) 0x00EF;
    static final byte PREFIX_MAGIC_BYTE = (byte) 0x00F0;
    static final byte UNARY_OP_MAGIC_BYTE = (byte) 0x00F1;
    static final byte BINARY_OP_MAGIC_BYTE = (byte) 0x00F2;
    static final byte MERGE_MAGIC_BYTE = (byte) 0x00F3;
    static final byte INDEX_MAGIC_BYTE = (byte) 0x00F4;


    /**
     * The byte representation may be expensive to calculate.
     * Thus it will be cashed after the first time.
     */
    private byte[] binaryRepresentation = null;

    /**
     * Caching getter for the binary representation.
     */
    private byte[] getBinaryRepresentation() {
        if (binaryRepresentation == null) {
            binaryRepresentation = getValue();
        }
        return binaryRepresentation;
    }

    /**
     * A serializable binary representation of the name,
     * it is used to determine if two names are equal
     * and must be implemented by all subclasses of name.
     */
    public abstract byte[] getValue();

    /**
     * Returns true if two names are the same.
     */
    public boolean identity(Name other) {
        return Arrays.equals(this.getBinaryRepresentation(), (other.getBinaryRepresentation()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Name) {
            return this.identity((Name) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getBinaryRepresentation());
    }



    @Override
    public final CompareResult cmp(Name lhs, Name rhs) {
        return lhs.compareWith(rhs);
    }

    /**
     * Compares two names with each other.
     * Normally names are either equal or non-comparable, i.e. a discrete partial order.
     * But some sub-classes of names (e.g. values, indices, user defined identifiers,...) may have a total order.
     */
    public CompareResult compareWith(Name other) {
        if (identity(other)) {
            return CompareResult.EQUAL;
        } else {
            return CompareResult.INCOMPARABLE;
        }
    }

    /**
     * Returns true if this name together with the given argument fall into a subclass of names that has
     * a total order.
     */
    public boolean inATotalOrderWith(Name other) {
        return false;
    }

    /**
     * Checks whether this name contains the given name.
     * Normally every name only contains itself.
     * In case of names combinators it may be the case the a name
     * contains multiple sub-names.
     */
    public boolean contains(Name name) {
        return this.identity(name);
    }

    public abstract boolean isVariable();

    public abstract boolean isValue();

    public abstract boolean isIdentifier();

    public abstract boolean isDerived();

    public abstract boolean isIndexed();

    public abstract String print(PrintingStrategy strategy);

    public abstract Name firstPart();

    public abstract Name secondPart();

    public abstract boolean isMultipart();

    public abstract Name part(int i);


    // Modifications

    // Prefix

    public Name prefixWith(Name name) {
        return new Prefix(this, name);
    }

    public boolean hasPrefix(Name name) {
        return getPrefix().isPresent();
    }

    public Optional<Name> getPrefix() {
        return Optional.empty();
    }

    public abstract Name unprefix(Name name);

    public abstract Name unprefixAll();

    // Typing

    public boolean isTyped() {
        return getType().isPresent();
    }

    public Optional<Name> getType() {
        return Optional.empty();
    }

    public Name stripType() {
        return this;
    }

    public Name typeBy(Name type) {
        return new BinaryCombinator(this, type, BinaryCombinator.Operation.TYPEDBY);
    }


    // Composition

    public Name composeSequentially(Name with) {
        return new BinaryCombinator(this, with, BinaryCombinator.Operation.SEQUENTIAL_COMPOSITION);
    }

    public boolean isComposed() {
        return false;
    }


    public Name subTypeOf(Name superT) {
        return new BinaryCombinator(this, superT, BinaryCombinator.Operation.EXTENDS);
    }

    public Name appliedTo(Name to) {
        return new BinaryCombinator(this, to, BinaryCombinator.Operation.APPLIED_TO);
    }

    public Name query(Name query) {
        return new BinaryCombinator(this, query, BinaryCombinator.Operation.PULLBACK);
    }

    public Name pair(Name other) {
        return new BinaryCombinator(this, other, BinaryCombinator.Operation.PAIR);
    }

    public Name times(Name other) {
        return new BinaryCombinator(this, other, BinaryCombinator.Operation.TIMES);}

    public Name projectionOn(Name other) {
        return new BinaryCombinator(this, other, BinaryCombinator.Operation.PROJECTION);
    }

    public Name injectedFrom(Name src) {
        return new BinaryCombinator(this, src, BinaryCombinator.Operation.INJECTION);
    }

    public Name preimage(Name name) {
        return new BinaryCombinator(this, name, BinaryCombinator.Operation.PREIMAGE);
    }

    public Name extendedBy(Name name) {
        return new BinaryCombinator(this, name, BinaryCombinator.Operation.AUGMENTED_WITH);
    }


    public Name sum(Name other) {
        return new BinaryCombinator(this, other, BinaryCombinator.Operation.COPRODUCT);
    }

    public Name elementOf(Name other) {
        return new BinaryCombinator(this, other, BinaryCombinator.Operation.ELEMENT_OF);}


    public Name mergeWith(Name... others) {
        return this.mergeWith(Arrays.asList(others));
    }

    public Name mergeWith(List<Name> others) {
        List<Name> all = new ArrayList<>();
        all.add(this);
        all.addAll(others);
        return Name.merge(all);
    }

    public static Name merge(List<Name> all) {
        return new MulitaryCombinator(all, MulitaryCombinator.Operation.UNION);
    }

    public static Name concat(List<Name> all) {
        return new MulitaryCombinator(all, MulitaryCombinator.Operation.CONCAT);
    }

    public Name inverse() {
        return new UnaryCombinator(this, UnaryCombinator.Operation.INVERSE);
    }

    public Name iterated(){
        return new UnaryCombinator(this, UnaryCombinator.Operation.ITERATED);
    }

    public Name copied() {
        return new UnaryCombinator(this, UnaryCombinator.Operation.COPIED);
    }

    public Name complement(){
        return new UnaryCombinator(this, UnaryCombinator.Operation.COMPLEMENT);
    }

    public Name optional(){
        return new UnaryCombinator(this, UnaryCombinator.Operation.OPTIONAL);
    }

    public Name mandatory(){
        return new UnaryCombinator(this, UnaryCombinator.Operation.MANDATORY);
    }

    public Name global() {
        return new UnaryCombinator(this, UnaryCombinator.Operation.GLOBAL);
    }

    public Name absolute() {
        return new UnaryCombinator(this, UnaryCombinator.Operation.ABSOLUTE);

    }

    public Name index(long idx) {
        return new Index(this, idx);
    }

    public Name downTypeAlong(Name superName) {
        return new BinaryCombinator(this, superName, BinaryCombinator.Operation.DOWNTYPE);
    }

    public Name addSuffix(Name suffix) {
        return new Prefix(suffix, this);
    }

    public Name childOf(Name parent) {
        return new BinaryCombinator(this, parent, BinaryCombinator.Operation.CHILD_OF);
    };


    // Factory methods

    public static Identifier identifier(String name) {
        return new SimpleIdentifier(name);
    }

    public static AnonymousIdentifier anonymousIdentifier() {
        return new AnonymousIdentifier();
    }

    public static UUIDIdentifier randomUUID() {
        return UUIDIdentifier.createRandom();
    }

    public static UUIDIdentifier timeBasedUUID() {
        return UUIDIdentifier.createTimeBased();
    }

    public static UUIDIdentifier namespaceBased(UUIDIdentifier id, Identifier identifier) {
        return UUIDIdentifier.createNamespaceBased(id, identifier.getValue());
    }

    public static Variable variable(String name) {
        return new Variable(name);
    }

    public static StringValue value(String value) {
        return new StringValue(value);
    }

    public static IntegerValue value(long value) {
        return new IntegerValue(BigInteger.valueOf(value));
    }

    public static IntegerValue value(BigInteger bigValue) {
        return new IntegerValue(bigValue);
    }

    public static FloatValue value(double value) {
        return new FloatValue(value);
    }

    public static BoolValue trueValue() {
        return new BoolValue(true);
    }

    public static BoolValue falseValue() {
        return new BoolValue(false);
    }

    public  Name unprefixTop() {
        return this;
    }

    @Override
    public boolean isSatisfied(Model<NameSet> model) {
        // Every Name is also an atomic propositional formula
        if (model instanceof NameSet) {
            NameSet instance = (NameSet) model;
            return instance.contains(this);
        }
        return false;
    }

    public Name substitution(Name name) {
        return new BinaryCombinator(this, name, BinaryCombinator.Operation.SUBSTITUTION);
    }

    public Name source() {
        return new UnaryCombinator(this, UnaryCombinator.Operation.SOURCE);
    }

    public Name target() {
        return new UnaryCombinator(this, UnaryCombinator.Operation.TARGET);
    }
}
