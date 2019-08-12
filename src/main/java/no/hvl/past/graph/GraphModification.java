package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a graph that arises from a slight modification.
 * It is realized via a proxy pattern that shadows a respective base graph.
 * A modification can express the following features:
 * - Adding elements
 * - Removing elements
 * - Adding a prefix for every element.
 *
 * Important notice: prefixing affects only the base graph, i.e. if this modification
 * add prefixes it is assumed that added elements are already prefixed and removed elements
 * have no prefix!
 */
public class GraphModification implements AbstractGraph {


    private final AbstractGraph basedOn;

    private final Set<Triple> adds;

    private final Set<Triple> removes;

    private final Optional<Name> prefix;

    private GraphModification(AbstractGraph basedOn, Set<Triple> adds, Set<Triple> removes, Optional<Name> prefix) {
        this.basedOn = basedOn;
        this.adds = adds;
        this.removes = removes;
        this.prefix = prefix;
    }

    public boolean hasPrefix() {
        return this.prefix.isPresent();
    }

    @Override
    public AbstractMorphism identity() {
        return new IdentityMorphism(basedOn);
    }

    @Override
    public boolean contains(Triple triple) {
        if (this.adds.contains(triple)) {
            return true;
        }
        if (this.hasPrefix() && triple.hasPrefix(this.prefix.get())) {
            return basedOn.contains(triple.unprefix(this.prefix.get())) && !this.removes.contains(triple.unprefix(this.prefix.get()));
        }
        if (!this.hasPrefix()) {
            return this.basedOn.contains(triple) && !this.removes.contains(triple);
        }
        return false;
    }

    @Override
    public boolean contains(Name name) {
        if (this.adds.stream().flatMap(t -> Stream.of(t.getSource(), t.getLabel(), t.getTarget())).collect(Collectors.toSet()).contains(name)) {
            return true;
        }
        if (this.hasPrefix() && name.hasPrefix(this.prefix.get())) {
            return this.basedOn.contains(name.unprefix(this.prefix.get())) && this.removes.stream().noneMatch(t -> t.getLabel().equals(name.unprefix(this.prefix.get())));
        }
        if (!this.hasPrefix()) {
            return this.basedOn.contains(name) && this.removes.stream().noneMatch(t -> t.getLabel().equals(name));
        }
        return false;
    }

    @Override
    public Set<Triple> outgoing(Name from) {
        Set<Triple> result = new HashSet<>();
        if (this.hasPrefix() && from.hasPrefix(this.prefix.get())) {
            result.addAll(this.basedOn.outgoing(from.unprefix(from)).stream().map(t -> t.prefix(this.prefix.get())).collect(Collectors.toSet()));
        }
        if (!this.hasPrefix()) {
            result.addAll(this.basedOn.outgoing(from));
        }
        result.removeIf(t -> this.removes.stream().anyMatch(t2 -> t2.equals(t) || t2.equals(t.unprefix(from))));
        result.addAll(adds.stream().filter(t -> t.getSource().equals(from)).collect(Collectors.toSet()));
        return result;
    }

    @Override
    public Set<Triple> incoming(Name to) {
        Set<Triple> result = new HashSet<>();
        if (this.hasPrefix() && to.hasPrefix(this.prefix.get())) {
            result.addAll(this.basedOn.incoming(to.unprefix(to)).stream().map(t -> t.prefix(this.prefix.get())).collect(Collectors.toSet()));
        }
        if (!this.hasPrefix()) {
            result.addAll(this.basedOn.incoming(to));
        }
        result.removeIf(t -> this.removes.stream().anyMatch(t2 -> t2.equals(t) || t2.equals(t.unprefix(to))));
        result.addAll(adds.stream().filter(t -> t.getSource().equals(to)).collect(Collectors.toSet()));
        return result;
    }

    @Override
    public Iterator<Triple> iterator() {
        Iterator<Triple> aiterator = basedOn.iterator();
        Iterator<Triple> biterator = this.adds.iterator();
        return new Iterator<Triple>() {

            private Triple lookforNext() {
                if (aiterator.hasNext()) {
                    Triple next = aiterator.next();
                    if (!removes.contains(next)) {
                        if (hasPrefix() && !next.hasPrefix(prefix.get())) {
                            return next.prefix(prefix.get());
                        } else {
                            return next;
                        }
                    } else {
                        return lookforNext();
                    }
                }
                if (biterator.hasNext()) {
                    return biterator.next();
                }
                return null;
            }

            private Triple lookahead = lookforNext();

            @Override
            public boolean hasNext() {
                return lookahead != null;
            }

            @Override
            public Triple next() {
                Triple next = lookahead;
                lookahead = lookforNext();
                return next;
            }
        };
    }


    public GraphModification merge(GraphModification otherExtension) {
        Set<Triple> newAdds = new HashSet<>();
        newAdds.addAll(otherExtension.adds);
        newAdds.addAll(adds);
        Set<Triple> newDeletes = new HashSet<>();
        newDeletes.addAll(otherExtension.removes);
        newDeletes.addAll(removes);
        return new GraphModification(otherExtension.basedOn, newAdds, newDeletes, this.prefix.isPresent() ? this.prefix : otherExtension.prefix);
    }

    @Override
    public Name getName() {
        return basedOn.getName();
    }

    public static GraphModification create(AbstractGraph basedOn, Set<Triple> adds, Set<Triple> deletes) {
        GraphModification result = new GraphModification(basedOn, adds, deletes, Optional.empty());
        if (basedOn instanceof GraphModification) {
            return result.merge((GraphModification) basedOn);
        }
        return result;
    }

    public static GraphModification create(AbstractGraph basedOn, Set<Triple> adds, Set<Triple> deletes, Name prefix) {
        GraphModification result = new GraphModification(basedOn, adds, deletes, Optional.of(prefix));
        if (basedOn instanceof GraphModification) {
            return result.merge((GraphModification) basedOn);
        }
        return result;
    }
}
