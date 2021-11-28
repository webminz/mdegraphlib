package no.hvl.past.graph.elements;

import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import no.hvl.past.util.ProperComparator;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Constituents of a graph.
 * Basically they represent an edge, that starts in a node, ends in a node and has a label.
 * But using a trick, they also serve as nodes as well, i.e. a special edge that is a loop and has the name
 * of the node as its label.
 */
public class Triple implements ProperComparator<Triple>, Comparable<Triple> {

    private final Name source;
    private final Name label;
    private final Name target;

    public Triple(Name source, Name edge, Name target) {
        this.source = source;
        this.label = edge;
        this.target = target;
    }

    // Properties and Getters

    public Name getSource() {
        return source;
    }

    public Name getLabel() {
        return label;
    }

    public Name getTarget() {
        return target;
    }

    public Stream<Name> parts() {
        return Stream.of(source, label, target);
    }

    public boolean isNode() {
        return this.source.equals(this.label) && this.label.equals(this.target);
    }

    public boolean isEddge() {
        return !this.source.equals(this.label) || !this.label.equals(this.target);
    }

    /**
     * Returns true if this edge is incident to another.
     */
    public boolean isIncident(Triple other) {
        return this.target.equals(other.source) || this.source.equals(other.target);
    }

    public boolean isAdjacent(Triple other) {
        return this.target.equals(other.target) || this.source.equals(other.source);
    }

    /**
     * Returns true if this edge represents a composition.
     */
    public boolean isComposed() {
        return getLabel().isComposed();
    }

    public boolean isDerived() {
        return getLabel().isDerived();
    }

    /**
     * Returns true if this edge represents an attribute.
     */
    public boolean isAttribute() {
        return getTarget().isValue();
    }

    /**
     * If this edge represents an instance of an attribute, this method
     * provides the attribute value.
     */
    public Optional<Value> getValue() {
        if (target instanceof Value) {
            return Optional.of((Value) target);
        }
        return Optional.empty();
    }



    // Combinators

    /**
     * Composes (in diagrammatical order) this edge with another edge
     * if possible (this.target == other.source).
     */
    public Optional<Triple> compose(Triple other) {
        if (this.target.equals(other.source)) {
            return Optional.of(new Triple(this.source, this.label.composeSequentially(other.label), other.target));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Composes (in mathematical function application order) this edge with another
     * edge if possible (i.e. this.source == other.target)
     */
    public Optional<Triple> preCompose(Triple other) {
        if (this.source.equals(other.target)) {
            return Optional.of((new Triple(other.source, other.label.composeSequentially(this.label), this.target)));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Produces the inverse of the edge.
     */
    public Triple inverse() {
        if (isNode()) {
            return this;
        } else {
            return new Triple(target, label.inverse(), source);
        }

    }

    // Object overrides

    @Override
    public CompareResult cmp(Triple lhs, Triple rhs) {
        CompareResult compareResult = lhs.getLabel().compareWith(rhs.getLabel());
        if (compareResult.equals(CompareResult.EQUAL)) {
            return lhs.getLabel().compareWith(rhs.getLabel());
        } else {
            return compareResult;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple triple = (Triple) o;
        return Objects.equals(source, triple.source) &&
                Objects.equals(label, triple.label) &&
                Objects.equals(target, triple.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, label, target);
    }

    @Override
    public String toString() {
        if (isNode()) {
            return "(" + label + ")";
        }
        return "" +
                '(' + source + ')' +
                "-[" + label + "]->" +
                '(' + target + ')';
    }

    // Factory Methods

    public static Triple node(Name nodeName) {
        return new Triple(nodeName, nodeName, nodeName);
    }


    public static Triple edge(Name source, Name label, Name target) {
        if (source == null || label == null || label == target) {
            throw new RuntimeException("Arrrgh NULL!" + source + " " + label + " " + target);
        }
        return new Triple(source, label, target);
    }

    public static Triple fromIdentifiers(String sourceIdentifier, String edgeIdentifier, String targetIdentifier) {
        return new Triple(Name.identifier(sourceIdentifier), Name.identifier(edgeIdentifier), Name.identifier(targetIdentifier));
    }

    public static Triple anonymousEdge(Name from, Name to) {
        return new Triple(from, Name.anonymousIdentifier(), to);
    }

    public static Triple intAttribute(Name owner, Name attributeInstanceName, long value) {
        return new Triple(owner, attributeInstanceName, Name.value(value));
    }

    public static Triple intAttribute(Name owner, Name attributeInstanceName, BigInteger value) {
        return new Triple(owner, attributeInstanceName, Name.value(value));
    }

    public static Triple floatAttribute(Name owner, Name attributeInstanceName, double value) {
        return new Triple(owner, attributeInstanceName, Name.value(value));
    }

    public static Triple stringAttribute(Name owner, Name attributeInstanceName, String value) {
        return new Triple(owner, attributeInstanceName, Name.value(value));
    }

    public static Triple boolAttribute(Name owner, Name attributeInstanceName, boolean value) {
        return new Triple(owner, attributeInstanceName, value ? Name.trueValue() : Name.falseValue());
    }
    // Renamings

    public Triple prefix(Name name) {
        return new Triple(this.source.prefixWith(name), this.label.prefixWith(name), this.target.prefixWith(name));
    }

    public Triple unprefix(Name name) {
        return new Triple(this.source.unprefix(name), this.label.unprefix(name), this.target.unprefix(name));
    }

    /**
     * Returns a variant of this triple where all names have been stripped of all prefixes.
     */
    public Triple unprefixAll() {
        return new Triple(this.source.unprefixAll(), this.label.unprefixAll(), this.target.unprefixAll());
    }


    /**
     * Returns true if this triple is homogeneously prefixed with the given name.
     */
    public boolean hasPrefix(Name name) {
        return this.getSource().hasPrefix(name) && this.getLabel().hasPrefix(name) && this.getTarget().hasPrefix(name);
    }


    public Optional<Triple> map(Function<Name, Optional<Name>> mapping) {
        if (parts().anyMatch(n -> !mapping.apply(n).isPresent())) {
            return Optional.empty();
        }
        return Optional.of(new Triple(mapping.apply(getSource()).get(), mapping.apply(getLabel()).get(), mapping.apply(getTarget()).get()));
    }

    public Triple mapName(Function<Name, Name> function) {
        return new Triple(function.apply(source), function.apply(label), function.apply(target));
    }


    public Triple combineMap(Triple other, BiFunction<Name, Name, Name> function) {
        return new Triple(function.apply(this.source, other.source), function.apply(this.label, other.label), function.apply(this.target, other.target));
    }

    public boolean namesHaveProperty(Predicate<Name> predicate) {
        return predicate.test(source) && predicate.test(label) && predicate.test(target);
    }

    public Optional<Name> getPrefix() {
        Optional<Name> sourcePrefix = this.source.getPrefix();
        Optional<Name> labelPrefix = this.label.getPrefix();
        Optional<Name> targetPrefix = this.target.getPrefix();
        if (sourcePrefix.isPresent() &&
                labelPrefix.isPresent() &&
                targetPrefix.isPresent() &&
                sourcePrefix.get().equals(labelPrefix.get()) &&
                sourcePrefix.get().equals(targetPrefix.get())) {
            return sourcePrefix;
        }
        return Optional.empty();
    }

    public boolean contains(Name name) {
        return this.getSource().equals(name) || this.getLabel().equals(name) || this.getTarget().equals(name);
    }

    @Override
    public int compareTo(Triple o) {
        switch (this.cmp(this, o)) {
            case EQUAL:
                return 0;
            case LESS_THAN:
                return -1;
            case BIGGER_THAN:
                return 1;
            case INCOMPARABLE:
            default:
                return 1;
        }
    }
}
