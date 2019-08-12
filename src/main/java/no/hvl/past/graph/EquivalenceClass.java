package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;
import no.hvl.past.graph.names.Prefix;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EquivalenceClass {

    private final Set<Triple> elements;

    public EquivalenceClass(Set<Triple> elements) {
        this.elements = elements;
    }

    public boolean overlaps(EquivalenceClass other) {
        if (other.equals(this)) {
            return false;
        }
        for (Triple tis : this.elements) {
            for (Triple oter : other.elements) {
                if (tis.equals(oter)) {
                    return true;
                }
            }
        }
        return false;
    }

    public EquivalenceClass merge(EquivalenceClass other) {
        HashSet<Triple> result = new HashSet<>();
        result.addAll(this.elements);
        result.addAll(other.elements);
        return new EquivalenceClass(result);
    }

    public boolean isNodeClass() {
        return this.elements.stream().allMatch(Triple::isNode);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquivalenceClass that = (EquivalenceClass) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    public Triple name(NamingStrategy strategy) {
        Name srcName = strategy.name(elements.stream().map(Triple::getSource).collect(Collectors.toList()));
        Name tgtName = strategy.name(elements.stream().map(Triple::getTarget).collect(Collectors.toList()));
        Name labName = strategy.name(elements.stream().map(Triple::getLabel).collect(Collectors.toList()));
        return new Triple(srcName, labName, tgtName);
    }

    public static EquivalenceClass fromTriple(Triple triple) {
        return new EquivalenceClass(Collections.singleton(triple));
    }

    public boolean containsLabelName(Name node) {
        return this.elements.stream().anyMatch(t -> t.getLabel().equals(node));
    }

    public boolean contains(Triple t) {
        return this.elements.contains(t);
    }
}
