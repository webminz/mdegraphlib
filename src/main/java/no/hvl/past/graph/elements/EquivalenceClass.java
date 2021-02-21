package no.hvl.past.graph.elements;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import no.hvl.past.names.Name;
import no.hvl.past.util.Holder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a equivalence class of triples, i.e.
 * graph elements that are set into relation.
 */
public class EquivalenceClass {

    private static final Name PLACEHOLDER_NS = Name.identifier("");

    /**
     * The elements in this class, they are namespace indexed by the graph from where they originate.
     */
    private final Multimap<Name, Triple> slots;

    private EquivalenceClass(Multimap<Name, Triple> slots) {
        this.slots = slots;
    }

    /**
     * Checks if this class and the other given class have elements in common
     * and are not identical at the same time.
     */
    public boolean overlapsButNonIdentical(EquivalenceClass other) {
        if (other.equals(this)) {
            return false;
        }
        for (Name key : this.slots.keySet()) {
            if (!this.slots.get(key).isEmpty() && !other.slots.get(key).isEmpty() && !Sets.intersection(
                    Sets.newHashSet(this.slots.get(key)), Sets.newHashSet(other.slots.get(key))).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Merges this class and the other class together.
     */
    public EquivalenceClass merge(EquivalenceClass other) {
        Multimap<Name, Triple> result = HashMultimap.create();
        result.putAll(this.slots);
        result.putAll(other.slots);
        return new EquivalenceClass(result);
    }

    public boolean compatible(Triple edge) {
        if (edge.isNode()) {
            return this.isNodeClass();
        } else {
            return this.isEdgeClass();
        }
    }

    public boolean contains(Name namespace, Triple edge) {
        return this.slots.get(namespace).contains(edge);
    }

    public boolean containsNode(Name namespace, Name node) {
        return this.slots.get(namespace).contains(Triple.node(node));
    }

    public void add(Name namespace, Triple triple) {
        this.slots.put(namespace, triple);
    }

    public void add(Triple triple) {
        this.slots.put(PLACEHOLDER_NS, triple);
    }

    /**
     * Checks whether this equivalence class sets two given graph elements in relation.
     * The host graph of these elements is known such that lookup can be performed efficiently.
     */
    public boolean relates(
            Name firstNamespace,
            Triple firstElement,
            Name secondNamespace,
            Triple secondElement) {
        return this.slots.get(firstNamespace).contains(firstElement) &&
                this.slots.get(secondNamespace).contains(secondElement);
    }

    /**
     * Checks whether this equivalence class sets two given graph elements in relation.
     * The host graph of these elements is not known.
     */
    public boolean relates(Triple firstElement, Triple secondElement) {
        return this.slots.entries().stream().anyMatch(kv -> kv.getValue().equals(firstElement)) &&
                this.slots.entries().stream().anyMatch(kv -> kv.getValue().equals(secondElement));
    }

    /**
     * Checks whether Equivalence class represents an equivalence on nodes.
     */
    public boolean isNodeClass() {
        return !isEdgeClass();
    }

    /**
     * Checks whether this equivalence class represents an equivalence on edges.
     */
    public boolean isEdgeClass() {
        return this.slots.entries().stream().allMatch(kv -> kv.getValue().isEddge());
    }

    public boolean verify() {
        return this.slots.entries().stream().anyMatch(kv -> kv.getValue().isNode()) &&
                this.slots.entries().stream().anyMatch(kv -> kv.getValue().isEddge());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquivalenceClass that = (EquivalenceClass) o;
        return Objects.equals(slots, that.slots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slots);
    }

//    public Triple representativeWithNSPrecedence(List<Name> nsHierarchy) {
//        Holder<Name> srcName = new Holder<>();
//        Holder<Name> lblName = new Holder<>();
//        Holder<Name> trgName = new Holder<>();
//        for (Name ns : nsHierarchy) {
//            if (!srcName.hasValue()) {
//                if (this.slots.containsKey(ns)) {
//                    Collection<Triple> triples = this.slots.get(ns);
//                    if (triples.size() == 1) {
//                        srcName.set(triples.iterator().next().getSource());
//                    } else {
//                        srcName.set(Name.merge(triples.stream().map(Triple::getSource).collect(Collectors.toSet())));
//                    }
//                }
//            }
//            if (!lblName.hasValue()) {
//                if (this.slots.containsKey(ns)) {
//                    Collection<Triple> triples = this.slots.get(ns);
//                    if (triples.size() == 1) {
//                        lblName.set(triples.iterator().next().getLabel());
//                    } else {
//                        lblName.set(Name.merge(triples.stream().map(Triple::getLabel).collect(Collectors.toSet())));
//                    }
//                }
//            }
//            if (!trgName.hasValue()) {
//                if (this.slots.containsKey(ns)) {
//                    Collection<Triple> triples = this.slots.get(ns);
//                    if (triples.size() == 1) {
//                        trgName.set(triples.iterator().next().getTarget());
//                    } else {
//                        trgName.set(Name.merge(triples.stream().map(Triple::getTarget).collect(Collectors.toSet())));
//                    }
//                }
//            }
//        }
//        if (srcName.hasValue() && lblName.hasValue() && trgName.hasValue()) {
//            return Triple.edge(srcName.unsafeGet(), lblName.unsafeGet(), trgName.unsafeGet());
//        } else {
//            return representative();
//        }
//    }
//
//    /**
//     * Turns this whole class into a single Triple.
//     * By default this is done by merging all names.
//     */
//    public Triple representative() {
//        Set<Name> sourceNames = new HashSet<>();
//        Set<Name> labelNames = new HashSet<>();
//        Set<Name> targetNames = new HashSet<>();
//        this.slots.entries().forEach(kv -> {
//            Triple triple;
//            if (kv.getKey().equals(PLACEHOLDER_NS)) {
//                triple = kv.getValue();
//            } else {
//                triple = kv.getValue().prefix(kv.getKey());
//            }
//            sourceNames.add(triple.getSource());
//            labelNames.add(triple.getLabel());
//            targetNames.add(triple.getTarget());
//        });
//        return new Triple(Name.merge(sourceNames), Name.merge(labelNames), Name.merge(targetNames));
//    }

//    /**
//     * Turns this whole class into a single Triple by merging the names
//     * but this time the sources are also represented by classes.
//     */
//    public Triple properRepresentative(Collection<EquivalenceClass> nodeClasses) {
//        Set<Name> labelNames = new HashSet<>();
//        Holder<Name> srcName = new Holder<>();
//        Holder<Name> trgName = new Holder<>();
//        this.slots.entries().forEach(kv -> {
//            Triple t = kv.getValue();
//            labelNames.add(t.getLabel().prefixWith(kv.getKey()));
//            if (!srcName.hasValue()) {
//                nodeClasses
//                        .stream()
//                        .filter(eqv -> eqv.containsNode(kv.getKey(), t.getSource()))
//                        .findFirst()
//                        .map(EquivalenceClass::representative)
//                        .map(Triple::getLabel)
//                        .ifPresent(srcName::set);
//            }
//            if (!trgName.hasValue()) {
//                nodeClasses
//                        .stream()
//                        .filter(eqv -> eqv.containsNode(kv.getKey(), t.getTarget()))
//                        .findFirst().map(EquivalenceClass::representative)
//                        .map(Triple::getLabel)
//                        .ifPresent(trgName::set);
//            }
//        });
//        if (!srcName.hasValue() || !trgName.hasValue()) {
//            return representative();
//        }
//        return new Triple(srcName.unsafeGet(), Name.merge(labelNames), trgName.unsafeGet());
//    }
//
//    public Triple properRepresentativeWithNSHierarch(Collection<EquivalenceClass> nodeClasses, List<Name> nsHierarchy) {
//        Set<Name> labelNames = new HashSet<>();
//        Holder<Name> srcName = new Holder<>();
//        Holder<Name> trgName = new Holder<>();
//        this.slots.entries().forEach(kv -> {
//            Triple t = kv.getValue();
//            labelNames.add(t.getLabel().prefixWith(kv.getKey()));
//            if (!srcName.hasValue()) {
//                nodeClasses
//                        .stream()
//                        .filter(eqv -> eqv.containsNode(kv.getKey(), t.getSource()))
//                        .findFirst()
//                        .map(eqv -> eqv.representativeWithNSPrecedence(nsHierarchy))
//                        .map(Triple::getLabel)
//                        .ifPresent(srcName::set);
//            }
//            if (!trgName.hasValue()) {
//                nodeClasses
//                        .stream()
//                        .filter(eqv -> eqv.containsNode(kv.getKey(), t.getTarget()))
//                        .findFirst()
//                        .map(eqv -> eqv.representativeWithNSPrecedence(nsHierarchy))
//                        .map(Triple::getLabel)
//                        .ifPresent(trgName::set);
//            }
//        });
//        if (!srcName.hasValue() || !trgName.hasValue()) {
//            return representativeWithNSPrecedence(nsHierarchy);
//        }
//        return new Triple(srcName.unsafeGet(), Name.merge(labelNames), trgName.unsafeGet());
//    }

    public static EquivalenceClass create(List<Name> slotNames, List<Triple> prefixedTriples) {
        Multimap<Name, Triple> multimap = HashMultimap.create();
        for (Name slotName : slotNames) {
            multimap.putAll(slotName, Collections.emptySet());
        }
        for (Triple triple : prefixedTriples) {
            Name prefix = triple.getPrefix().orElse(PLACEHOLDER_NS);
            multimap.put(prefix, triple.unprefixAll());
        }
        return new EquivalenceClass(multimap);
    }


}



