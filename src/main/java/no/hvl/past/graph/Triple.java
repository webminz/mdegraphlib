package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

import java.util.Objects;
import java.util.Optional;

/**
 * Constituents of a graph.
 * Basically they represent an edge, that starts in a node, ends in a node and has a label.
 * But using a trick, they also serve as nodes as well, i.e. a special edge that is a loop and has the name
 * of the node as its label.
 */
public class Triple {

    private final Name source;
    private final Name label;
    private final Name target;

    public Triple(Name source, Name edge, Name target) {
        this.source = source;
        this.label = edge;
        this.target = target;
    }

    public Name getSource() {
        return source;
    }

    public Name getLabel() {
        return label;
    }

    public Name getTarget() {
        return target;
    }

    public Triple prefix(Name name) {
        return new Triple(this.source.prefix(name), this.label.prefix(name), this.target.prefix(name));
    }

    public Triple unprefix(Name name) {
        return new Triple(this.source.unprefix(name), this.label.unprefix(name), this.target.unprefix(name));
    }

    public boolean isNode() {
        return this.source.equals(this.label) && this.label.equals(this.target);
    }

    public boolean isEddge() {
        return !this.source.equals(this.label) || !this.label.equals(this.target);
    }

    public boolean isIncident(Triple other) {
        return this.target.equals(other.source) || this.source.equals(other.target);
    }

    public Optional<Triple> compose(Triple other) {
        if (this.target.equals(other.source)) {
            return Optional.of(new Triple(this.source, this.label.composeSequentially(other.label), other.target));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Triple> preCompose(Triple other) {
        if (this.source.equals(other.target)) {
            return Optional.of((new Triple(other.source, other.label.composeSequentially(this.label), this.target)));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Some constructions require to combine elements from different graph.
     * This operation merges two triples and thus requires that both have been prefixed.
     */
    public Triple combine(Triple other) {
        return new Triple(this.source.merge(other.source),
                this.label.merge(other.label),
                this.target.merge(other.target));
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
        return "" +
                '(' + source + ')' +
                "-[" + label + "]->" +
                '(' + target + ')';
    }

    public static Triple fromNode(Name nodeName) {
        return new Triple(nodeName, nodeName, nodeName);
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

}
